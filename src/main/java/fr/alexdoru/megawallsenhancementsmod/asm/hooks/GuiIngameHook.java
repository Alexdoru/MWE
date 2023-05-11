package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.KillCounter;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.FKCounterHUD;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.LastWitherHPHUD;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import net.minecraft.client.gui.FontRenderer;

@SuppressWarnings("unused")
public class GuiIngameHook {

    public static String cancelHungerTitle(String subtitle) {
        if (ConfigHandler.hideHungerTitleInMW && subtitle.contains("Get to the middle to stop the hunger!")) {
            return "";
        }
        return subtitle;
    }

    public static String getSidebarTextLine(String textIn, int lineNumber) {
        if (ConfigHandler.witherHUDinSidebar && lineNumber == 12 && ConfigHandler.showLastWitherHUD && FKCounterMod.isInMwGame && ScoreboardTracker.getMwScoreboardParser().isOnlyOneWitherAlive()) {
            return LastWitherHPHUD.instance.displayText;
        }
        if (ConfigHandler.fkcounterHUDinSidebar && lineNumber == 11 && ConfigHandler.showfkcounterHUD && FKCounterMod.isInMwGame && KillCounter.getGameId() != null) {
            return FKCounterHUD.instance.displayText;
        }
        return textIn;
    }

    public static int getSidebarTextLineWidth(int width, FontRenderer fontRenderer, boolean redNumbers) {
        if (ConfigHandler.witherHUDinSidebar && ConfigHandler.showLastWitherHUD && FKCounterMod.isInMwGame && ScoreboardTracker.getMwScoreboardParser().isOnlyOneWitherAlive()) {
            width = Math.max(width, fontRenderer.getStringWidth(LastWitherHPHUD.instance.displayText + (redNumbers ? ": 12" : "")));
        }
        if (ConfigHandler.fkcounterHUDinSidebar && ConfigHandler.showfkcounterHUD && FKCounterMod.isInMwGame && KillCounter.getGameId() != null) {
            width = Math.max(width, fontRenderer.getStringWidth(FKCounterHUD.instance.displayText + (redNumbers ? ": 11" : "")));
        }
        return width;
    }

}
