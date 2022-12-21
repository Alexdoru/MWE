package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedHypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangPlayernameToUUID;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.chat.ReportSuggestionHandler;
import fr.alexdoru.megawallsenhancementsmod.chat.WarningMessagesHandler;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.data.StringLong;
import fr.alexdoru.megawallsenhancementsmod.data.WDR;
import fr.alexdoru.megawallsenhancementsmod.data.WdrData;
import fr.alexdoru.megawallsenhancementsmod.gui.guiscreens.NoCheatersConfigGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportQueue;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardUtils;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.utils.MultithreadingUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class CommandNocheaters extends MyAbstractCommand {

    private static HashMap<String, WDR> sortedmap = new HashMap<>();

    @Override
    public String getCommandName() {
        return "nocheaters";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/nocheaters";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length == 0) {
            WarningMessagesHandler.printReportMessagesForWorld(true);
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("config")) {

            new DelayedTask(() -> mc.displayGuiScreen(new NoCheatersConfigGuiScreen()), 1);

        } else if (args[0].equalsIgnoreCase("reportlist") || args[0].equalsIgnoreCase("debugreportlist")) {

            final boolean doStalk = args[0].equalsIgnoreCase("reportlist");

            if (doStalk) {
                if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                    ChatUtil.printApikeySetupInfo();
                    return;
                }
            }

            MultithreadingUtil.addTaskToQueue(() -> {

                int displaypage = 1;
                int nbreport = 1; // pour compter le nb de report et en afficher que 8 par page
                int nbpage = 1;

                if (args.length > 1) {
                    try {
                        displaypage = parseInt(args[1]);
                    } catch (NumberInvalidException e) {
                        ChatUtil.addChatMessage(EnumChatFormatting.RED + "Not a valid number");
                        return null;
                    }
                }

                if (args.length == 1 || sortedmap.isEmpty()) {
                    final HashMap<String, WDR> newmap = new HashMap<>(WdrData.getWdredMap());
                    sortedmap = sortByValue(newmap);
                }

                final IChatComponent imsgbody = new ChatComponentText("");
                boolean warning = true;
                final long timeNow = (new Date()).getTime();
                final List<Future<IChatComponent>> futureList = new ArrayList<>();

                for (final Map.Entry<String, WDR> entry : sortedmap.entrySet()) {

                    final String uuid = entry.getKey();
                    final WDR wdr = entry.getValue();

                    if (wdr.isOnlyIgnored()) {
                        continue;
                    }

                    if (nbreport == 11) {
                        nbreport = 1;
                        nbpage++;
                    }

                    if (nbpage == displaypage) {
                        warning = false;
                        futureList.add(MultithreadingUtil.addTaskToQueueAndGetFuture(new CreateReportLineTask(uuid, wdr, doStalk, timeNow)));
                    }

                    nbreport++;

                }

                for (final Future<IChatComponent> iChatComponentFuture : futureList) {
                    imsgbody.appendSibling(iChatComponentFuture.get());
                }

                if (warning) {
                    ChatUtil.addChatMessage(EnumChatFormatting.RED + "No reports to display, " + nbpage + " page" + (nbpage == 1 ? "" : "s") + " available.");
                } else {
                    ChatUtil.printIChatList(
                            "Report list",
                            imsgbody,
                            displaypage,
                            nbpage,
                            doStalk ? getCommandUsage(null) + " reportlist" : getCommandUsage(null) + " debugreportlist",
                            EnumChatFormatting.RED,
                            null,
                            null
                    );
                }

                return null;

            });

        } else if (args[0].equalsIgnoreCase("ignore")) {

            if (args.length == 1) {
                ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + getCommandUsage(sender) + " ignore <playername>");
            } else {

                ReportQueue.INSTANCE.clearReportsSentBy(args[1]);

                MultithreadingUtil.addTaskToQueue(() -> {

                    String uuid = null;
                    String playername = args[1];
                    boolean isaNick = false;

                    try {
                        final MojangPlayernameToUUID apireq = new MojangPlayernameToUUID(playername);
                        uuid = apireq.getUuid();
                        playername = apireq.getName();
                        if (uuid != null && !HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                            final CachedHypixelPlayerData playerdata;
                            try {
                                playerdata = new CachedHypixelPlayerData(uuid);
                                final LoginData loginData = new LoginData(playerdata.getPlayerData());
                                if (loginData.hasNeverJoinedHypixel()) {
                                    uuid = null;
                                } else if (!playername.equals(loginData.getdisplayname())) {
                                    uuid = null;
                                }
                            } catch (ApiException e) {
                                uuid = null;
                            }
                        }
                    } catch (ApiException ignored) {}

                    if (uuid == null) {  // The playername doesn't exist or never joined hypixel

                        // search for the player's gameprofile in the tablist
                        for (final NetworkPlayerInfo networkplayerinfo : mc.getNetHandler().getPlayerInfoMap()) {
                            if (networkplayerinfo.getGameProfile().getName().equalsIgnoreCase(args[0])) {
                                uuid = networkplayerinfo.getGameProfile().getName();
                                playername = uuid;
                                isaNick = true;
                            }
                        }

                        if (!isaNick) { // couldn't find the nicked player in the tab list
                            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + ChatUtil.invalidPlayernameMsg(args[0]) + EnumChatFormatting.RED + " Couldn't find the " + EnumChatFormatting.DARK_PURPLE + "nicked" + EnumChatFormatting.RED + " player in the tablist");
                            return null;
                        }

                    }

                    final WDR wdr = WdrData.getWdr(uuid);

                    if (wdr != null) { // the player was already reported before

                        if (!wdr.isIgnored()) {
                            wdr.hacks.add(WDR.IGNORED);
                        }

                    } else { // the player wasn't reported before
                        final long time = (new Date()).getTime();
                        final ArrayList<String> hacks = new ArrayList<>();
                        hacks.add(WDR.IGNORED);
                        WdrData.put(uuid, new WDR(time, time, hacks));
                    }

                    ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "You added " + EnumChatFormatting.RED + playername
                            + EnumChatFormatting.GREEN + " to your ignore list, it will ignore all report suggestions from that player. Use : "
                            + EnumChatFormatting.YELLOW + getCommandUsage(sender) + " ignorelist" + EnumChatFormatting.GREEN + " to list and remove poeple from the list.");
                    return null;

                });

            }

        } else if (args[0].equalsIgnoreCase("ignorelist")) {

            if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                ChatUtil.printApikeySetupInfo();
                return;
            }

            MultithreadingUtil.addTaskToQueue(() -> {

                int displaypage = 1;
                int nbreport = 1; // pour compter le nb de report et en afficher que 8 par page
                int nbpage = 1;

                if (args.length > 1) {
                    try {
                        displaypage = parseInt(args[1]);
                    } catch (NumberInvalidException e) {
                        ChatUtil.addChatMessage(EnumChatFormatting.RED + "Not a valid number");
                        return null;
                    }
                }

                final IChatComponent imsgbody = new ChatComponentText("");
                boolean warning = true;
                final List<Future<IChatComponent>> futureList = new ArrayList<>();

                for (final Map.Entry<String, WDR> entry : WdrData.getWdredMap().entrySet()) {

                    if (entry.getValue().isIgnored()) {

                        final String uuid = entry.getKey();
                        final WDR wdr = entry.getValue();

                        if (nbreport == 11) {
                            nbreport = 1;
                            nbpage++;
                        }

                        if (nbpage == displaypage) {
                            futureList.add(MultithreadingUtil.addTaskToQueueAndGetFuture(new IgnoreLineTask(uuid, wdr)));
                            warning = false;
                        }

                        nbreport++;

                    }

                }

                for (final Future<IChatComponent> iChatComponentFuture : futureList) {
                    imsgbody.appendSibling(iChatComponentFuture.get());
                }

                if (warning && nbreport == 1) {
                    ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "No one in your ignore list");
                } else if (warning) {
                    ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "No ignored players to display, " + nbpage + " page" + (nbpage == 1 ? "" : "s") + " available.");
                } else {
                    ChatUtil.printIChatList(
                            "Ignore list",
                            imsgbody,
                            displaypage,
                            nbpage,
                            getCommandUsage(null) + " ignorelist",
                            EnumChatFormatting.DARK_GRAY,
                            null,
                            null
                    );
                }

                return null;

            });

        } else if (args[0].equalsIgnoreCase("ignoreremove")) {

            if (args.length == 3) {

                final String uuid = args[1];
                final String playername = args[2];
                final WDR wdr = WdrData.getWdr(uuid);

                if (wdr == null) {

                    ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GOLD + playername + EnumChatFormatting.RED + " wasn't found in your ignore list");

                } else {

                    if (wdr.isOnlyIgnored()) {
                        WdrData.remove(uuid);
                    } else {
                        wdr.hacks.remove(WDR.IGNORED);
                    }

                    ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GOLD + playername
                            + EnumChatFormatting.GREEN + " has been removed from your ignore list, you will now receive report suggestions from that player.");

                }

            } else {

                ChatUtil.addChatMessage(EnumChatFormatting.DARK_RED + "This shouldn't happen");

            }

        } else if (args[0].equalsIgnoreCase("clearreportqueue")) {

            ReportQueue.INSTANCE.clearSuggestionsInReportQueue();

        } else if (args[0].equalsIgnoreCase("cancelreport")) {

            if (args.length == 2) {
                ReportQueue.INSTANCE.clearReportsFor(args[1]);
            }

        } else if (args[0].equalsIgnoreCase("debug")) {

            ConfigHandler.isDebugMode = !ConfigHandler.isDebugMode;
            if (ConfigHandler.isDebugMode) {
                ChatUtil.debug("Enabled debug mode");
            } else {
                ChatUtil.debug("Disabled debug mode");
            }

        } else if (args[0].equalsIgnoreCase("getscoreboard")) {

            ScoreboardUtils.debugGetScoreboard();

        } else if (args[0].equalsIgnoreCase("autoreporthistory")) {

            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ChatUtil.getTagNoCheaters());
            stringBuilder.append(EnumChatFormatting.GREEN);
            stringBuilder.append("Players reported this game : ");
            stringBuilder.append(EnumChatFormatting.GOLD);
            for (final StringLong stringLong : ReportSuggestionHandler.getReportSuggestionHistory()) {
                if (stringLong.message != null) {
                    stringBuilder.append(stringLong.message).append(" ");
                }
            }
            ChatUtil.addChatMessage(stringBuilder.toString());

        } else {

            ChatUtil.addChatMessage(getCommandHelp());

        }

    }

    private IChatComponent getCommandHelp() {

        return new ChatComponentText(EnumChatFormatting.RED + ChatUtil.bar() + "\n"
                + ChatUtil.centerLine(EnumChatFormatting.GOLD + "NoCheaters Help\n\n")
                + EnumChatFormatting.YELLOW + getCommandUsage(null) + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "prints the list of reported players in your current world\n"
                + EnumChatFormatting.YELLOW + getCommandUsage(null) + " config" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "opens the config gui\n"
                + EnumChatFormatting.YELLOW + getCommandUsage(null) + " ignore <player>" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "ignores all future report suggestions from that player\n"
                + EnumChatFormatting.YELLOW + getCommandUsage(null) + " ignorelist" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "prints the list of ignored players\n"
                + EnumChatFormatting.YELLOW + getCommandUsage(null) + " reportlist" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "prints the list of reported players\n"
                + EnumChatFormatting.YELLOW + getCommandUsage(null) + " clearreportqueue" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "cancels all reports suggestions about to be sent\n"
                + EnumChatFormatting.YELLOW + getCommandUsage(null) + " autoreporthistory" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "prints all players reported during the ongoing game\n"
                + EnumChatFormatting.RED + ChatUtil.bar()
        );

    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        final String[] arguments = {"autoreporthistory", "clearreportqueue", "config", "help", "ignore", "ignorelist", "reportlist"};
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, arguments);
        }
        if (args.length == 2 && args[0].equals("ignore")) {
            return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
        }
        return null;
    }

    /**
     * Returns a sorted hashmap of reports from high to low
     */
    private static HashMap<String, WDR> sortByValue(HashMap<String, WDR> hashmapIn) {
        final List<Map.Entry<String, WDR>> list = new LinkedList<>(hashmapIn.entrySet());
        list.sort((o1, o2) -> (o1.getValue()).compareToInvert(o2.getValue()));
        final HashMap<String, WDR> temp = new LinkedHashMap<>();
        for (final Map.Entry<String, WDR> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

}

class CreateReportLineTask implements Callable<IChatComponent> {

    private final String uuid;
    private final WDR wdr;
    private final boolean doStalk;
    private final long timeNow;

    public CreateReportLineTask(String uuid, WDR wdr, boolean doStalk, long timeNow) {
        this.uuid = uuid;
        this.wdr = wdr;
        this.doStalk = doStalk;
        this.timeNow = timeNow;
    }

    @Override
    public IChatComponent call() {

        try {

            final IChatComponent imsg;

            if (doStalk) {

                if (wdr.isNicked()) {

                    imsg = WarningMessagesHandler.createPlayerNameWithHoverText(EnumChatFormatting.DARK_PURPLE + "[Nick] " + EnumChatFormatting.GOLD + uuid, uuid, uuid, wdr, EnumChatFormatting.WHITE)[0];

                } else {

                    final HypixelPlayerData playerdata = new HypixelPlayerData(uuid);
                    final LoginData logindata = new LoginData(playerdata.getPlayerData());
                    imsg = WarningMessagesHandler.createPlayerNameWithHoverText(logindata.getFormattedName(), logindata.getdisplayname(), uuid, wdr, EnumChatFormatting.WHITE)[0];

                    final IChatComponent ismgStatus = new ChatComponentText("");

                    if (logindata.isHidingFromAPI()) {

                        logindata.parseLatestActivity(playerdata.getPlayerData());
                        final long latestActivityTime = logindata.getLatestActivityTime();
                        final String latestActivity = logindata.getLatestActivity();
                        final boolean isProbBanned = isProbBanned(latestActivityTime);
                        ismgStatus.appendSibling(new ChatComponentText(EnumChatFormatting.GRAY + " " + latestActivity + " " + (isProbBanned ? EnumChatFormatting.DARK_GRAY : EnumChatFormatting.YELLOW) + DateUtil.timeSince(latestActivityTime)));

                    } else if (logindata.isOnline()) { // player is online

                        ismgStatus.appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + "    Online"));

                    } else { // print lastlogout

                        final boolean isProbBanned = isProbBanned(logindata.getLastLogout());
                        ismgStatus.appendSibling(new ChatComponentText(EnumChatFormatting.GRAY + " Lastlogout " + (isProbBanned ? EnumChatFormatting.DARK_GRAY : EnumChatFormatting.YELLOW) + DateUtil.timeSince(logindata.getLastLogout())));

                    }

                    ismgStatus.setChatStyle(new ChatStyle()
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to run : /stalk " + logindata.getdisplayname())))
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stalk " + logindata.getdisplayname())));

                    imsg.appendSibling(ismgStatus);

                }

            } else {

                imsg = new ChatComponentText(EnumChatFormatting.RED + uuid + EnumChatFormatting.GRAY + " reported : " + EnumChatFormatting.YELLOW + DateUtil.timeSince(wdr.timestamp));

            }

            return imsg.appendSibling(new ChatComponentText("\n"));

        } catch (ApiException e) {
            return new ChatComponentText(EnumChatFormatting.RED + e.getMessage() + "\n");
        }

    }

    private boolean isProbBanned(long latestActivityTime) {
        return (Math.abs(latestActivityTime - wdr.timestamp) < 24L * 60L * 60L * 1000L && Math.abs(timeNow - wdr.timestamp) > 3L * 24L * 60L * 60L * 1000L) || Math.abs(timeNow - latestActivityTime) > 14L * 24L * 60L * 60L * 1000L;
    }

}

class IgnoreLineTask implements Callable<IChatComponent> {

    private final String uuid;
    private final WDR wdr;

    public IgnoreLineTask(String uuid, WDR wdr) {
        this.uuid = uuid;
        this.wdr = wdr;
    }

    @Override
    public IChatComponent call() {

        try {

            final String formattedName;
            if (wdr.isNicked()) {
                formattedName = EnumChatFormatting.GOLD + "uuid";
                return new ChatComponentText(formattedName + "\n").setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to un-ignore " + formattedName)))
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nocheaters ignoreremove " + uuid + " " + uuid)));
            } else {
                final HypixelPlayerData playerdata = new HypixelPlayerData(uuid);
                final LoginData logindata = new LoginData(playerdata.getPlayerData());
                formattedName = logindata.getFormattedName();
                return new ChatComponentText(formattedName + "\n").setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to un-ignore " + formattedName)))
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nocheaters ignoreremove " + uuid + " " + logindata.getdisplayname())));
            }

        } catch (ApiException e) {
            return new ChatComponentText(EnumChatFormatting.RED + e.getMessage() + "\n");
        }

    }

}
