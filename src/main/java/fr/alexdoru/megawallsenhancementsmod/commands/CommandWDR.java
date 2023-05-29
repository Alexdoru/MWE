package fr.alexdoru.megawallsenhancementsmod.commands;

import fr.alexdoru.megawallsenhancementsmod.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedHypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangPlayernameToUUID;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.chat.WarningMessagesHandler;
import fr.alexdoru.megawallsenhancementsmod.data.ReportTimestamp;
import fr.alexdoru.megawallsenhancementsmod.data.WDR;
import fr.alexdoru.megawallsenhancementsmod.data.WdrData;
import fr.alexdoru.megawallsenhancementsmod.features.PartyDetection;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.KillCounter;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.GameInfoTracker;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportQueue;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardUtils;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.MultithreadingUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.TabCompletionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandWDR extends MyAbstractCommand {

    private static final HashMap<String, ReportTimestamp> timestampsMap = new HashMap<>();
    private static final char TIMESTAMP_REPORT_CHAR = '-';
    private static final char TIMEMARK_REPORT_CHAR = '#';
    private static int nbTimeMarks = 0;

    @Override
    public String getCommandName() {
        return "watchdogreport";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender));
            return;
        }
        handleWDRCommand(args, true, false);
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("wdr");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/wdr <player> <cheats(optional)> <timestamp(optional)>";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            if (FKCounterMod.isInMwGame) {
                if (FKCounterMod.isPrepPhase) {
                    return getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName());
                } else {
                    final List<String> playersInThisGame = KillCounter.getPlayersInThisGame();
                    playersInThisGame.removeAll(TabCompletionUtil.getOnlinePlayersByName());
                    return getListOfStringsMatchingLastWord(args, playersInThisGame);
                }
            }
            return null;
        }
        return args.length > 1 ? getListOfStringsMatchingLastWord(args, CommandReport.cheatsArray) : null;
    }

    public static void addTimeMark() {
        nbTimeMarks++;
        final String key = String.valueOf(nbTimeMarks);
        final long timestamp = (new Date()).getTime();
        final String serverID = ScoreboardUtils.getGameIdFromScoreboard();
        final String timerOnReplay = GameInfoTracker.getTimeSinceGameStart(timestamp, serverID, 0);
        timestampsMap.put(key, new ReportTimestamp(timestamp, serverID, timerOnReplay));
        ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters()
                + EnumChatFormatting.GREEN + "Added timestamp : " + EnumChatFormatting.GOLD + "#" + key + EnumChatFormatting.GREEN + ".")
                .setChatStyle(new ChatStyle()
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ChatComponentText(EnumChatFormatting.GREEN + "Key : " + EnumChatFormatting.GOLD + "#" + key + "\n" +
                                        EnumChatFormatting.GREEN + "Timestamp : " + EnumChatFormatting.GOLD + DateUtil.ESTformatTimestamp(timestamp) + "\n" +
                                        EnumChatFormatting.GREEN + "ServerID : " + EnumChatFormatting.GOLD + serverID + "\n" +
                                        EnumChatFormatting.GREEN + "Timer on replay (approx.) : " + EnumChatFormatting.GOLD + timerOnReplay + "\n" +
                                        EnumChatFormatting.YELLOW + "Click to fill a report with this timestmap")))
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/wdr  " + "#" + key))));
    }

    public static void handleWDRCommand(String[] args, boolean sendReport, boolean showReportMessage) {
        boolean usesTimestamp = false;
        boolean usesTimeMark = false;
        final ArrayList<String> arraycheats = new ArrayList<>();
        final String playername = args[0];
        final StringBuilder message = new StringBuilder("/wdr " + playername);
        String serverID = "?";
        String timerOnReplay = "?";
        long longtimetosubtract = 0;
        long timestamp = 0;
        final long time = (new Date()).getTime();

        if (args.length == 1) {

            arraycheats.add("cheating");

        } else {

            for (int i = 1; i < args.length; i++) { // reads each arg one by one

                if (args[i].charAt(0) == TIMESTAMP_REPORT_CHAR) { // handling timestamped reports

                    if (usesTimestamp) {
                        ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "You can't have more than one timestamp in the arguments.");
                        return;
                    }

                    if (usesTimeMark) {
                        ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "You can't use both special arguments in the same reports!");
                        return;
                    }

                    usesTimestamp = true;
                    final String rawtimestamp = args[i].substring(1);

                    if (!args[i].equals("-")) { // computing the -time argument

                        final Matcher Matcher0 = Pattern.compile("(\\d+)").matcher(rawtimestamp);
                        final Matcher Matcher1 = Pattern.compile("(\\d+)s").matcher(rawtimestamp);
                        final Matcher Matcher2 = Pattern.compile("(\\d+)m").matcher(rawtimestamp);
                        final Matcher Matcher3 = Pattern.compile("(\\d+)m(\\d+)s").matcher(rawtimestamp);

                        if (Matcher0.matches()) {
                            longtimetosubtract = Long.parseLong(Matcher0.group(1));
                        } else if (Matcher1.matches()) {
                            longtimetosubtract = Long.parseLong(Matcher1.group(1));
                        } else if (Matcher2.matches()) {
                            longtimetosubtract = 60 * Long.parseLong(Matcher2.group(1));
                        } else if (Matcher3.matches()) {
                            longtimetosubtract = 60 * Long.parseLong(Matcher3.group(1)) + Long.parseLong(Matcher3.group(2));
                        }

                    }

                    if (args.length == 2) {
                        arraycheats.add("cheating");
                    }

                    timestamp = time - longtimetosubtract * 1000; // Milliseconds
                    serverID = ScoreboardUtils.getGameIdFromScoreboard();
                    timerOnReplay = GameInfoTracker.getTimeSinceGameStart(timestamp, serverID, (int) longtimetosubtract);
                    message.append(" ").append(args[i]);

                } else if (args[i].charAt(0) == TIMEMARK_REPORT_CHAR) { // process the command if you use a stored timestamp

                    if (usesTimeMark) {
                        ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "You can't use more than one #timestamp in the arguments.");
                        return;
                    }

                    if (usesTimestamp) {
                        ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "You can't use both special arguments in the same reports!");
                        return;
                    }

                    usesTimeMark = true;

                    final String key = args[i].substring(1);
                    final ReportTimestamp timemark = timestampsMap.get(key);

                    if (timemark == null) {

                        ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.YELLOW + key + EnumChatFormatting.RED + " isn't a valid timestamp #");
                        return;

                    } else {

                        if (args.length == 2) {
                            arraycheats.add("cheating");
                        }

                        timestamp = timemark.timestamp;
                        serverID = timemark.serverID;
                        timerOnReplay = timemark.timerOnReplay;
                        message.append(" ").append(TIMESTAMP_REPORT_CHAR).append((time - timestamp) / 1000L);

                    }

                } else if (args[i].equalsIgnoreCase("fastbreak")) {
                    arraycheats.add(args[i]);
                    message.append(" speed");
                } else if (args[i].equalsIgnoreCase("autoblock") || args[i].equalsIgnoreCase("multiaura")) {
                    arraycheats.add(args[i]);
                    message.append(" killaura");
                } else if (args[i].equalsIgnoreCase("noslowdown") || args[i].equalsIgnoreCase("keepsprint")) {
                    arraycheats.add(args[i]);
                    message.append(" velocity");
                } else {
                    arraycheats.add(args[i]);
                    message.append(" ").append(args[i]); //reconstructs the message to send it to the server
                }

            }

        }

        if (sendReport) {
            if (mc.thePlayer != null) {
                sendChatMessage(message.toString());
                ReportQueue.INSTANCE.addPlayerReportedThisGame(playername);
            }
        }

        PartyDetection.printBoostingReportAdvice(playername);

        if (FKCounterMod.isPreGameLobby) {
            ChatUtil.addChatMessage(ChatUtil.getChatReportingAdvice());
        }

        final boolean finalUsesTimestamp = usesTimestamp;
        final boolean finalUsesTimeMark = usesTimeMark;
        final String finalServerID = serverID;
        final String finalTimerOnReplay = timerOnReplay;
        final long finalTimestamp = timestamp;

        MultithreadingUtil.addTaskToQueue(() -> {

            String uuid = null;
            boolean isaNick = false;
            String formattedPlayername = null;
            String playernameInThread = playername;

            try {
                final MojangPlayernameToUUID apireq = new MojangPlayernameToUUID(playernameInThread);
                uuid = apireq.getUuid();
                playernameInThread = apireq.getName();
                if (uuid != null && !HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                    final CachedHypixelPlayerData playerdata;
                    try {
                        playerdata = new CachedHypixelPlayerData(uuid);
                        final LoginData loginData = new LoginData(playerdata.getPlayerData());
                        formattedPlayername = loginData.getFormattedName();
                        if (loginData.hasNeverJoinedHypixel()) {
                            uuid = null;
                        } else if (!playernameInThread.equals(loginData.getdisplayname())) {
                            uuid = null;
                        }
                    } catch (ApiException e) {
                        uuid = null;
                    }
                }
            } catch (ApiException ignored) {}

            if (uuid == null) {  // The playername doesn't exist or never joined hypixel

                for (final NetworkPlayerInfo networkplayerinfo : mc.getNetHandler().getPlayerInfoMap()) { // search for the player's gameprofile in the tablist
                    if (networkplayerinfo.getGameProfile().getName().equalsIgnoreCase(args[0])) {
                        uuid = networkplayerinfo.getGameProfile().getName();
                        formattedPlayername = ScorePlayerTeam.formatPlayerName(networkplayerinfo.getPlayerTeam(), networkplayerinfo.getGameProfile().getName());
                        playernameInThread = uuid;
                        isaNick = true;
                    }
                }

                if (!isaNick) { // couldn't find the nicked player in the tab list
                    ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + ChatUtil.inexistantMinecraftNameMsg(args[0]) + EnumChatFormatting.RED + " Couldn't find the " + EnumChatFormatting.DARK_PURPLE + "nicked" + EnumChatFormatting.RED + " player in the tablist");
                    return null;
                }

            }

            final String finalPlayername = playernameInThread;
            final ArrayList<String> argsinWDR = new ArrayList<>();
            if (finalUsesTimestamp || finalUsesTimeMark) { // format for timestamps reports : UUID timestamplastreport -serverID timeonreplay playernameduringgame timestampforcheat specialcheat cheat1 cheat2 cheat3 etc

                argsinWDR.add("-" + finalServerID);
                argsinWDR.add(finalTimerOnReplay);
                argsinWDR.add(finalPlayername);
                argsinWDR.add(Long.toString(finalTimestamp));
                argsinWDR.addAll(arraycheats);

                final WDR wdr = WdrData.getWdr(uuid);
                if (wdr != null) {
                    argsinWDR.addAll(wdr.hacks);
                }

                if (isaNick && !argsinWDR.contains(WDR.NICK)) {
                    argsinWDR.add(WDR.NICK);
                }

                final WDR newreport = new WDR(finalTimestamp, finalTimestamp, argsinWDR);
                WdrData.put(uuid, newreport);
                Minecraft.getMinecraft().addScheduledTask(() -> NameUtil.updateMWPlayerDataAndEntityData(finalPlayername, false));
                ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() +
                        EnumChatFormatting.GREEN + "You reported " + (isaNick ? EnumChatFormatting.GREEN + "the" + EnumChatFormatting.DARK_PURPLE + " nicked player " : ""))
                        .appendSibling(WarningMessagesHandler.createPlayerNameWithHoverText(formattedPlayername, playernameInThread, uuid, newreport, EnumChatFormatting.RED)[0])
                        .appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + " with a " + EnumChatFormatting.YELLOW +
                                "timestamp" + EnumChatFormatting.GREEN + " and will receive warnings about this player in-game"
                                + EnumChatFormatting.GREEN + (isaNick ? " for the next 24 hours." : "."))));

            } else {  // isn't a timestamped report

                final WDR wdr = WdrData.getWdr(uuid);
                boolean alreadyReported = false;

                if (wdr != null) {

                    argsinWDR.addAll(wdr.hacks);
                    alreadyReported = true;

                    for (final String arraycheat : arraycheats) {
                        boolean doublon = false;
                        for (final String arg : argsinWDR) {
                            if (arraycheat.equals(arg)) {
                                doublon = true;
                                break;
                            }
                        }
                        if (!doublon) {
                            argsinWDR.add(arraycheat);
                        }
                    }

                } else {
                    argsinWDR.addAll(arraycheats);
                }

                if (isaNick && !argsinWDR.contains(WDR.NICK)) {
                    argsinWDR.add(WDR.NICK);
                }
                WdrData.put(uuid, new WDR(time, time, argsinWDR));
                Minecraft.getMinecraft().addScheduledTask(() -> NameUtil.updateMWPlayerDataAndEntityData(finalPlayername, false));
                if (showReportMessage || !alreadyReported) {
                    ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() +
                            EnumChatFormatting.GREEN + "You reported " + (isaNick ? EnumChatFormatting.GREEN + "the" + EnumChatFormatting.DARK_PURPLE + " nicked player " : "")
                            + EnumChatFormatting.RED + (formattedPlayername == null ? playernameInThread : EnumChatFormatting.RESET + formattedPlayername) + EnumChatFormatting.GREEN + " and will receive warnings about this player in-game"
                            + EnumChatFormatting.GREEN + (isaNick ? " for the next 24 hours." : "."));
                }
            }

            return null;

        });
    }

}