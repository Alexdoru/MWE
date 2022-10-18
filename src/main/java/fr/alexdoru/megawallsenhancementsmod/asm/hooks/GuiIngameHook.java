package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.events.KillCounter;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.events.ScoreboardEvent;
import fr.alexdoru.megawallsenhancementsmod.gui.FKCounterHUD;
import fr.alexdoru.megawallsenhancementsmod.gui.LastWitherHPHUD;

@SuppressWarnings("unused")
public class GuiIngameHook {

    public static String cancelHungerTitle(String subtitle) {
        if (subtitle.contains("Get to the middle to stop the hunger!")) {
            return "";
        }
        return subtitle;
    }

    public static String getSidebarTextLine(String textIn, int lineNumber) {
        if (ConfigHandler.witherHUDinSidebar && lineNumber == 12 && ConfigHandler.showLastWitherHUD && FKCounterMod.isInMwGame && ScoreboardEvent.getMwScoreboardParser().isOnlyOneWitherAlive()) {
            return LastWitherHPHUD.instance.displayText;
        }
        if (ConfigHandler.fkcounterHUDinSidebar && lineNumber == 11 && ConfigHandler.showfkcounterHUD && FKCounterMod.isInMwGame && KillCounter.getGameId() != null) {
            return FKCounterHUD.instance.displayText;
        }
        return textIn;
    }

}
