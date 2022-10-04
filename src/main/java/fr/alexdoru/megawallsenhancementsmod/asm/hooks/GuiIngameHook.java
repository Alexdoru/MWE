package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.fkcountermod.events.ScoreboardEvent;
import fr.alexdoru.fkcountermod.gui.FKCounterGui;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.LastWitherHPGui;

@SuppressWarnings("unused")
public class GuiIngameHook {

    public static String cancelHungerTitle(String subtitle) {
        if (subtitle.contains("Get to the middle to stop the hunger!")) {
            return "";
        }
        return subtitle;
    }

    public static String getSidebarTextLine(String textIn, int lineNumber) {
        if (ConfigHandler.witherHUDinSidebar && lineNumber == 12 && ConfigHandler.show_lastWitherHUD && FKCounterMod.isInMwGame && ScoreboardEvent.getMwScoreboardParser().isOnlyOneWitherAlive()) {
            return LastWitherHPGui.instance.displayText;
        }
        if (ConfigHandler.FKHUDinSidebar && lineNumber == 11 && ConfigHandler.show_fkcHUD && FKCounterMod.isInMwGame && KillCounter.getGameId() != null) {
            return FKCounterGui.instance.displayText;
        }
        return textIn;
    }

}
