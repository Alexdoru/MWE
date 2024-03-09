package fr.alexdoru.megawallsenhancementsmod.nocheaters;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatHandler;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.chat.WarningChatComponent;
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

import java.util.UUID;

public class WarningMessagesHandler {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Called when you type /nocheaters
     */
    public static void printReportMessagesForWorld(boolean callFromCommand) {
        ChatHandler.deleteAllWarningMessages();
        boolean foundReport = false;
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            final UUID uuid = netInfo.getGameProfile().getId();
            final String playerName = netInfo.getGameProfile().getName();
            final WDR wdr = WdrData.getWdr(uuid, playerName);
            if (wdr == null) {
                continue;
            }
            foundReport = true;
            printWarningMessage(uuid, ScorePlayerTeam.formatPlayerName(netInfo.getPlayerTeam(), playerName), playerName, wdr);
        }
        if (callFromCommand && !foundReport) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "No reported player here !");
        }
    }

    public static void printWarningMessage(UUID uuid, String formattedName, String playername, WDR wdr) {
        final String wdrmapKey = uuid.version() == 4 ? uuid.toString() : playername;
        final IChatComponent imsg = new WarningChatComponent(playername, EnumChatFormatting.RED + "Warning : ")
                .appendSibling(createPlayerNameWithHoverText(formattedName, playername, wdrmapKey, wdr, EnumChatFormatting.LIGHT_PURPLE))
                .appendText(EnumChatFormatting.GRAY + " joined, Cheats :");
        final IChatComponent allCheats = wdr.getFormattedHacks();
        if (!ScoreboardTracker.isPreGameLobby) {
            allCheats.setChatStyle(new ChatStyle()
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GREEN + "Click this message to report this player" + "\n"
                            + EnumChatFormatting.YELLOW + "Command : " + EnumChatFormatting.RED + "/report " + playername + " cheating" + ChatUtil.getReportingAdvice())))
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + playername + " cheating")));
        }
        imsg.appendSibling(allCheats);
        ChatUtil.addChatMessage(imsg);
    }

    /**
     * Returns a message with the player name and a hover event on top with the report info
     */
    public static IChatComponent createPlayerNameWithHoverText(String formattedName, String playername, String wdrmapKey, WDR wdr, EnumChatFormatting namecolor) {
        formattedName = formattedName == null || formattedName.contains(EnumChatFormatting.OBFUSCATED.toString()) ? namecolor.toString() + playername : formattedName;
        return new ChatComponentText(formattedName).setChatStyle(new ChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/unwdr " + wdrmapKey + " " + playername))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(
                        formattedName + "\n"
                                + EnumChatFormatting.GREEN + "Last reported : " + EnumChatFormatting.YELLOW + DateUtil.timeSince(wdr.time) + " ago, on " + DateUtil.localformatTimestamp(wdr.time) + "\n"
                                + EnumChatFormatting.GREEN + "Reported for :" + EnumChatFormatting.GOLD + wdr.hacksToString() + "\n\n"
                                + EnumChatFormatting.YELLOW + "Click here to remove this player from your report list"))));
    }

}
