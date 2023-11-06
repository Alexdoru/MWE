package fr.alexdoru.megawallsenhancementsmod.chat;

import fr.alexdoru.megawallsenhancementsmod.asm.hooks.GuiScreenHook;
import fr.alexdoru.megawallsenhancementsmod.data.WDR;
import fr.alexdoru.megawallsenhancementsmod.data.WdrData;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportQueue;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Date;

public class WarningMessagesHandler {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Called when you type /nocheaters
     */
    public static void printReportMessagesForWorld(boolean callFromCommand) {
        ChatHandler.deleteAllWarningMessages();
        boolean foundReport = false;
        final long datenow = (new Date()).getTime();
        for (final NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {
            final String uuid = networkPlayerInfo.getGameProfile().getId().toString().replace("-", "");
            final String playerName = networkPlayerInfo.getGameProfile().getName();
            final WDR wdr = WdrData.getWdr(uuid, playerName);
            if (wdr == null) {
                continue;
            }
            foundReport = true;
            final boolean gotautoreported = ReportQueue.INSTANCE.addAutoReportToQueue(datenow, playerName, wdr);
            if (wdr.transformName()) {
                printWarningMessage(
                        datenow,
                        uuid,
                        (!ScoreboardTracker.isInMwGame || ScoreboardTracker.isPrepPhase) ? null : ScorePlayerTeam.formatPlayerName(networkPlayerInfo.getPlayerTeam(), playerName),
                        playerName,
                        wdr,
                        gotautoreported
                );
            }
        }
        if (callFromCommand && !foundReport) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "No reported player here !");
        }
    }

    public static void printWarningMessage(long datenow, String uuid, String formattedName, String playername, WDR wdr, boolean disableReportButton) {

        final String wdrmapKey = wdr.isNicked() ? playername : uuid;
        final IChatComponent imsg = new ChatComponentText(EnumChatFormatting.RED + "Warning : ").appendSibling(createPlayerNameWithHoverText(formattedName, playername, wdrmapKey, wdr, EnumChatFormatting.LIGHT_PURPLE));
        final IChatComponent allCheats = wdr.getFormattedHacks();
        imsg.appendText(EnumChatFormatting.GRAY + " joined,");
        final boolean olderThanMaxAutoreport = wdr.shouldPrintBigText(datenow);

        if (olderThanMaxAutoreport) {
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GREEN + " [Report Player]").setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Click here to continue auto-reporting that player every game")))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, GuiScreenHook.SEND_REPORT_AGAIN + wdrmapKey + " " + playername))));
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.YELLOW + " [Remove Player]").setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Click here to remove this player from your report list\n"
                            + EnumChatFormatting.YELLOW + "and stop auto-reporting them every game")))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unwdr " + wdrmapKey + " " + playername))));
            imsg.appendText(EnumChatFormatting.RED + " It's been " + EnumChatFormatting.GRAY + DateUtil.timeSince(wdr.timeLastManualReport) + EnumChatFormatting.RED + " since you last manually reported that player for :")
                    .appendSibling(allCheats)
                    .appendText(EnumChatFormatting.RED + ", either remove them from your report list or report them again.");
        } else if (!disableReportButton && ScoreboardTracker.isInMwGame && !ScoreboardTracker.isPrepPhase && wdr.canBeReported(datenow)) {
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_GREEN + " [Report again]").setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Click here to report this player again")))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, GuiScreenHook.SEND_REPORT_AGAIN + wdrmapKey + " " + playername))));
        }

        if (!ScoreboardTracker.isPreGameLobby) {
            allCheats.setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Click this message to report this player" + "\n"
                            + EnumChatFormatting.YELLOW + "Command : " + EnumChatFormatting.RED + "/report " + playername + " cheating" + ChatUtil.getReportingAdvice())))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + playername + " cheating")));
        }

        if (!olderThanMaxAutoreport) {
            imsg.appendText(EnumChatFormatting.GRAY + " Cheats :");
            imsg.appendSibling(allCheats);
        }

        ChatUtil.addChatMessage(imsg);

    }

    /**
     * Returns a message with the player name and a hover event on top with the report info
     */
    public static IChatComponent createPlayerNameWithHoverText(String formattedNameIn, String playername, String wdrmapKey, WDR wdr, EnumChatFormatting namecolor) {
        final String formattedName = formattedNameIn == null ? namecolor.toString() + playername : formattedNameIn;
        return new ChatComponentText(formattedName).setChatStyle(new ChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unwdr " + wdrmapKey + " " + playername))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                        formattedName + "\n"
                                //+ EnumChatFormatting.GREEN + "Last auto report : " + EnumChatFormatting.YELLOW + DateUtil.localformatTimestamp(wdr.timestamp) + "\n"
                                + EnumChatFormatting.GREEN + "Last reported : " + EnumChatFormatting.YELLOW + DateUtil.timeSince(wdr.timeLastManualReport) + " ago, on " + DateUtil.localformatTimestamp(wdr.timeLastManualReport) + "\n"
                                + EnumChatFormatting.GREEN + "Reported for :" + EnumChatFormatting.GOLD + wdr.hacksToString() + "\n\n"
                                + EnumChatFormatting.YELLOW + "Click here to remove this player from your report list"))));

    }

}
