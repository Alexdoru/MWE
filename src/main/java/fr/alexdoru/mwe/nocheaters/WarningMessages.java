package fr.alexdoru.mwe.nocheaters;

import fr.alexdoru.mwe.chat.ChatHandler;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.chat.WarningChatComponent;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.DateUtil;
import fr.alexdoru.mwe.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import java.util.UUID;

import static net.minecraft.util.EnumChatFormatting.*;

public class WarningMessages {

    public static void printReportMessagesForWorld(boolean callFromCommand) {
        ChatHandler.deleteAllWarningMessages();
        boolean foundReport = false;
        for (final NetworkPlayerInfo netInfo : NameUtil.sortedCopyOf(Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap())) {
            final UUID uuid = netInfo.getGameProfile().getId();
            final String playerName = netInfo.getGameProfile().getName();
            final WDR wdr = WdrData.getWdr(uuid, playerName);
            if (wdr == null) {
                continue;
            }
            foundReport = true;
            printWarningMessage(uuid, netInfo.getPlayerTeam(), playerName, wdr);
        }
        if (callFromCommand && !foundReport) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + GREEN + "No reported player here !");
        }
    }

    public static void printWarningMessage(UUID uuid, Team team, String playername, WDR wdr) {
        final String wdrmapKey = uuid.version() == 4 ? uuid.toString() : playername;
        final IChatComponent imsg = new WarningChatComponent(playername, RED + "Warning : ")
                .appendSibling(getPlayernameWithHoverText(null, team, playername, wdrmapKey, wdr))
                .appendText(GRAY + " joined, Cheats :");
        ChatUtil.addSkinToComponent(imsg, playername);
        final IChatComponent allCheats = wdr.getFormattedCheats();
        if (!ScoreboardTracker.isPreGameLobby()) {
            allCheats.setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(GREEN + "Click this message to report this player" + "\n"
                            + YELLOW + "Command : " + RED + "/report " + playername + " cheating" + ChatUtil.getReportingAdvice())))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + playername + " cheating")));
        }
        imsg.appendSibling(allCheats);
        ChatUtil.addChatMessage(imsg);
    }

    public static IChatComponent getPlayernameWithHoverText(String formattedName, Team team, String playername, String wdrmapKey, WDR wdr) {
        if (formattedName == null) {
            formattedName = NameUtil.formatPlayerNameUnscrambled(team, playername);
        }
        return new ChatComponentText(formattedName).setChatStyle(new ChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unwdr " + wdrmapKey + " " + playername))
                .setChatHoverEvent(getWDRHoverEvent(formattedName, wdr)));
    }

    public static HoverEvent getWDRHoverEvent(String formattedName, WDR wdr) {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                formattedName + "\n"
                        + GREEN + "Last reported : " + YELLOW + DateUtil.timeSince(wdr.getTimestamp()) + " ago, on " + DateUtil.localformatTimestamp(wdr.getTimestamp()) + "\n"
                        + GREEN + "Reported for :" + GOLD + wdr.cheatsToString() + "\n\n"
                        + YELLOW + "Click here to remove this player from your report list"));
    }

}
