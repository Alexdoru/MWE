package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.features.FinalKillCounter;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.FKCounterHUD;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.LastWitherHPHUD;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import net.minecraft.client.gui.FontRenderer;

@SuppressWarnings("unused")
public class GuiIngameHook {

    public static String cancelHungerTitle(String subtitle) {
        if (ConfigHandler.hideHungerTitleInMW && subtitle.contains("Get to the center to stop the hunger")) {
            return "";
        }
        return subtitle;
    }

    public static String getSidebarTextLine(String textIn, int lineNumber) {
        if (ConfigHandler.witherHUDinSidebar && lineNumber == 13 && ConfigHandler.showLastWitherHUD && ScoreboardTracker.isInMwGame && ScoreboardTracker.getParser().isOnlyOneWitherAlive()) {
            return LastWitherHPHUD.instance.displayText;
        }
        if (ConfigHandler.fkcounterHUDinSidebar && lineNumber == 12 && ConfigHandler.showfkcounterHUD && ScoreboardTracker.isInMwGame && FinalKillCounter.getGameId() != null) {
            return FKCounterHUD.instance.displayText;
        }
        return textIn;
    }

    public static int getSidebarTextLineWidth(int width, FontRenderer fontRenderer, boolean redNumbers) {
        if (ConfigHandler.witherHUDinSidebar && ConfigHandler.showLastWitherHUD && ScoreboardTracker.isInMwGame && ScoreboardTracker.getParser().isOnlyOneWitherAlive()) {
            width = Math.max(width, fontRenderer.getStringWidth(LastWitherHPHUD.instance.displayText + (redNumbers ? ": 12" : "")));
        }
        if (ConfigHandler.fkcounterHUDinSidebar && ConfigHandler.showfkcounterHUD && ScoreboardTracker.isInMwGame && FinalKillCounter.getGameId() != null) {
            width = Math.max(width, fontRenderer.getStringWidth(FKCounterHUD.instance.displayText + (redNumbers ? ": 11" : "")));
        }
        return width;
    }

}
