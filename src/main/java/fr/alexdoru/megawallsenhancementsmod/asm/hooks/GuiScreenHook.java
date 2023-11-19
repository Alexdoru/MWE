package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatHandler;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.features.MegaWallsEndGameStats;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.WDR;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.WdrData;
import fr.alexdoru.megawallsenhancementsmod.utils.ClipboardUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.util.Date;

@SuppressWarnings("unused")
public class GuiScreenHook {

    public static final String COPY_TO_CLIPBOARD_COMMAND = "/copyToClipBoard ";
    public static final String MW_GAME_END_STATS = "/gamestatsmw";
    public static final String SEND_REPORT_AGAIN = "/sendreportagain ";

    /**
     * Returns true if it handles a custom click event
     */
    public static boolean handleMWEnCustomChatCommand(String command) {
        if (command != null && command.startsWith(COPY_TO_CLIPBOARD_COMMAND)) {
            ClipboardUtil.copyString(command.replaceFirst(COPY_TO_CLIPBOARD_COMMAND, ""));
            return true;
        }
        if (MW_GAME_END_STATS.equals(command)) {
            MegaWallsEndGameStats.printGameStatsMessage();
            return true;
        }
        if (command != null && command.startsWith(SEND_REPORT_AGAIN)) {
            final String[] args = command.split(" ");
            handleSendReportAgain(args);
            return true;
        }
        return false;
    }

    /**
     * Format to use this command : "/sendreportagain <UUID> <playerName>"
     */
    private static void handleSendReportAgain(String[] args) {
        if (args.length < 3) {
            return;
        }
        final String uuid = args[1];
        final String playername = args[2];
        final WDR wdr = WdrData.getWdr(uuid);
        if (wdr != null) {
            if (wdr.hasValidCheats()) {
                wdr.time = new Date().getTime();
                if (Minecraft.getMinecraft().thePlayer != null) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/wdr " + playername);
                }
                ChatHandler.deleteWarningMessagesFor(playername);
                NameUtil.updateMWPlayerDataAndEntityData(playername, false);
            } else {
                ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Those cheats aren't recognized by the mod :" + EnumChatFormatting.GOLD + wdr.hacksToString() + EnumChatFormatting.RED + ", use valid cheats to use the reporting features.");
            }
        }
    }

}
