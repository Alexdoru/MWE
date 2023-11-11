package fr.alexdoru.megawallsenhancementsmod.nocheaters;

import fr.alexdoru.megawallsenhancementsmod.asm.hooks.NetHandlerPlayClientHook;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandHypixelShout;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandReport;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.data.ScangameData;
import fr.alexdoru.megawallsenhancementsmod.data.StringLong;
import fr.alexdoru.megawallsenhancementsmod.events.MegaWallsGameEvent;
import fr.alexdoru.megawallsenhancementsmod.features.FinalKillCounter;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
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
import net.minecraftforge.client.event.ClientChatReceivedEvent;
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

    @SubscribeEvent
    public void onMegaWallsGameEvent(MegaWallsGameEvent event) {
        if (event.getType() == MegaWallsGameEvent.EventType.GAME_START || event.getType() == MegaWallsGameEvent.EventType.GAME_END) {
            clearReportSuggestionHistory();
            ReportQueue.INSTANCE.clearPlayersReportedThisGame();
        }
    }

    public static boolean parseReportMessage(
            ClientChatReceivedEvent event,
            @Nullable String messageSender,
            @Nullable String squadname,
            String msgIn,
            String fmsgIn) {
        if (ConfigHandler.reportSuggestions) {
            final Matcher matcher1 = REPORT_PATTERN1.matcher(msgIn);
            final Matcher matcher2 = REPORT_PATTERN2.matcher(msgIn);
            if (matcher1.find()) {
                final String reportText = matcher1.group();
                final String reportedPlayerOrUUID = matcher1.group(1);
                final String reportedPlayer = reportedPlayerOrUUID.length() == 36 ? getNameFromUUID(reportedPlayerOrUUID) : reportedPlayerOrUUID;
                if (reportedPlayer != null && isNameValid(reportedPlayer)) {
                    handleReportSuggestion(
                            event,
                            reportedPlayer,
                            messageSender,
                            squadname,
                            reportedPlayerOrUUID.equals(reportedPlayer) ? reportText : reportText.replace(reportedPlayerOrUUID, reportedPlayer),
                            "bhop",
                            reportedPlayerOrUUID.equals(reportedPlayer) ? fmsgIn : fmsgIn.replace(reportedPlayerOrUUID, reportedPlayer));
                } else {
                    event.message = getIChatComponentWithSquadnameAsSender(fmsgIn, messageSender, squadname);
                }
                return true;
            } else if (matcher2.find()) {
                final String reportText = matcher2.group();
                final String reportedPlayerOrUUID = matcher2.group(1);
                final String reportedPlayer = reportedPlayerOrUUID.length() == 36 ? getNameFromUUID(reportedPlayerOrUUID) : reportedPlayerOrUUID;
                final String cheat = matcher2.group(2).toLowerCase();
                if (reportedPlayer != null && isCheatValid(cheat) && isNameValid(reportedPlayer)) {
                    handleReportSuggestion(
                            event,
                            reportedPlayer,
                            messageSender,
                            squadname,
                            reportedPlayerOrUUID.equals(reportedPlayer) ? reportText : reportText.replace(reportedPlayerOrUUID, reportedPlayer),
                            cheat,
                            reportedPlayerOrUUID.equals(reportedPlayer) ? fmsgIn : fmsgIn.replace(reportedPlayerOrUUID, reportedPlayer));
                } else {
                    event.message = getIChatComponentWithSquadnameAsSender(fmsgIn, messageSender, squadname);
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
            ClientChatReceivedEvent event,
            String reportedPlayer,
            @Nullable String messageSender,
            @Nullable String squadname,
            String reportText,
            String cheat,
            String fmsg) {

        final boolean isSenderMyself = isPlayerMyself(messageSender);
        final boolean isTargetMyself = isPlayerMyself(reportedPlayer);
        boolean isSenderInTablist = false;
        boolean isSenderFlaging = false;
        boolean isSenderCheating = false;
        /*Only accepts VIP+, MVP, MVP+, MVP++*/

        final String senderUUID;

        if (isSenderMyself) {

            isSenderInTablist = true;

        } else if (messageSender != null) {

            final NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.getPlayerInfo(messageSender);
            if (networkPlayerInfo != null) {
                isSenderInTablist = true;
                final UUID id = networkPlayerInfo.getGameProfile().getId();
                senderUUID = id.toString().replace("-", "");
                isSenderFlaging = ScangameData.doesPlayerFlag(id);
                final WDR wdr = WdrData.getWdr(senderUUID, messageSender);
                if (wdr != null) {
                    isSenderCheating = wdr.hasValidCheats();
                }
            }

        }

        printCustomReportSuggestionChatText(
                event,
                fmsg,
                messageSender,
                reportedPlayer,
                cheat,
                reportText,
                squadname,
                isTargetMyself,
                isSenderInTablist,
                isSenderCheating,
                isSenderFlaging
        );

    }

    private static void printCustomReportSuggestionChatText(
            ClientChatReceivedEvent event,
            String fmsg,
            @Nullable String messageSender,
            String reportedPlayer,
            String cheat,
            String reportText,
            @Nullable String squadname,
            boolean isTargetMyself,
            boolean isSenderInTablist,
            boolean isSenderCheating,
            boolean isSenderFlaging) {

        if (!ConfigHandler.reportSuggestions) {
            event.message = getIChatComponentWithSquadnameAsSender(fmsg, messageSender, squadname);
            return;
        }

        if (!isSenderCheating && !isSenderFlaging) {
            SoundUtil.playReportSuggestionSound();
        }

        if (!isSenderInTablist || messageSender == null) {
            final String newFmsg = getReportTextWithFormattedName(fmsg, reportText, reportedPlayer);
            final IChatComponent imsg = getIChatComponentWithSquadnameAsSender(newFmsg, messageSender, squadname);
            addButtons(imsg, reportedPlayer, cheat, isTargetMyself);
            event.message = imsg;
            return;
        }

        if (isSenderCheating) {
            final String msg = StringUtil.insertAfterName(fmsg, messageSender, EnumChatFormatting.YELLOW + " (Cheater)", EnumChatFormatting.GRAY + EnumChatFormatting.STRIKETHROUGH.toString(), true);
            event.message = new ChatComponentText(msg);
            return;
        }

        if (isSenderFlaging) {
            final String newFmsg = StringUtil.insertAfterName(fmsg, messageSender, EnumChatFormatting.LIGHT_PURPLE + " (Scangame)", "", true);
            final IChatComponent imsg = getIChatComponentWithSquadnameAsSender(newFmsg, messageSender, squadname);
            addButtons(imsg, reportedPlayer, cheat, isTargetMyself);
            event.message = imsg;
            return;
        }

        final String newFmsg = getReportTextWithFormattedName(fmsg, reportText, reportedPlayer);
        final IChatComponent imsg = getIChatComponentWithSquadnameAsSender(newFmsg, messageSender, squadname);
        addButtons(imsg, reportedPlayer, cheat, isTargetMyself);
        event.message = imsg;

    }

    private static String getReportTextWithFormattedName(String fmsg, String reportText, String reportedPlayer) {
        if (ScoreboardTracker.isInMwGame) {
            final String newReportText = EnumChatFormatting.DARK_RED + reportText.replace(reportedPlayer, NameUtil.getFormattedNameWithoutIcons(reportedPlayer) + EnumChatFormatting.DARK_RED);
            return StringUtil.replaceTargetWith(fmsg, reportText, newReportText);
        }
        return StringUtil.changeColorOf(fmsg, reportText, EnumChatFormatting.DARK_RED);
    }

    public static List<StringLong> getReportSuggestionHistory() {
        return reportSuggestionHistory;
    }

    private static void addButtons(IChatComponent imsg, String reportedPlayer, String cheat, boolean isTargetMyself) {
        if (isTargetMyself) {
            return;
        }
        imsg.appendSibling(ChatUtil.getReportButton(reportedPlayer, "cheating", ClickEvent.Action.RUN_COMMAND));
        imsg.appendSibling(ChatUtil.getWDRButton(reportedPlayer, cheat, ClickEvent.Action.SUGGEST_COMMAND));
    }

    private static IChatComponent getIChatComponentWithSquadnameAsSender(String fmsg, @Nullable String messageSender, @Nullable String squadname) {
        return new ChatComponentText(messageSender != null && squadname != null ? fmsg.replaceFirst(messageSender, squadname) : fmsg);
    }

    private static boolean isCheatValid(String cheat) {
        return CommandReport.cheatsList.contains(cheat);
    }

    private static boolean isNameValid(String playername) {
        return NetHandlerPlayClientHook.getPlayerInfo(playername) != null || isPlayerMyself(playername) || FinalKillCounter.wasPlayerInThisGame(playername);
    }

    private static boolean isPlayerMyself(@Nullable String name) {
        return (mc.thePlayer != null && mc.thePlayer.getName().equalsIgnoreCase(name)) || (!ConfigHandler.hypixelNick.isEmpty() && ConfigHandler.hypixelNick.equals(name));
    }

    private static void clearReportSuggestionHistory() {
        reportSuggestionHistory.clear();
    }

    /**
     * Mirrors the {@link ReportSuggestionHandler#parseReportMessage}
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
        final NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.getPlayerInfo(reportedPlayer);
        if (networkPlayerInfo == null) return;
        final String uuid = networkPlayerInfo.getGameProfile().getId().toString();
        if (mc.thePlayer != null) {
            new DelayedTask(() -> ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "Shout was blocked, trying to send report suggestion with UUID instead"));
            new DelayedTask(() -> mc.thePlayer.sendChatMessage("/shout " + blockedMessgae.replaceFirst(reportText, reportText.replaceFirst(reportedPlayer, uuid))), 20);
        }
    }

}
