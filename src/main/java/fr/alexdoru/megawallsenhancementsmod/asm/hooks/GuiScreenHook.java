package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.features.MegaWallsEndGameStats;
import fr.alexdoru.megawallsenhancementsmod.utils.ClipboardUtil;

@SuppressWarnings("unused")
public class GuiScreenHook {

    public static final String COPY_TO_CLIPBOARD_COMMAND = "/copyToClipBoard ";
    public static final String MW_GAME_END_STATS = "/gamestatsmw";

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
        return false;
    }

}
