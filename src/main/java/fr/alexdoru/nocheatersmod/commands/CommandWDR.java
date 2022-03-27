package fr.alexdoru.nocheatersmod.commands;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedHypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.api.cache.CachedMojangUUID;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser.LoginData;
import fr.alexdoru.megawallsenhancementsmod.utils.*;
import fr.alexdoru.nocheatersmod.data.TimeMark;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import fr.alexdoru.nocheatersmod.events.GameInfoGrabber;
import fr.alexdoru.nocheatersmod.events.ReportQueue;
import fr.alexdoru.nocheatersmod.util.NoCheatersMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.*;

public class CommandWDR extends CommandBase {

    private static final ResourceLocation reportDeniedSound = new ResourceLocation("mob.villager.no");
    private static final HashMap<String, TimeMark> TimeMarksMap = new HashMap<>();
    private static final char timestampreportkey = '-';
    private static final char timemarkedreportkey = '#';
    private static int nbTimeMarks = 0;
    private static final List<Long> commandUsageTimeList = new ArrayList<>();
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void addTimeMark() {
        nbTimeMarks++;
        String key = String.valueOf(nbTimeMarks);
        long timestamp = (new Date()).getTime();
        String serverID = GameInfoGrabber.getGameIDfromscoreboard();
        String timerOnReplay = GameInfoGrabber.getTimeSinceGameStart(timestamp, serverID, 0);
        TimeMarksMap.put(key, new TimeMark(timestamp, serverID, timerOnReplay));
        addChatMessage(new ChatComponentText(getTagNoCheaters()
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

    @Override
    public String getCommandName() {
        return "watchdogreport";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : " + getCommandUsage(sender)));
            return;
        }
        handleWDRCommand(args, false);
    }

    public static void handleWDRCommand(String[] args, boolean delayReport) {
        Multithreading.addTaskToQueue(() -> {
            boolean isaTimestampedReport = false;
            boolean usesTimeMark = false;
            ArrayList<String> arraycheats = new ArrayList<>();    // for WDR object
            String playername = args[0];
            StringBuilder message = new StringBuilder("/wdr " + playername);
            String serverID = "?";
            String timerOnReplay = "?";
            long longtimetosubtract = 0;
            long timestamp = 0;
            long time = (new Date()).getTime();

            if (args.length == 1) {
                arraycheats.add("cheating");
            } else {
                for (int i = 1; i < args.length; i++) { // reads each arg one by one

                    if (args[i].charAt(0) == timestampreportkey) { // handling timestamped reports

                        if (isaTimestampedReport) {
                            addChatMessage(new ChatComponentText(getTagNoCheaters()
                                    + EnumChatFormatting.RED + "You can't have more than one timestamp in the arguments."));
                            return null;
                        }

                        if (usesTimeMark) {
                            addChatMessage(new ChatComponentText(getTagNoCheaters()
                                    + EnumChatFormatting.RED + "You can't use both special arguments in the same reports!"));
                            return null;
                        }

                        isaTimestampedReport = true;
                        String rawtimestamp = args[i].substring(1);

                        if (!args[i].equals("-")) { // computing the -time argument

                            Matcher Matcher0 = Pattern.compile("(\\d+)").matcher(rawtimestamp);
                            Matcher Matcher1 = Pattern.compile("(\\d+)s").matcher(rawtimestamp);
                            Matcher Matcher2 = Pattern.compile("(\\d+)m").matcher(rawtimestamp);
                            Matcher Matcher3 = Pattern.compile("(\\d+)m(\\d+)s").matcher(rawtimestamp);

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

                        timestamp = time - longtimetosubtract * 1000; // Milliseconds
                        serverID = GameInfoGrabber.getGameIDfromscoreboard();
                        timerOnReplay = GameInfoGrabber.getTimeSinceGameStart(timestamp, serverID, (int) longtimetosubtract);
                        message.append(" ").append(args[i]);

                    } else if (args[i].charAt(0) == timemarkedreportkey) { // process the command if you use a stored timestamp

                        if (usesTimeMark) {
                            addChatMessage(new ChatComponentText(getTagNoCheaters()
                                    + EnumChatFormatting.RED + "You can't use more than one #timestamp in the arguments."));
                            return null;
                        }

                        if (isaTimestampedReport) {
                            addChatMessage(new ChatComponentText(getTagNoCheaters()
                                    + EnumChatFormatting.RED + "You can't use both special arguments in the same reports!"));
                            return null;
                        }

                        usesTimeMark = true;

                        String key = args[i].substring(1);
                        TimeMark timemark = TimeMarksMap.get(key);

                        if (timemark == null) {

                            addChatMessage(new ChatComponentText(getTagNoCheaters()
                                    + EnumChatFormatting.YELLOW + key + EnumChatFormatting.RED + " isn't a valid timestamp #"));
                            return null;

                        } else {

                            timestamp = timemark.timestamp;
                            serverID = timemark.serverID;
                            timerOnReplay = timemark.timerOnReplay;

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

            if ((isaTimestampedReport || usesTimeMark) && args.length == 2) {
                addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage : /wdr <player> <cheats(optional)> <timestamp(optional)>"));
                return null;
            }

            if ((isaTimestampedReport || usesTimeMark)) { // adds the timestamp to the report
                message.append(" ").append(timerOnReplay.equals("?") ? "" : timerOnReplay);
            }

            if (delayReport) {
                ReportQueue.INSTANCE.addPlayerToQueueRandom(playername);
            } else {
                if (mc.thePlayer != null) {
                    mc.thePlayer.sendChatMessage(message.toString()); //sends command to server
                }
            }

            long l = System.currentTimeMillis();
            commandUsageTimeList.removeIf(o -> (o + 2 * 60 * 1000L < l));
            boolean playSound = false;
            if (commandUsageTimeList.size() >= 5) {
                ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Don't report too many players at once or Hypixel will ignore your reports thinking you are a bot trying to flood their system"));
                playSound = true;
            }
            commandUsageTimeList.add(l);

            if (FKCounterMod.preGameLobby) {
                ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getChatReportingAdvice()));
                playSound = true;
            }

            if (playSound) {
                mc.addScheduledTask(() -> mc.getSoundHandler().playSound(PositionedSoundRecord.create(reportDeniedSound, 1.0F)));
            }

            CachedMojangUUID apireq;
            String uuid = null;
            boolean isaNick = false;
            String formattedPlayername = null;

            try {
                apireq = new CachedMojangUUID(playername);
                uuid = apireq.getUuid();
                playername = apireq.getName();
                if (uuid != null && !HypixelApiKeyUtil.apiKeyIsNotSetup()) {
                    CachedHypixelPlayerData playerdata;
                    try {
                        playerdata = new CachedHypixelPlayerData(uuid, HypixelApiKeyUtil.getApiKey());
                        LoginData loginData = new LoginData(playerdata.getPlayerData());
                        formattedPlayername = loginData.getFormattedName();
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

                for (NetworkPlayerInfo networkplayerinfo : mc.getNetHandler().getPlayerInfoMap()) { // search for the player's gameprofile in the tablist
                    if (networkplayerinfo.getGameProfile().getName().equalsIgnoreCase(args[0])) {
                        uuid = networkplayerinfo.getGameProfile().getName();
                        formattedPlayername = ScorePlayerTeam.formatPlayerName(networkplayerinfo.getPlayerTeam(), networkplayerinfo.getGameProfile().getName());
                        playername = uuid;
                        isaNick = true;
                    }
                }

                if (!isaNick) { // couldn't find the nicked player in the tab list
                    addChatMessage(new ChatComponentText(getTagNoCheaters() + invalidplayernameMsg(args[0]) + EnumChatFormatting.RED + " Couldn't find the " + EnumChatFormatting.DARK_PURPLE + "nicked" + EnumChatFormatting.RED + " player in the tablist"));
                    return null;
                }

            }

            // save the player in the wdr map

            ArrayList<String> argsinWDR = new ArrayList<>();
            if (isaTimestampedReport || usesTimeMark) { // format for timestamps reports : UUID timestamplastreport -serverID timeonreplay playernameduringgame timestampforcheat specialcheat cheat1 cheat2 cheat3 etc

                argsinWDR.add("-" + serverID);
                argsinWDR.add(timerOnReplay);
                argsinWDR.add(playername);
                argsinWDR.add(Long.toString(timestamp));
                argsinWDR.addAll(arraycheats);

                WDR wdr = WdredPlayers.getWdredMap().get(uuid); // look if he was already reported and add the previous cheats without duplicates

                if (wdr != null) { // the player was already reported before

                    //if(wdr.hacks.get(0).charAt(0) == '-') { // previous report was also a timestamped report

                    // adds the previous arguments after the current report
                    argsinWDR.addAll(wdr.hacks);
                    //}
                }

                if (isaNick && !argsinWDR.contains(WDR.NICK)) {
                    argsinWDR.add(WDR.NICK);
                }

                WDR newreport = new WDR(timestamp, timestamp, argsinWDR);
                WdredPlayers.getWdredMap().put(uuid, newreport);
                NameUtil.updateGameProfileAndName(playername, false);
                addChatMessage(new ChatComponentText(getTagNoCheaters() +
                        EnumChatFormatting.GREEN + "You reported " + (isaNick ? EnumChatFormatting.GREEN + "the" + EnumChatFormatting.DARK_PURPLE + " nicked player " : ""))
                        .appendSibling(NoCheatersMessages.createPlayerNameWithHoverText(formattedPlayername, playername, uuid, newreport, EnumChatFormatting.RED)[0])
                        .appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + " with a " + EnumChatFormatting.YELLOW +
                                "timestamp" + EnumChatFormatting.GREEN + " and will receive warnings about this player in-game"
                                + EnumChatFormatting.GREEN + (isaNick ? " for the next 24 hours." : "."))));

            } else {  // isn't a timestamped report

                WDR wdr = WdredPlayers.getWdredMap().get(uuid); // look if he was already reported and add the previous cheats without duplicates

                if (wdr != null) { // the player was already reported before

                    argsinWDR.addAll(wdr.hacks);

                    for (String arraycheat : arraycheats) { // adds the report at the end of the previous information and avoids duplicates
                        boolean doublon = false;
                        for (String arg : argsinWDR) {
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
                WdredPlayers.getWdredMap().put(uuid, new WDR(time, time, argsinWDR));
                NameUtil.updateGameProfileAndName(playername, false);
                addChatMessage(new ChatComponentText(getTagNoCheaters() +
                        EnumChatFormatting.GREEN + "You reported " + (isaNick ? EnumChatFormatting.GREEN + "the" + EnumChatFormatting.DARK_PURPLE + " nicked player " : "")
                        + EnumChatFormatting.RED + (formattedPlayername == null ? playername : EnumChatFormatting.RESET + formattedPlayername) + EnumChatFormatting.GREEN + " and will receive warnings about this player in-game"
                        + EnumChatFormatting.GREEN + (isaNick ? " for the next 24 hours." : ".")));
            }

            return null;

        });
    }

    @Override
    public List<String> getCommandAliases() {
        ArrayList<String> res = new ArrayList<>();
        res.add("wdr");
        res.add("Wdr");
        res.add("WDR");
        return res;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/wdr <player> <cheats(optional)> <timestamp(optional)>";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? (FKCounterMod.isInMwGame && FKCounterMod.isitPrepPhase ? getListOfStringsMatchingLastWord(args, TabCompletionUtil.getOnlinePlayersByName()) : null) : (args.length > 1 ? getListOfStringsMatchingLastWord(args, CommandReport.cheatsArray) : null);
    }

}