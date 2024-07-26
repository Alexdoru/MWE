package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.gui.guiapi.GuiManager;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.gui.FontRenderer;

@SuppressWarnings("unused")
public class GuiIngameHook_CustomSidebarLines {

    public static String getSidebarTextLine(String textIn, int lineNumber) {
        if (MWEConfig.witherHUDinSidebar && lineNumber == 13 && MWEConfig.showLastWitherHUD && ScoreboardTracker.isInMwGame() && ScoreboardTracker.getParser().isOnlyOneWitherAlive()) {
            return GuiManager.lastWitherHPHUD.displayText;
        }
        if (MWEConfig.fkcounterHUDinSidebar && lineNumber == 12 && MWEConfig.showfkcounterHUD && ScoreboardTracker.isInMwGame() && FinalKillCounter.getGameId() != null) {
            return GuiManager.fkCounterHUD.displayText;
        }
        return textIn;
    }

    public static int getSidebarTextLineWidth(int width, FontRenderer fontRenderer, boolean redNumbers) {
        if (MWEConfig.witherHUDinSidebar && MWEConfig.showLastWitherHUD && ScoreboardTracker.isInMwGame() && ScoreboardTracker.getParser().isOnlyOneWitherAlive()) {
            width = Math.max(width, fontRenderer.getStringWidth(GuiManager.lastWitherHPHUD.displayText + (redNumbers ? ": 12" : "")));
        }
        if (MWEConfig.fkcounterHUDinSidebar && MWEConfig.showfkcounterHUD && ScoreboardTracker.isInMwGame() && FinalKillCounter.getGameId() != null) {
            width = Math.max(width, fontRenderer.getStringWidth(GuiManager.fkCounterHUD.displayText + (redNumbers ? ": 11" : "")));
        }
        return width;
    }

}
