package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.MWGameStatsEvent;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.ClipboardUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import fr.alexdoru.nocheatersmod.data.WDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import fr.alexdoru.nocheatersmod.events.ReportQueue;
import net.minecraft.util.ChatComponentText;
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
            if (MWGameStatsEvent.getGameStats() != null) {
                ChatUtil.addChatMessage(MWGameStatsEvent.getGameStats().getGameStatMessage(MWGameStatsEvent.getFormattedname()));
            } else {
                ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + "No game stats available"));
            }
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
        String uuid = args[1];
        String playername = args[2];
        WDR wdr = WdredPlayers.getWdredMap().get(uuid);
        if (wdr != null) {
            if (wdr.hasValidCheats()) {
                long time = (new Date()).getTime();
                if (FKCounterMod.preGameLobby && ConfigHandler.toggleautoreport) {
                    wdr.timestamp = time - WDR.TIME_BETWEEN_AUTOREPORT;
                    wdr.timeLastManualReport = time - WDR.TIME_BETWEEN_AUTOREPORT;
                    ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "Your cheating report against " + EnumChatFormatting.RED + playername + EnumChatFormatting.GREEN + " will be sent during the game."));
                } else {
                    ReportQueue.INSTANCE.addPlayerToQueue(playername, true);
                    wdr.timestamp = time;
                    wdr.timeLastManualReport = time;
                }
                NameUtil.updateGameProfileAndName(playername, false);
            } else {
                ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Those cheats aren't recognized by the mod :" + EnumChatFormatting.GOLD + wdr.hacksToString() + EnumChatFormatting.RED + ", use valid cheats to use the reporting features."));
            }
        }
    }

}
