package fr.alexdoru.nocheatersmod.util;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.fkcountermod.utils.DelayedTask;
import fr.alexdoru.megawallsenhancementsmod.asm.hooks.NetHandlerPlayClientHook;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandScanGame;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.data.StringLong;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.StringUtil;
import fr.alexdoru.nocheatersmod.commands.CommandReport;
import fr.alexdoru.nocheatersmod.commands.CommandWDR;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import fr.alexdoru.nocheatersmod.events.ReportQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.*;

public class ReportSuggestionHandler {

    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final ResourceLocation REPORT_SUGGESTION_SOUND = new ResourceLocation("random.orb");
    private static final Pattern REPORT_PATTERN1 = Pattern.compile("([a-zA-Z0-9_*]{2,16}) (?:|is )b?hop?ping", Pattern.CASE_INSENSITIVE);
    private static final Pattern REPORT_PATTERN2 = Pattern.compile("\\/?(?:wdr|report) ([a-zA-Z0-9_*]{2,16}) (\\w+)", Pattern.CASE_INSENSITIVE);
    private static final List<StringLong> reportSuggestionHistory = new ArrayList<>();
    private static final List<Long> reportSpamCheck = new ArrayList<>();
    private static final long TIME_BETWEEN_REPORT_SUGGESTION_PLAYER = 40L * 60L * 1000L;

    public static boolean parseReportMessage(@Nullable String senderRank, @Nullable String messageSender, @Nullable String squadname, String msgIn, String fmsgIn) {

        if (ConfigHandler.reportsuggestions || ConfigHandler.autoreportSuggestions) {

            Matcher matcher1 = REPORT_PATTERN1.matcher(msgIn);
            Matcher matcher2 = REPORT_PATTERN2.matcher(msgIn);

            if (matcher1.find()) {
                String reportText = matcher1.group();
                String reportedPlayer = matcher1.group(1);
                String uncensoredName = null;
                if (reportedPlayer.contains("****")) {
                    uncensoredName = findPlayernameWithCorrectCase(reportedPlayer.replace("****", "seks"));
                }
                if (uncensoredName != null) {
                    fmsgIn = fmsgIn.replace(reportedPlayer, uncensoredName);
                    reportText = reportText.replace(reportedPlayer, uncensoredName);
                    reportedPlayer = uncensoredName;
                }
                if (isAValidName(reportedPlayer)) {
                    handleReportSuggestion(reportedPlayer, senderRank, messageSender, squadname, reportText, "bhop", fmsgIn);
                } else {
                    addChatMessage(getIChatComponentWithSquadnameAsSender(fmsgIn, messageSender, squadname));
                }
                return true;
            } else if (matcher2.find()) {
                String reportText = matcher2.group();
                String reportedPlayer = matcher2.group(1);
                String cheat = matcher2.group(2);
                String uncensoredName = null;
                if (reportedPlayer.contains("****")) {
                    uncensoredName = findPlayernameWithCorrectCase(reportedPlayer.replace("****", "seks"));
                }
                if (uncensoredName != null) {
                    fmsgIn = fmsgIn.replace(reportedPlayer, uncensoredName);
                    reportText = reportText.replace(reportedPlayer, uncensoredName);
                    reportedPlayer = uncensoredName;
                }
                if (isAValidCheat(cheat) && isAValidName(reportedPlayer)) {
                    handleReportSuggestion(reportedPlayer, senderRank, messageSender, squadname, reportText, cheat, fmsgIn);
                } else {
                    addChatMessage(getIChatComponentWithSquadnameAsSender(fmsgIn, messageSender, squadname));
                }
                return true;
            }

        }

        return false;

    }

    public static void playReportSuggestionSound(boolean playSound) {
        if (playSound) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(REPORT_SUGGESTION_SOUND, 1.0F));
        }
    }

    /**
     * reportedPlayer is necessarily in the tablist
     */
    private static void handleReportSuggestion(String reportedPlayer, @Nullable String senderRank, @Nullable String messageSender, @Nullable String squadname, String reportText, String cheat, String fmsg) {

        boolean isSenderMyself = isPlayerMyself(messageSender);
        boolean isTargetMyself = isPlayerMyself(reportedPlayer);
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

            NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.playerInfoMap.get(messageSender);
            if (networkPlayerInfo != null) {
                isSenderInTablist = true;
                final UUID id = networkPlayerInfo.getGameProfile().getId();
                isSenderNicked = !NameUtil.isRealPlayer(id);
                senderUUID = id.toString().replace("-", "");
                isSenderFlaging = CommandScanGame.doesPlayerFlag(id);
                final WDR wdr = WdredPlayers.getPlayer(senderUUID, messageSender);
                if (wdr != null) {
                    isSenderIgnored = wdr.isIgnored();
                    isSenderCheating = wdr.hasValidCheats();
                }
            }

        }

        if (isSenderNicked || senderRank != null && (senderRank.equals("VIP+") || senderRank.equals("MVP") || senderRank.equals("MVP+") || senderRank.equals("MVP++"))) {
            isSenderRankValid = true;
        }

        boolean gotAutoreported = checkAndSendReportSuggestion(messageSender, reportedPlayer, cheat, isSenderMyself, isTargetMyself, isSenderInTablist, isSenderIgnored, isSenderCheating, isSenderFlaging, false, isSenderRankValid);
        printCustomReportSuggestionChatText(fmsg, messageSender, reportedPlayer, cheat, reportText, squadname, isSenderMyself, isTargetMyself, isSenderInTablist, isSenderIgnored, isSenderCheating, isSenderFlaging, false, gotAutoreported, senderUUID);

    }

    private static boolean checkAndSendReportSuggestion(@Nullable String messageSender, String reportedPlayer, String cheat, boolean isSenderMyself, boolean isTargetMyself, boolean isSenderInTablist, boolean isSenderIgnored, boolean isSenderCheating, boolean isSenderFlaging, boolean isSenderNicked, boolean isSenderRankValid) {

        if (!ConfigHandler.autoreportSuggestions || !isSenderInTablist || messageSender == null || isSenderIgnored || isSenderCheating || !FKCounterMod.isInMwGame || isTargetMyself) {
            return false;
        }

        if (isSenderMyself) {
            String[] args = new String[]{reportedPlayer, cheat};
            CommandWDR.handleWDRCommand(args, true, canWDRPlayer(reportedPlayer));
        }

        if (FKCounterMod.isitPrepPhase) {
            if (isSenderMyself) {
                new DelayedTask(() -> addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "\u2716" + EnumChatFormatting.GRAY + " Cannot share a report before the walls fall!")), 0);
                return true;
            }
            return false;
        }

        if (isSenderFlaging) {
            if (isSenderMyself) {
                new DelayedTask(() -> addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "\u2716" + EnumChatFormatting.GRAY + " You cannot share a report since you flag in /scangame!")), 0);
                return true;
            }
            return false;
        }

        if (isSenderNicked) {
            if (isSenderMyself) {
                new DelayedTask(() -> addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "\u2716" + EnumChatFormatting.GRAY + " You cannot share a report when you are nicked!")), 0);
                return true;
            }
            return false;
        }

        if (!isSenderRankValid && !messageSender.equals(ConfigHandler.hypixelNick)) {
            if (isSenderMyself) {
                new DelayedTask(() -> addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "\u2716" + EnumChatFormatting.GRAY + " You need to be at least " + EnumChatFormatting.GREEN + "VIP" + EnumChatFormatting.GOLD + "+" + EnumChatFormatting.GRAY + " to share a report with others")), 0);
                return true;
            }
            return false;
        }

        if (canReportSuggestionPlayer(reportedPlayer)) {
            if (isSenderMyself) {
                new DelayedTask(() -> addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "\u2714" + EnumChatFormatting.GRAY + " Your report will be shared with other players in the game")), 0);
                return true;
            }
            checkReportSpam();
            if(ReportQueue.INSTANCE.addPlayerToQueueRandom(messageSender, reportedPlayer)) {
                new DelayedTask(() -> addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "\u2714" + EnumChatFormatting.GRAY + " Sending report in a moment... ").appendSibling(ChatUtil.getCancelButton(reportedPlayer))), 0);
                return true;
            } else {
                new DelayedTask(() -> addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "\u2714" + EnumChatFormatting.GRAY + " You already reported this player during this game")), 0);
            }
            return false;
        } else {
            if (isSenderMyself) {
                new DelayedTask(() -> addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "\u2716" + EnumChatFormatting.GRAY + " This player has already been reported during this game")), 0);
                return true;
            } else {
                new DelayedTask(() -> addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "\u2714" + EnumChatFormatting.GRAY + " You already reported this player during this game")), 0);
            }
        }

        return false;

    }

    private static void checkReportSpam() {
        long l = System.currentTimeMillis();
        reportSpamCheck.add(l);
        reportSpamCheck.removeIf(time -> (time + 30L * 1000L < l));
        if (reportSpamCheck.size() >= 4) {
            addChatMessage(new ChatComponentText(getTagNoCheaters() + EnumChatFormatting.YELLOW + "Is someone trying to spam the reporting system ? ").appendSibling(ChatUtil.getCancelAllReportsButton()));
        }
    }

    private static void printCustomReportSuggestionChatText(String fmsg, @Nullable String messageSender, String reportedPlayer, String cheat, String reportText, @Nullable String squadname, boolean isSenderMyself, boolean isTargetMyself, boolean isSenderInTablist, boolean isSenderIgnored, boolean isSenderCheating, boolean isSenderFlaging, boolean isSenderNicked, boolean gotAutoreported, @Nullable String senderUUID) {

        if (!ConfigHandler.reportsuggestions) {
            addChatMessage(getIChatComponentWithSquadnameAsSender(fmsg, messageSender, squadname));
            return;
        }

        playReportSuggestionSound(!isSenderIgnored && !isSenderCheating && !isSenderFlaging);

        if (!isSenderInTablist || messageSender == null) {
            final String newFmsg = StringUtil.changeColorOf(fmsg, reportText, EnumChatFormatting.DARK_RED) + " ";
            final IChatComponent imsg = getIChatComponentWithSquadnameAsSender(newFmsg, messageSender, squadname);
            addButtons(imsg, reportedPlayer, cheat, isSenderMyself, isTargetMyself, gotAutoreported);
            addChatMessage(imsg);
            return;
        }

        if (isSenderIgnored) {
            final IChatComponent imsg = new ChatComponentText(StringUtil.insertAfterName(fmsg, messageSender, EnumChatFormatting.GRAY + " (Ignored)", EnumChatFormatting.GRAY + EnumChatFormatting.STRIKETHROUGH.toString(), true));
            if (senderUUID != null) {
                imsg.appendSibling(ChatUtil.getUnIgnoreButton(senderUUID, messageSender));
            }
            addChatMessage(imsg);
            return;
        }

        if (isSenderCheating) {
            addChatMessage(new ChatComponentText(StringUtil.insertAfterName(fmsg, messageSender, EnumChatFormatting.YELLOW + " (Cheater)", EnumChatFormatting.GRAY + EnumChatFormatting.STRIKETHROUGH.toString(), true)));
            return;
        }

        if (isSenderFlaging) {
            final String newFmsg = StringUtil.insertAfterName(fmsg, messageSender, EnumChatFormatting.LIGHT_PURPLE + " (Scangame)", "", true);
            final IChatComponent imsg = getIChatComponentWithSquadnameAsSender(newFmsg, messageSender, squadname);
            if (FKCounterMod.isMWEnvironement && !isSenderMyself) {
                imsg.appendSibling(getIgnoreButton(messageSender));
            }
            addButtons(imsg, reportedPlayer, cheat, isSenderMyself, isTargetMyself, gotAutoreported);
            addChatMessage(imsg);
            return;
        }

        if (isSenderNicked) {
            final String s1 = StringUtil.insertAfterName(fmsg, messageSender, EnumChatFormatting.DARK_PURPLE + " (Nick)", "", false);
            final String newFmsg = StringUtil.changeColorOf(s1, reportText, EnumChatFormatting.DARK_RED) + " ";
            final IChatComponent imsg = getIChatComponentWithSquadnameAsSender(newFmsg, messageSender, squadname);
            addButtons(imsg, reportedPlayer, cheat, isSenderMyself, isTargetMyself, gotAutoreported);
            addChatMessage(imsg);
            return;
        }

        final String newFmsg = StringUtil.changeColorOf(fmsg, reportText, EnumChatFormatting.DARK_RED) + " ";
        final IChatComponent imsg = getIChatComponentWithSquadnameAsSender(newFmsg, messageSender, squadname);
        if (FKCounterMod.isMWEnvironement && !isSenderMyself) {
            imsg.appendSibling(getIgnoreButton(messageSender));
        }
        addButtons(imsg, reportedPlayer, cheat, isSenderMyself, isTargetMyself, gotAutoreported);
        addChatMessage(imsg);

    }

    private static boolean canReportSuggestionPlayer(String playername) {
        long timestamp = System.currentTimeMillis();
        reportSuggestionHistory.removeIf(o -> (o.timestamp + TIME_BETWEEN_REPORT_SUGGESTION_PLAYER < timestamp));
        for (StringLong stringLong : reportSuggestionHistory) {
            if (stringLong.message != null && stringLong.message.equalsIgnoreCase(playername)) {
                return false;
            }
        }
        reportSuggestionHistory.add(new StringLong(timestamp, playername));
        return true;
    }

    private static boolean canWDRPlayer(String playername) {
        long timestamp = System.currentTimeMillis();
        reportSuggestionHistory.removeIf(o -> (o.timestamp + TIME_BETWEEN_REPORT_SUGGESTION_PLAYER < timestamp));
        for (StringLong stringLong : reportSuggestionHistory) {
            if (stringLong.message != null && stringLong.message.equalsIgnoreCase(playername)) {
                return false;
            }
        }
        return true;
    }

    private static void addButtons(IChatComponent imsg, String reportedPlayer, String cheat, boolean isSenderMyself, boolean isTargetMyself, boolean gotautoreported) {
        if (isTargetMyself) {
            return;
        }
        if (!gotautoreported) {
            imsg.appendSibling(getReportButton(reportedPlayer, "cheating", ClickEvent.Action.RUN_COMMAND));
        }
        if (!isSenderMyself || !gotautoreported) {
            imsg.appendSibling(getWDRButton(reportedPlayer, cheat, ClickEvent.Action.SUGGEST_COMMAND));
        }
    }

    private static IChatComponent getIChatComponentWithSquadnameAsSender(String fmsg, @Nullable String messageSender, @Nullable String squadname) {
        return new ChatComponentText(messageSender != null && squadname != null ? fmsg.replaceFirst(messageSender, squadname) : fmsg);
    }

    private static boolean isAValidCheat(String cheat) {
        return CommandReport.cheatsList.contains(cheat);
    }

    private static boolean isAValidName(String playername) {
        return NetHandlerPlayClientHook.playerInfoMap.get(playername) != null || isPlayerMyself(playername) || KillCounter.wasPlayerInThisGame(playername);
    }

    private static boolean isPlayerMyself(@Nullable String name) {
        return (mc.thePlayer != null && mc.thePlayer.getName().equalsIgnoreCase(name)) || (!ConfigHandler.hypixelNick.equals("") && ConfigHandler.hypixelNick.equals(name));
    }

    public static void clearReportSuggestionHistory() {
        reportSuggestionHistory.clear();
    }

    private static String findPlayernameWithCorrectCase(String nameIn) {
        for (NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (networkPlayerInfo.getGameProfile().getName().equalsIgnoreCase(nameIn)) {
                return networkPlayerInfo.getGameProfile().getName();
            }
        }
        return null;
    }

}
