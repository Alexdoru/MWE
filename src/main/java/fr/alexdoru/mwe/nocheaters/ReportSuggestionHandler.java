package fr.alexdoru.mwe.nocheaters;

import fr.alexdoru.mwe.asm.hooks.NetHandlerPlayClientHook_PlayerMapTracker;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.commands.CommandReport;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.data.ScangameData;
import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.NameUtil;
import fr.alexdoru.mwe.utils.SoundUtil;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportSuggestionHandler {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Pattern REPORT_PATTERN1 = Pattern.compile("(\\w{2,16}) (?:|is )b?hop?ping", Pattern.CASE_INSENSITIVE);
    private static final Pattern REPORT_PATTERN2 = Pattern.compile("/?(?:wdr|report) (\\w{2,16}) (\\w{2,16})", Pattern.CASE_INSENSITIVE);

    public static boolean processMessage(
            ClientChatReceivedEvent event,
            @Nullable String messageSender,
            @Nullable String squadname,
            String fmsgIn, String msgIn) {
        if (MWEConfig.reportSuggestions) {
            final Matcher matcher1 = REPORT_PATTERN1.matcher(msgIn);
            final Matcher matcher2 = REPORT_PATTERN2.matcher(msgIn);
            if (matcher1.find()) {
                final String reportText = matcher1.group();
                final String reportedPlayer = matcher1.group(1);
                if (isNameValid(reportedPlayer)) {
                    handleReportSuggestion(event, reportedPlayer, messageSender, squadname, reportText, "bhop", fmsgIn);
                    return true;
                }
            } else if (matcher2.find()) {
                final String reportText = matcher2.group();
                final String reportedPlayer = matcher2.group(1);
                final String cheat = matcher2.group(2).toLowerCase();
                if (isNameValid(reportedPlayer) && isCheatValid(cheat)) {
                    handleReportSuggestion(event, reportedPlayer, messageSender, squadname, reportText, cheat, fmsgIn);
                    return true;
                }
            }
        }
        return false;
    }

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

        if (isSenderMyself) {

            isSenderInTablist = true;

        } else if (messageSender != null) {

            final NetworkPlayerInfo netInfo = NetHandlerPlayClientHook_PlayerMapTracker.getPlayerInfo(messageSender);
            if (netInfo != null) {
                isSenderInTablist = true;
                final UUID uuid = netInfo.getGameProfile().getId();
                isSenderFlaging = ScangameData.doesPlayerFlag(uuid);
                final WDR wdr = WdrData.getWdr(uuid, messageSender);
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

        if (!isSenderCheating && !isSenderFlaging) {
            SoundUtil.playChatNotifSound();
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
        if (ScoreboardTracker.isInMwGame()) {
            final String newReportText = EnumChatFormatting.DARK_RED + reportText.replace(reportedPlayer, NameUtil.getFormattedNameWithoutIcons(reportedPlayer) + EnumChatFormatting.DARK_RED);
            return StringUtil.replaceTargetWith(fmsg, reportText, newReportText);
        }
        return StringUtil.changeColorOf(fmsg, reportText, EnumChatFormatting.DARK_RED);
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
        return NetHandlerPlayClientHook_PlayerMapTracker.getPlayerInfo(playername) != null || isPlayerMyself(playername) || FinalKillCounter.wasPlayerInThisGame(playername);
    }

    private static boolean isPlayerMyself(@Nullable String name) {
        return (mc.thePlayer != null && mc.thePlayer.getName().equalsIgnoreCase(name)) || (!MWEConfig.hypixelNick.isEmpty() && MWEConfig.hypixelNick.equals(name));
    }

}
