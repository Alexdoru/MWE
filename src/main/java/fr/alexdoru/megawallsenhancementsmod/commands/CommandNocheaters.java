package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedHypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangPlayernameToUUID;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.chat.ReportSuggestionHandler;
import fr.alexdoru.megawallsenhancementsmod.chat.WarningMessagesHandler;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.data.StringLong;
import fr.alexdoru.megawallsenhancementsmod.data.WDR;
import fr.alexdoru.megawallsenhancementsmod.data.WdrData;
import fr.alexdoru.megawallsenhancementsmod.gui.guiscreens.NoCheatersConfigGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportQueue;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardUtils;
import fr.alexdoru.megawallsenhancementsmod.utils.*;
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

    @Override
    public String getCommandName() {
        return "nocheaters";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length == 0) {
            WarningMessagesHandler.printReportMessagesForWorld(true);
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("config")) {

            new DelayedTask(() -> mc.displayGuiScreen(new NoCheatersConfigGuiScreen(null)));

        } else if (args[0].equalsIgnoreCase("reportlist") || args[0].equalsIgnoreCase("debugreportlist")) {

            printReportList(args);

        } else if (args[0].equalsIgnoreCase("ignore")) {

            addPlayerToIgnoreList(args);

        } else if (args[0].equalsIgnoreCase("ignorelist")) {

            printIgnoreList(args);

        } else if (args[0].equalsIgnoreCase("ignoreremove")) {

            removePlayerFromIgnoreList(args);

        } else if (args[0].equalsIgnoreCase("clearreportqueue")) {

            ReportQueue.INSTANCE.clearSuggestionsInReportQueue();

        } else if (args[0].equalsIgnoreCase("cancelreport")) {

            if (args.length == 2) {
                ReportQueue.INSTANCE.clearReportsFor(args[1]);
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

        } else if (args[0].equalsIgnoreCase("log")) {

            this.logPlayer(args);

        } else {

            this.printCommandHelp();

        }

    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        final String[] arguments = {"autoreporthistory", "clearreportqueue", "config", "help", "ignore", "ignorelist", "reportlist"};
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, arguments);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("ignore") || args.length >= 2 && args[0].equalsIgnoreCase("log")) {
            return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
        }
        return null;
    }

    @Override
    protected void printCommandHelp() {
        ChatUtil.addChatMessage(
                EnumChatFormatting.RED + ChatUtil.bar() + "\n"
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

    private void printReportList(String[] args) {

        final boolean doStalk = args[0].equalsIgnoreCase("reportlist");
        if (doStalk) {
            if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                ChatUtil.printApikeySetupInfo();
                return;
            }
        }

        final int displaypage;
        if (args.length > 1) {
            try {
                displaypage = parseInt(args[1]);
            } catch (NumberInvalidException e) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "Not a valid page number");
                return;
            }
        } else {
            displaypage = 1;
        }

        final Map<String, WDR> sortedMap = MapUtil.sortByDecreasingValue(WdrData.getWdredMap());
        final long timeNow = (new Date()).getTime();
        final List<Future<IChatComponent>> futureList = new ArrayList<>();
        int nbreport = 1; // pour compter le nb de report et en afficher que 8 par page
        int nbpage = 1;
        boolean warning = true;

        for (final Map.Entry<String, WDR> entry : sortedMap.entrySet()) {
            if (entry.getValue().isOnlyIgnored()) {
                continue;
            }
            if (nbreport == 11) {
                nbreport = 1;
                nbpage++;
            }
            if (nbpage == displaypage) {
                warning = false;
                futureList.add(MultithreadingUtil.addTaskToQueue(new CreateReportLineTask(entry.getKey(), entry.getValue(), doStalk, timeNow)));
            }
            nbreport++;
        }

        if (warning) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "No reports to display, " + nbpage + " page" + (nbpage == 1 ? "" : "s") + " available.");
            return;
        }

        final int finalNbpage = nbpage;
        MultithreadingUtil.addTaskToQueue(() -> {
            final IChatComponent imsgbody = new ChatComponentText("");
            for (final Future<IChatComponent> iChatComponentFuture : futureList) {
                imsgbody.appendSibling(iChatComponentFuture.get());
            }
            ChatUtil.printIChatList(
                    "Report list",
                    imsgbody,
                    displaypage,
                    finalNbpage,
                    doStalk ? getCommandUsage(null) + " reportlist" : getCommandUsage(null) + " debugreportlist",
                    EnumChatFormatting.RED,
                    null,
                    null
            );
            return null;
        });
    }

    private void printIgnoreList(String[] args) {

        if (HypixelApiKeyUtil.apiKeyIsNotSetup()) {
            ChatUtil.printApikeySetupInfo();
            return;
        }

        final int displaypage;
        if (args.length > 1) {
            try {
                displaypage = parseInt(args[1]);
            } catch (NumberInvalidException e) {
                ChatUtil.addChatMessage(EnumChatFormatting.RED + "Not a valid number");
                return;
            }
        } else {
            displaypage = 1;
        }

        boolean warning = true;
        final List<Future<IChatComponent>> futureList = new ArrayList<>();
        int nbreport = 1; // pour compter le nb de report et en afficher que 8 par page
        int nbpage = 1;

        for (final Map.Entry<String, WDR> entry : WdrData.getWdredMap().entrySet()) {
            if (entry.getValue().isIgnored()) {
                if (nbreport == 11) {
                    nbreport = 1;
                    nbpage++;
                }
                if (nbpage == displaypage) {
                    futureList.add(MultithreadingUtil.addTaskToQueue(new IgnoreLineTask(entry.getKey(), entry.getValue().isNicked())));
                    warning = false;
                }
                nbreport++;
            }
        }

        if (warning && nbreport == 1) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "No one in your ignore list");
            return;
        }
        if (warning) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "No ignored players to display, "
                    + nbpage + " page" + (nbpage == 1 ? "" : "s") + " available.");
            return;
        }

        final int finalNbpage = nbpage;
        MultithreadingUtil.addTaskToQueue(() -> {
            final IChatComponent imsgbody = new ChatComponentText("");
            for (final Future<IChatComponent> iChatComponentFuture : futureList) {
                imsgbody.appendSibling(iChatComponentFuture.get());
            }
            ChatUtil.printIChatList(
                    "Ignore list",
                    imsgbody,
                    displaypage,
                    finalNbpage,
                    getCommandUsage(null) + " ignorelist",
                    EnumChatFormatting.DARK_GRAY,
                    null,
                    null
            );
            return null;
        });
    }

    private void removePlayerFromIgnoreList(String[] args) {
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
                    wdr.hacks.trimToSize();
                }
                ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GOLD + playername
                        + EnumChatFormatting.GREEN + " has been removed from your ignore list, you will now receive report suggestions from that player.");
            }
        } else {
            ChatUtil.addChatMessage(EnumChatFormatting.DARK_RED + "This shouldn't happen");
        }
    }

    private void logPlayer(String[] args) {
        if (ASMLoadingPlugin.isObf && !mc.thePlayer.getUniqueID().equals(UUID.fromString("57715d32-a685-4e2e-ae68-54c19808b58d"))) {
            return;
        }
        for (int i = 1, argsLength = args.length; i < argsLength; i++) {
            String name = args[i];
            if (ScoreboardTracker.isReplayMode) {
                for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
                    if (netInfo.getGameProfile().getName().contains(name)) {
                        // in replay mode for some reason the names
                        // of players contains color codes :
                        // ex : Playername§r
                        name = netInfo.getGameProfile().getName();
                    }
                }
            }
            if (HackerDetector.INSTANCE.playersToLog.remove(name)) {
                ChatUtil.debug("Removed " + name + " players to log");
            } else {
                HackerDetector.INSTANCE.playersToLog.add(name);
                ChatUtil.debug("Added " + name + " to players to log");
            }
        }
        ConfigHandler.debugLogging = !HackerDetector.INSTANCE.playersToLog.isEmpty();
        ConfigHandler.saveConfig();
        if (ConfigHandler.debugLogging) {
            ChatUtil.debug("Enabled debug logging for " + HackerDetector.INSTANCE.playersToLog);
        } else {
            ChatUtil.debug("Turned off debug logging");
        }
    }

    private void addPlayerToIgnoreList(String[] args) {

        if (args.length == 1) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + getCommandUsage(null) + " ignore <playername>");
            return;
        }

        final String playername = args[1];
        ReportQueue.INSTANCE.clearReportsSentBy(playername);

        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (netInfo.getGameProfile().getName().equalsIgnoreCase(playername)) {
                if (netInfo.getGameProfile().getId().version() == 4) {
                    this.addPlayerToIgnoreList(netInfo.getGameProfile().getId().toString().replace("-", ""), playername);
                } else if (netInfo.getGameProfile().getId().version() == 1) {
                    this.addPlayerToIgnoreList(playername, playername);
                }
            }
        }

        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                final MojangPlayernameToUUID mojangReq = new MojangPlayernameToUUID(playername);
                if (!HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                    try {
                        final LoginData loginData = new LoginData(CachedHypixelPlayerData.getPlayerData(mojangReq.getUuid()));
                        if (!loginData.hasNeverJoinedHypixel() && mojangReq.getName().equals(loginData.getdisplayname())) {
                            mc.addScheduledTask(() -> this.addPlayerToIgnoreList(mojangReq.getUuid(), mojangReq.getName()));
                            return null;
                        }
                    } catch (ApiException ignored) {}
                }
            } catch (ApiException ignored) {}
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + ChatUtil.inexistantMinecraftNameMsg(playername)
                    + EnumChatFormatting.RED + " Couldn't find the " + EnumChatFormatting.DARK_PURPLE + "nicked" + EnumChatFormatting.RED + " player in the tablist");
            return null;
        });

    }

    private void addPlayerToIgnoreList(String uuid, String playername) {
        final WDR wdr = WdrData.getWdr(uuid);
        if (wdr != null) { // the player was already reported before
            if (!wdr.isIgnored()) {
                wdr.hacks.add(WDR.IGNORED);
                wdr.hacks.trimToSize();
            }
        } else { // the player wasn't reported before
            final long time = (new Date()).getTime();
            final ArrayList<String> hacks = new ArrayList<>();
            hacks.add(WDR.IGNORED);
            WdrData.put(uuid, new WDR(time, time, hacks));
        }
        ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "You added " + EnumChatFormatting.RED + playername
                + EnumChatFormatting.GREEN + " to your ignore list, it will ignore all report suggestions from that player. Use : "
                + EnumChatFormatting.YELLOW + getCommandUsage(null) + " ignorelist" + EnumChatFormatting.GREEN + " to list and remove poeple from the list.");
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
                        ismgStatus.appendText(EnumChatFormatting.GRAY + " " + latestActivity + " " + (isProbBanned ? EnumChatFormatting.DARK_GRAY : EnumChatFormatting.YELLOW) + DateUtil.timeSince(latestActivityTime));

                    } else if (logindata.isOnline()) { // player is online

                        ismgStatus.appendText(EnumChatFormatting.GREEN + "    Online");

                    } else { // print lastlogout

                        final boolean isProbBanned = isProbBanned(logindata.getLastLogout());
                        ismgStatus.appendText(EnumChatFormatting.GRAY + " Lastlogout " + (isProbBanned ? EnumChatFormatting.DARK_GRAY : EnumChatFormatting.YELLOW) + DateUtil.timeSince(logindata.getLastLogout()));

                    }

                    ismgStatus.setChatStyle(new ChatStyle()
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to run : /stalk " + logindata.getdisplayname())))
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stalk " + logindata.getdisplayname())));

                    imsg.appendSibling(ismgStatus);

                }

            } else {

                imsg = new ChatComponentText(EnumChatFormatting.RED + uuid + EnumChatFormatting.GRAY + " reported : " + EnumChatFormatting.YELLOW + DateUtil.timeSince(wdr.timestamp));

            }

            return imsg.appendText("\n");

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
    private final boolean isNicked;

    public IgnoreLineTask(String uuid, boolean isNicked) {
        this.uuid = uuid;
        this.isNicked = isNicked;
    }

    @Override
    public IChatComponent call() {

        try {

            final String formattedName;
            if (this.isNicked) {
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
