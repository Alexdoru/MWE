package fr.alexdoru.nocheatersmod.commands;

import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedHypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedMojangUUID;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.gui.NoCheatersConfigGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.utils.*;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import fr.alexdoru.nocheatersmod.events.ReportQueue;
import fr.alexdoru.nocheatersmod.util.NoCheatersMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.*;

public class CommandNocheaters extends CommandBase {

    private static HashMap<String, WDR> sortedmap = new HashMap<>();
    private static final Minecraft mc = Minecraft.getMinecraft();

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
            NoCheatersMessages.printReportMessagesForWorld(true);
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("config")) {

            new DelayedTask(() -> mc.displayGuiScreen(new NoCheatersConfigGuiScreen()), 1);

        } else if (args[0].equalsIgnoreCase("reportlist") || args[0].equalsIgnoreCase("debugreportlist")) {

            boolean doStalk = args[0].equalsIgnoreCase("reportlist");

            if (doStalk) {
                if (HypixelApiKeyUtil.apiKeyIsNotSetup()) { //api key not setup
                    addChatMessage(new ChatComponentText(apikeyMissingErrorMsg()));
                    return;
                }
            }

            Multithreading.addTaskToQueue(() -> {

                int displaypage = 1;
                int nbreport = 1; // pour compter le nb de report et en afficher que 8 par page
                int nbpage = 1;

                if (args.length > 1) {
                    try {
                        displaypage = parseInt(args[1]);
                    } catch (NumberInvalidException e) {
                        addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Not a valid number"));
                        return null;
                    }
                }

                if (args.length == 1 || sortedmap.isEmpty()) {
                    HashMap<String, WDR> newmap = new HashMap<>(WdredPlayers.getWdredMap());
                    sortedmap = sortByValue(newmap);
                }

                IChatComponent imsgbody = new ChatComponentText("");
                boolean warning = true;
                long timeNow = (new Date()).getTime();
                List<Future<IChatComponent>> futureList = new ArrayList<>();

                for (Map.Entry<String, WDR> entry : sortedmap.entrySet()) {

                    String uuid = entry.getKey();
                    WDR wdr = entry.getValue();

                    if(wdr.isOnlyIgnored()) {
                        continue;
                    }

                    if (nbreport == 11) {
                        nbreport = 1;
                        nbpage++;
                    }

                    if (nbpage == displaypage) {
                        warning = false;
                        futureList.add(Multithreading.addTaskToQueueAndGetFuture(new CreateReportLineTask(uuid, wdr, doStalk, timeNow)));
                    }

                    nbreport++;

                }

                for (Future<IChatComponent> iChatComponentFuture : futureList) {
                    imsgbody.appendSibling(iChatComponentFuture.get());
                }

                if (warning) {
                    addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No reports to display, " + nbpage + " page" + (nbpage == 1 ? "" : "s") + " available."));
                } else {

                    IChatComponent imsg = new ChatComponentText(EnumChatFormatting.RED + ChatUtil.bar() + "\n" + "             ");
                    String command = doStalk ? getCommandUsage(null) + " reportlist" : getCommandUsage(null) + " debugreportlist";

                    if (displaypage > 1) {
                        imsg.appendSibling(new ChatComponentText("" + EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD + " <<")
                                .setChatStyle(new ChatStyle()
                                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to view page " + (displaypage - 1))))
                                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (displaypage - 1)))));
                    } else {
                        imsg.appendSibling(new ChatComponentText("   "));
                    }

                    imsg.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + " Report list (Page " + displaypage + " of " + nbpage + ")"));

                    if (displaypage < nbpage) {
                        imsg.appendSibling(new ChatComponentText("" + EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD + " >>")
                                .setChatStyle(new ChatStyle()
                                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to view page " + (displaypage + 1))))
                                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (displaypage + 1)))));
                    }

                    imsg.appendSibling(new ChatComponentText("\n"))
                            .appendSibling(imsgbody)
                            .appendSibling(new ChatComponentText(EnumChatFormatting.RED + ChatUtil.bar()));

                    addChatMessage(imsg);

                }

                return null;

            });

        } else if (args[0].equalsIgnoreCase("ignore")) {

            if (args.length == 1) {
                addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + getCommandUsage(sender) + " ignore <playername>"));
            } else {

                ReportQueue.INSTANCE.clearReportsSentBy(args[1]);

                Multithreading.addTaskToQueue(() -> {

                    CachedMojangUUID apireq;
                    String uuid = null;
                    String playername = args[1];
                    boolean isaNick = false;

                    try {
                        apireq = new CachedMojangUUID(playername);
                        uuid = apireq.getUuid();
                        playername = apireq.getName();
                        if (uuid != null && !HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                            CachedHypixelPlayerData playerdata;
                            try {
                                playerdata = new CachedHypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
                                LoginData loginData = new LoginData(playerdata.getPlayerData());
                                if (loginData.hasNeverJoinedHypixel()) {
                                    uuid = null;
                                } else if (!playername.equals(loginData.getdisplayname())) {
                                    uuid = null;
                                }
                            } catch (ApiException ignored) {
                                uuid = null;
                            }
                        }
                    } catch (ApiException ignored) {
                    }

                    if (uuid == null) {  // The playername doesn't exist or never joined hypixel

                        // search for the player's gameprofile in the tablist
                        for (NetworkPlayerInfo networkplayerinfo : mc.getNetHandler().getPlayerInfoMap()) {
                            if (networkplayerinfo.getGameProfile().getName().equalsIgnoreCase(args[0])) {
                                uuid = networkplayerinfo.getGameProfile().getName();
                                playername = uuid;
                                isaNick = true;
                            }
                        }

                        if (!isaNick) { // couldn't find the nicked player in the tab list
                            addChatMessage(new ChatComponentText(getTagNoCheaters()
                                    + invalidplayernameMsg(args[0]) + EnumChatFormatting.RED + " Couldn't find the " + EnumChatFormatting.DARK_PURPLE + "nicked" + EnumChatFormatting.RED + " player in the tablist"));
                            return null;
                        }

                    }

                    WDR wdr = WdredPlayers.getWdredMap().get(uuid);

                    if (wdr != null) { // the player was already reported before

                        if (!wdr.isIgnored()) {
                            wdr.hacks.add(WDR.IGNORED);
                        }

                    } else { // the player wasn't reported before
                        long time = (new Date()).getTime();
                        ArrayList<String> hacks = new ArrayList<>();
                        hacks.add(WDR.IGNORED);
                        WdredPlayers.getWdredMap().put(uuid, new WDR(time, time, hacks));
                    }

                    addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "You added " + EnumChatFormatting.RED + playername
                            + EnumChatFormatting.GREEN + " to your ignore list, it will ignore all report suggestions from that player. Use : "
                            + EnumChatFormatting.YELLOW + getCommandUsage(sender) + " ignorelist" + EnumChatFormatting.GREEN + " to list and remove poeple from the list."));
                    return null;

                });

            }

        } else if (args[0].equalsIgnoreCase("ignorelist")) {

            if (HypixelApiKeyUtil.apiKeyIsNotSetup()) { //api key not setup
                addChatMessage(new ChatComponentText(apikeyMissingErrorMsg()));
                return;
            }

            Multithreading.addTaskToQueue(() -> {

                int displaypage = 1;
                int nbreport = 1; // pour compter le nb de report et en afficher que 8 par page
                int nbpage = 1;

                if (args.length > 1) {
                    try {
                        displaypage = parseInt(args[1]);
                    } catch (NumberInvalidException e) {
                        addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Not a valid number"));
                        return null;
                    }
                }

                IChatComponent imsgbody = new ChatComponentText("");
                boolean warning = true;
                List<Future<IChatComponent>> futureList = new ArrayList<>();

                for (Map.Entry<String, WDR> entry : WdredPlayers.getWdredMap().entrySet()) {

                    if (entry.getValue().isIgnored()) {

                        String uuid = entry.getKey();
                        WDR wdr = entry.getValue();

                        if (nbreport == 11) {
                            nbreport = 1;
                            nbpage++;
                        }

                        if (nbpage == displaypage) {
                            futureList.add(Multithreading.addTaskToQueueAndGetFuture(new IgnoreLineTask(uuid, wdr)));
                            warning = false;
                        }

                        nbreport++;

                    }

                }

                for (Future<IChatComponent> iChatComponentFuture : futureList) {
                    imsgbody.appendSibling(iChatComponentFuture.get());
                }

                if (warning && nbreport == 1) {
                    addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "No one in your ignore list"));
                } else if (warning) {
                    addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "No ignored players to display, " + nbpage + " page" + (nbpage == 1 ? "" : "s") + " available."));
                } else {

                    IChatComponent imsg = new ChatComponentText(EnumChatFormatting.DARK_GRAY + ChatUtil.bar() + "\n" + "     ");
                    String command = getCommandUsage(null) + " ignorelist";

                    if (displaypage > 1) {
                        imsg.appendSibling(new ChatComponentText("" + EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD + " <<")
                                .setChatStyle(new ChatStyle()
                                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to view page " + (displaypage - 1))))
                                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (displaypage - 1)))));
                    } else {
                        imsg.appendSibling(new ChatComponentText("   "));
                    }

                    imsg.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + " Ignore list (Page " + displaypage + " of " + nbpage + ") " + EnumChatFormatting.GOLD + "Click a name to un-ignore"));

                    if (displaypage < nbpage) {
                        imsg.appendSibling(new ChatComponentText("" + EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD + " >>")
                                .setChatStyle(new ChatStyle()
                                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to view page " + (displaypage + 1))))
                                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (displaypage + 1)))));
                    }

                    imsg.appendSibling(new ChatComponentText("\n"))
                            .appendSibling(imsgbody)
                            .appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GRAY + ChatUtil.bar()));

                    addChatMessage(imsg);

                }

                return null;

            });

        } else if (args[0].equalsIgnoreCase("ignoreremove")) {

            if (args.length == 3) {

                String uuid = args[1];
                String playername = args[2];
                WDR wdr = WdredPlayers.getWdredMap().get(uuid);

                if (wdr == null) {

                    addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GOLD + playername + EnumChatFormatting.RED + " wasn't found in your ignore list"));

                } else {

                    if (wdr.isOnlyIgnored()) {
                        WdredPlayers.getWdredMap().remove(uuid);
                    } else {
                        wdr.hacks.remove(WDR.IGNORED);
                    }

                    addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GOLD + playername
                            + EnumChatFormatting.GREEN + " has been removed from your ignore list, you will now receive report suggestions from that player."));

                }

            } else {

                addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "This shouldn't happen"));

            }

        } else if (args[0].equalsIgnoreCase("clearreportqueue")) {

            ReportQueue.INSTANCE.clearSuggestionsInReportQueue();

        } else if (args[0].equalsIgnoreCase("cancelreport")) {

            if (args.length == 2) {
                ReportQueue.INSTANCE.clearReportsFor(args[1]);
            }

        } else {

            addChatMessage(getCommandHelp());

        }

    }

    private IChatComponent getCommandHelp() {

        return new ChatComponentText(EnumChatFormatting.RED + bar() + "\n"
                + centerLine(EnumChatFormatting.GOLD + "NoCheaters Help\n\n")
                + EnumChatFormatting.YELLOW + getCommandUsage(null) + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "prints the list of reported players in your current world\n"
                + EnumChatFormatting.YELLOW + getCommandUsage(null) + " config" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "opens the config gui\n"
                + EnumChatFormatting.YELLOW + getCommandUsage(null) + " ignore <player>" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "ignores all future report suggestions from that player\n"
                + EnumChatFormatting.YELLOW + getCommandUsage(null) + " ignorelist" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "prints the list of ignored players\n"
                + EnumChatFormatting.YELLOW + getCommandUsage(null) + " reportlist" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "prints the list of reported players\n"
                + EnumChatFormatting.YELLOW + getCommandUsage(null) + " clearreportqueue" + EnumChatFormatting.GRAY + " - " + EnumChatFormatting.AQUA + "cancels all reports suggestions about to be sent\n"
                + EnumChatFormatting.RED + bar()
        );

    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        String[] arguments = {"clearreportqueue", "config", "help", "ignore", "ignorelist", "reportlist"};
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, arguments);
        }
        if (args.length == 2 && args[0].equals("ignore")) {
            return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
        }
        return null;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    /**
     * Returns a sorted hashmap of reports from high to low
     */
    private static HashMap<String, WDR> sortByValue(HashMap<String, WDR> hashmapIn) {
        List<Map.Entry<String, WDR>> list = new LinkedList<>(hashmapIn.entrySet());
        list.sort((o1, o2) -> (o1.getValue()).compareToInvert(o2.getValue()));
        HashMap<String, WDR> temp = new LinkedHashMap<>();
        for (Map.Entry<String, WDR> aa : list) {
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

            IChatComponent imsg;

            if (doStalk) {

                if (wdr.isNicked()) {

                    imsg = NoCheatersMessages.createPlayerNameWithHoverText(EnumChatFormatting.DARK_PURPLE + "[Nick] " + EnumChatFormatting.GOLD + uuid, uuid, uuid, wdr, EnumChatFormatting.WHITE)[0];

                } else {

                    HypixelPlayerData playerdata = new HypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
                    LoginData logindata = new LoginData(playerdata.getPlayerData());
                    imsg = NoCheatersMessages.createPlayerNameWithHoverText(logindata.getFormattedName(), logindata.getdisplayname(), uuid, wdr, EnumChatFormatting.WHITE)[0];

                    IChatComponent ismgStatus = new ChatComponentText("");

                    if (logindata.isHidingFromAPI()) {

                        logindata.parseLatestActivity(playerdata.getPlayerData());
                        long latestActivityTime = logindata.getLatestActivityTime();
                        String latestActivity = logindata.getLatestActivity();
                        boolean isProbBanned = isProbBanned(latestActivityTime);
                        ismgStatus.appendSibling(new ChatComponentText(EnumChatFormatting.GRAY + " " + latestActivity + " " + (isProbBanned ? EnumChatFormatting.DARK_GRAY : EnumChatFormatting.YELLOW) + DateUtil.timeSince(latestActivityTime)));

                    } else if (logindata.isOnline()) { // player is online

                        ismgStatus.appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + "    Online"));

                    } else { // print lastlogout

                        boolean isProbBanned = isProbBanned(logindata.getLastLogout());
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
                HypixelPlayerData playerdata = new HypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
                LoginData logindata = new LoginData(playerdata.getPlayerData());
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
