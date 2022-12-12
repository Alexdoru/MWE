package fr.alexdoru.megawallsenhancementsmod.chat;

import fr.alexdoru.megawallsenhancementsmod.asm.hooks.NetHandlerPlayClientHook;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandHypixelShout;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandReport;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandWDR;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.data.ScangameData;
import fr.alexdoru.megawallsenhancementsmod.data.StringLong;
import fr.alexdoru.megawallsenhancementsmod.data.WDR;
import fr.alexdoru.megawallsenhancementsmod.data.WdrData;
import fr.alexdoru.megawallsenhancementsmod.events.MegaWallsGameEvent;
import fr.alexdoru.megawallsenhancementsmod.features.SquadHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.KillCounter;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportQueue;
import fr.alexdoru.megawallsenhancementsmod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.SoundUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportSuggestionHandler {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final String uuidPattern = "[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}";
    private static final Pattern REPORT_PATTERN1 = Pattern.compile("((?:\\w{2,16}|" + uuidPattern + ")) (?:|is )b?hop?ping", Pattern.CASE_INSENSITIVE);
    private static final Pattern REPORT_PATTERN2 = Pattern.compile("\\/?(?:wdr|report) (\\w{2,16}) ((?:\\w{2,16}|" + uuidPattern + "))", Pattern.CASE_INSENSITIVE);
    private static final List<StringLong> reportSuggestionHistory = new ArrayList<>();
    private static final List<Long> reportSpamCheck = new ArrayList<>();
    private static final long TIME_BETWEEN_REPORT_SUGGESTION_PLAYER = 40L * 60L * 1000L;

    @SubscribeEvent
    public void onMegaWallsGameEvent(MegaWallsGameEvent event) {
        if (event.getType() == MegaWallsGameEvent.EventType.GAME_START || event.getType() == MegaWallsGameEvent.EventType.GAME_END) {
            clearReportSuggestionHistory();
            ReportQueue.INSTANCE.clearPlayersReportedThisGame();
        }
    }

    public static boolean parseReportMessage(
            @Nullable String senderRank,
            @Nullable String messageSender,
            @Nullable String squadname,
            String msgIn,
            String fmsgIn) {
        if (ConfigHandler.reportSuggestions || ConfigHandler.autoreportSuggestions) {
            final Matcher matcher1 = REPORT_PATTERN1.matcher(msgIn);
            final Matcher matcher2 = REPORT_PATTERN2.matcher(msgIn);
            if (matcher1.find()) {
                final String reportText = matcher1.group();
                final String reportedPlayerOrUUID = matcher1.group(1);
                final String reportedPlayer = reportedPlayerOrUUID.length() == 36 ? getNameFromUUID(reportedPlayerOrUUID) : reportedPlayerOrUUID;
                if (reportedPlayer != null && isNameValid(reportedPlayer)) {
                    handleReportSuggestion(
                            reportedPlayer,
                            senderRank,
                            messageSender,
                            squadname,
                            reportedPlayerOrUUID.equals(reportedPlayer) ? reportText : reportText.replace(reportedPlayerOrUUID, reportedPlayer),
                            "bhop",
                            reportedPlayerOrUUID.equals(reportedPlayer) ? fmsgIn : fmsgIn.replace(reportedPlayerOrUUID, reportedPlayer));
                } else {
                    ChatUtil.addChatMessage(getIChatComponentWithSquadnameAsSender(fmsgIn, messageSender, squadname));
                }
                return true;
            } else if (matcher2.find()) {
                final String reportText = matcher2.group();
                final String reportedPlayerOrUUID = matcher2.group(1);
                final String reportedPlayer = reportedPlayerOrUUID.length() == 36 ? getNameFromUUID(reportedPlayerOrUUID) : reportedPlayerOrUUID;
                final String cheat = matcher2.group(2).toLowerCase();
                if (reportedPlayer != null && isCheatValid(cheat) && isNameValid(reportedPlayer)) {
                    handleReportSuggestion(
                            reportedPlayer,
                            senderRank,
                            messageSender,
                            squadname,
                            reportedPlayerOrUUID.equals(reportedPlayer) ? reportText : reportText.replace(reportedPlayerOrUUID, reportedPlayer),
                            cheat,
                            reportedPlayerOrUUID.equals(reportedPlayer) ? fmsgIn : fmsgIn.replace(reportedPlayerOrUUID, reportedPlayer));
                } else {
                    ChatUtil.addChatMessage(getIChatComponentWithSquadnameAsSender(fmsgIn, messageSender, squadname));
                }
                return true;
            }
        }
        return false;
    }

    private static String getNameFromUUID(String s) {
        final UUID uuid = UUID.fromString(s);
        final NetworkPlayerInfo networkPlayerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(uuid);
        return networkPlayerInfo == null ? null : networkPlayerInfo.getGameProfile().getName();
    }

    /**
     * reportedPlayer is necessarily in the tablist
     */
    private static void handleReportSuggestion(
            String reportedPlayer,
            @Nullable String senderRank,
            @Nullable String messageSender,
            @Nullable String squadname,
            String reportText,
            String cheat,
            String fmsg) {

        final boolean isSenderMyself = isPlayerMyself(messageSender);
        final boolean isTargetMyself = isPlayerMyself(reportedPlayer);
        boolean isSenderInTablist = false;
        boolean isSenderNicked = false;
        boolean isSenderFlaging = false;
        boolean isSenderIgnored = false;
        boolean isSenderCheating = false;
        /*Only accepts MVP, MVP+, MVP++*/
        boolean isSenderRankValid = false;

        String senderUUID = null;

        if (isSenderMyself) {

            isSenderInTablist = true;

        } else if (messageSender != null) {

            final NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.playerInfoMap.get(messageSender);
            if (networkPlayerInfo != null) {
                isSenderInTablist = true;
                final UUID id = networkPlayerInfo.getGameProfile().getId();
                isSenderNicked = NameUtil.isntRealPlayer(id);
                senderUUID = id.toString().replace("-", "");
                isSenderFlaging = ScangameData.doesPlayerFlag(id);
                final WDR wdr = WdrData.getWdr(senderUUID, messageSender);
                if (wdr != null) {
                    isSenderIgnored = wdr.isIgnored();
                    isSenderCheating = wdr.hasValidCheats();
                }
            }

        }

        if (isSenderNicked || senderRank != null && (senderRank.equals("VIP+") || senderRank.equals("MVP") || senderRank.equals("MVP+") || senderRank.equals("MVP++"))) {
            isSenderRankValid = true;
        }

        final boolean gotAutoreported = checkAndSendReportSuggestion(
                messageSender,
                reportedPlayer,
                cheat,
                isSenderMyself,
                isTargetMyself,
                isSenderInTablist,
                isSenderIgnored,
                isSenderCheating,
                isSenderFlaging,
                isSenderRankValid);
        printCustomReportSuggestionChatText(
                fmsg,
                messageSender,
                reportedPlayer,
                cheat,
                reportText,
                squadname,
                isSenderMyself,
                isTargetMyself,
                isSenderInTablist,
                isSenderIgnored,
                isSenderCheating,
                isSenderFlaging,
                gotAutoreported,
                senderUUID);

    }

    private static boolean checkAndSendReportSuggestion(
            @Nullable String messageSender,
            String reportedPlayer,
            String cheat,
            boolean isSenderMyself,
            boolean isTargetMyself,
            boolean isSenderInTablist,
            boolean isSenderIgnored,
            boolean isSenderCheating,
            boolean isSenderFlaging,
            boolean isSenderRankValid) {

        if (!ConfigHandler.autoreportSuggestions ||
                !isSenderInTablist ||
                messageSender == null ||
                isSenderIgnored ||
                isSenderCheating ||
                !FKCounterMod.isInMwGame ||
                isTargetMyself) {
            return false;
        }

        if (isSenderMyself) {
            CommandWDR.handleWDRCommand(new String[]{reportedPlayer, cheat}, canReportSuggestionPlayer(reportedPlayer, false), false);
        } else if (SquadHandler.getSquad().get(messageSender) != null) {
            CommandWDR.handleWDRCommand(new String[]{reportedPlayer, cheat}, false, false);
        }

        if (FKCounterMod.isitPrepPhase) {
            if (isSenderMyself) {
                new DelayedTask(() -> ChatUtil.addChatMessage(EnumChatFormatting.RED + "\u2716" + EnumChatFormatting.GRAY + " Cannot share a report before the walls fall!"), 0);
                return true;
            }
            return false;
        }

        if (isSenderFlaging) {
            if (isSenderMyself) {
                new DelayedTask(() -> ChatUtil.addChatMessage(EnumChatFormatting.RED + "\u2716" + EnumChatFormatting.GRAY + " You cannot share a report since you flag in /scangame!"), 0);
                return true;
            }
            return false;
        }

        if (!isSenderRankValid && !messageSender.equals(ConfigHandler.hypixelNick)) {
            if (isSenderMyself) {
                new DelayedTask(() -> ChatUtil.addChatMessage(EnumChatFormatting.RED + "\u2716" + EnumChatFormatting.GRAY + " You need to be at least " + EnumChatFormatting.GREEN + "VIP" + EnumChatFormatting.GOLD + "+" + EnumChatFormatting.GRAY + " to share a report with others"), 0);
                return true;
            }
            return false;
        }

        if (canReportSuggestionPlayer(reportedPlayer, true)) {
            if (isSenderMyself) {
                new DelayedTask(() -> ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "\u2714" + EnumChatFormatting.GRAY + " Your report will be shared with other players in the game"), 0);
                return true;
            }
            checkReportSpam();
            if (ReportQueue.INSTANCE.addReportSuggestionToQueue(messageSender, reportedPlayer)) {
                new DelayedTask(() -> ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "\u2714" + EnumChatFormatting.GRAY + " Sending report in a moment...")
                        .appendSibling(ChatUtil.getCancelButton(reportedPlayer))), 0);
                return true;
            } else {
                new DelayedTask(() -> ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "\u2714" + EnumChatFormatting.GRAY + " You already reported this player during this game"), 0);
            }
            return false;
        } else {
            if (isSenderMyself) {
                new DelayedTask(() -> ChatUtil.addChatMessage(EnumChatFormatting.RED + "\u2716" + EnumChatFormatting.GRAY + " This player has already been reported during this game"), 0);
                return true;
            } else {
                new DelayedTask(() -> ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "\u2714" + EnumChatFormatting.GRAY + " You already reported this player during this game"), 0);
            }
        }

        return false;

    }

    private static void checkReportSpam() {
        final long l = System.currentTimeMillis();
        reportSpamCheck.add(l);
        reportSpamCheck.removeIf(time -> (time + 30L * 1000L < l));
        if (reportSpamCheck.size() >= 4) {
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.YELLOW + "Is someone trying to spam the reporting system ?")
                    .appendSibling(ChatUtil.getCancelAllReportsButton()));
        }
    }

    private static void printCustomReportSuggestionChatText(
            String fmsg,
            @Nullable String messageSender,
            String reportedPlayer,
            String cheat,
            String reportText,
            @Nullable String squadname,
            boolean isSenderMyself,
            boolean isTargetMyself,
            boolean isSenderInTablist,
            boolean isSenderIgnored,
            boolean isSenderCheating,
            boolean isSenderFlaging,
            boolean gotAutoreported,
            @Nullable String senderUUID) {

        if (!ConfigHandler.reportSuggestions) {
            ChatUtil.addChatMessage(getIChatComponentWithSquadnameAsSender(fmsg, messageSender, squadname));
            return;
        }

        if (!isSenderIgnored && !isSenderCheating && !isSenderFlaging) {
            SoundUtil.playReportSuggestionSound();
        }

        if (!isSenderInTablist || messageSender == null) {
            final String newFmsg = getReportTextWithFormattedName(fmsg, reportText, reportedPlayer);
            final IChatComponent imsg = getIChatComponentWithSquadnameAsSender(newFmsg, messageSender, squadname);
            addButtons(imsg, reportedPlayer, cheat, isSenderMyself, isTargetMyself, gotAutoreported);
            ChatUtil.addChatMessage(imsg);
            return;
        }

        if (isSenderIgnored) {
            final IChatComponent imsg = new ChatComponentText(StringUtil.insertAfterName(fmsg, messageSender, EnumChatFormatting.GRAY + " (Ignored)", EnumChatFormatting.GRAY + EnumChatFormatting.STRIKETHROUGH.toString(), true));
            if (senderUUID != null) {
                imsg.appendSibling(ChatUtil.getUnIgnoreButton(senderUUID, messageSender));
            }
            ChatUtil.addChatMessage(imsg);
            return;
        }

        if (isSenderCheating) {
            ChatUtil.addChatMessage(StringUtil.insertAfterName(fmsg, messageSender, EnumChatFormatting.YELLOW + " (Cheater)", EnumChatFormatting.GRAY + EnumChatFormatting.STRIKETHROUGH.toString(), true));
            return;
        }

        if (isSenderFlaging) {
            final String newFmsg = StringUtil.insertAfterName(fmsg, messageSender, EnumChatFormatting.LIGHT_PURPLE + " (Scangame)", "", true);
            final IChatComponent imsg = getIChatComponentWithSquadnameAsSender(newFmsg, messageSender, squadname);
            if (FKCounterMod.isMWEnvironement && !isSenderMyself) {
                imsg.appendSibling(ChatUtil.getIgnoreButton(messageSender));
            }
            addButtons(imsg, reportedPlayer, cheat, isSenderMyself, isTargetMyself, gotAutoreported);
            ChatUtil.addChatMessage(imsg);
            return;
        }

        final String newFmsg = getReportTextWithFormattedName(fmsg, reportText, reportedPlayer);
        final IChatComponent imsg = getIChatComponentWithSquadnameAsSender(newFmsg, messageSender, squadname);
        if (FKCounterMod.isMWEnvironement && !isSenderMyself) {
            imsg.appendSibling(ChatUtil.getIgnoreButton(messageSender));
        }
        addButtons(imsg, reportedPlayer, cheat, isSenderMyself, isTargetMyself, gotAutoreported);
        ChatUtil.addChatMessage(imsg);

    }

    private static String getReportTextWithFormattedName(String fmsg, String reportText, String reportedPlayer) {
        if (FKCounterMod.isInMwGame) {
            final String newReportText = EnumChatFormatting.DARK_RED + reportText.replace(reportedPlayer, NameUtil.getFormattedNameWithoutIcons(reportedPlayer) + EnumChatFormatting.DARK_RED);
            return StringUtil.replaceTargetWith(fmsg, reportText, newReportText);
        }
        return StringUtil.changeColorOf(fmsg, reportText, EnumChatFormatting.DARK_RED);
    }

    /**
     * Check to not send report suggestion twice per game for the same player
     */
    private static boolean canReportSuggestionPlayer(String playername, boolean addReportToList) {
        final long timestamp = System.currentTimeMillis();
        reportSuggestionHistory.removeIf(o -> (o.timestamp + TIME_BETWEEN_REPORT_SUGGESTION_PLAYER < timestamp));
        for (final StringLong stringLong : reportSuggestionHistory) {
            if (stringLong.message != null && stringLong.message.equalsIgnoreCase(playername)) {
                return false;
            }
        }
        if (addReportToList) {
            reportSuggestionHistory.add(new StringLong(timestamp, playername));
        }
        return true;
    }

    public static List<StringLong> getReportSuggestionHistory() {
        return reportSuggestionHistory;
    }

    private static void addButtons(IChatComponent imsg, String reportedPlayer, String cheat, boolean isSenderMyself, boolean isTargetMyself, boolean gotautoreported) {
        if (isTargetMyself) {
            return;
        }
        if (!gotautoreported) {
            imsg.appendSibling(ChatUtil.getReportButton(reportedPlayer, "cheating", ClickEvent.Action.RUN_COMMAND));
        }
        if (!isSenderMyself || !gotautoreported) {
            imsg.appendSibling(ChatUtil.getWDRButton(reportedPlayer, cheat, ClickEvent.Action.SUGGEST_COMMAND));
        }
    }

    private static IChatComponent getIChatComponentWithSquadnameAsSender(String fmsg, @Nullable String messageSender, @Nullable String squadname) {
        return new ChatComponentText(messageSender != null && squadname != null ? fmsg.replaceFirst(messageSender, squadname) : fmsg);
    }

    private static boolean isCheatValid(String cheat) {
        return CommandReport.cheatsList.contains(cheat);
    }

    private static boolean isNameValid(String playername) {
        return NetHandlerPlayClientHook.playerInfoMap.get(playername) != null || isPlayerMyself(playername) || KillCounter.wasPlayerInThisGame(playername);
    }

    private static boolean isPlayerMyself(@Nullable String name) {
        return (mc.thePlayer != null && mc.thePlayer.getName().equalsIgnoreCase(name)) || (!ConfigHandler.hypixelNick.equals("") && ConfigHandler.hypixelNick.equals(name));
    }

    private static void clearReportSuggestionHistory() {
        reportSuggestionHistory.clear();
    }

    /**
     * Mirrors the {@link ReportSuggestionHandler#parseReportMessage(String, String, String, String, String)}
     * method and returns true if this method would parse the shout as a report suggestion
     * but the player can't be found in the tablist.
     * Although it only checks one regex pattern because the other one would conflict
     * too much with messages that people want to send outside of report suggestions.
     * This is mainly to prevent wasting shouts if the targeted player
     * isn't found at the moment you send the shout.
     */
    public static boolean shouldCancelShout(String msg) {
        final Matcher matcher2 = REPORT_PATTERN2.matcher(msg);
        if (matcher2.find()) {
            final String reportedPlayerOrUUID = matcher2.group(1);
            final String reportedPlayer = reportedPlayerOrUUID.length() == 36 ? getNameFromUUID(reportedPlayerOrUUID) : reportedPlayerOrUUID;
            final String cheat = matcher2.group(2).toLowerCase();
            if (isCheatValid(cheat) && reportedPlayer != null) {
                return !isNameValid(reportedPlayer);
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Parses the blocked message to look for a report suggestion, if it contains one,
     * and it was sent from a shout, it sends it again but with the uuid instead of the playername
     */
    public static void processBlockedMessage(String msg) {
        final String latestShoutSent = CommandHypixelShout.getLatestShoutSent();
        if (latestShoutSent == null || !latestShoutSent.equals(msg)) {
            return;
        }
        CommandHypixelShout.resetLastShout();
        final Matcher matcher1 = Pattern.compile("(\\w{2,16}) (?:|is )b?hop?ping", Pattern.CASE_INSENSITIVE).matcher(msg);
        final Matcher matcher2 = Pattern.compile("\\/?(?:wdr|report) (\\w{2,16}) (\\w+)", Pattern.CASE_INSENSITIVE).matcher(msg);
        if (matcher1.find()) {
            final String reportText = matcher1.group();
            final String reportedPlayer = matcher1.group(1);
            if (isNameValid(reportedPlayer)) {
                sendShoutWithUUID(msg, reportText, reportedPlayer);
            }
        } else if (matcher2.find()) {
            final String reportText = matcher2.group();
            final String reportedPlayer = matcher2.group(1);
            final String cheat = matcher2.group(2).toLowerCase();
            if (isCheatValid(cheat) && isNameValid(reportedPlayer)) {
                sendShoutWithUUID(msg, reportText, reportedPlayer);
            }
        }
    }

    private static void sendShoutWithUUID(String blockedMessgae, String reportText, String reportedPlayer) {
        final NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.playerInfoMap.get(reportedPlayer);
        if (networkPlayerInfo == null) return;
        final String uuid = networkPlayerInfo.getGameProfile().getId().toString();
        if (mc.thePlayer != null) {
            new DelayedTask(() -> ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "Shout was blocked, trying to send report suggestion with UUID instead"), 1);
            new DelayedTask(() -> mc.thePlayer.sendChatMessage("/shout " + blockedMessgae.replaceFirst(reportText, reportText.replaceFirst(reportedPlayer, uuid))), 20);
        }
    }

}
