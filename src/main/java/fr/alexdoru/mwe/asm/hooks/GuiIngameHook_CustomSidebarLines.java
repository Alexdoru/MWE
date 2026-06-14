package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.gui.MWERendererManager;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.gui.FontRenderer;

public class GuiIngameHook_CustomSidebarLines {

    public static String getSidebarTextLine(String textIn, int lineNumber) {
        if (MWEConfig.witherHUDinSidebar
                && lineNumber == 13
                && MWEConfig.lastWitherHUDPosition.isEnabled()
                && ScoreboardTracker.isInMwGame()
                && ScoreboardTracker.getParser().isOnlyOneWitherAlive()) {
            return MWERendererManager.lastWitherHPHUD.displayText;
        }
        if (MWEConfig.fkcounterHUDinSidebar
                && lineNumber == 12
                && MWEConfig.fkcounterHUDPosition.isEnabled()
                && ScoreboardTracker.isInMwGame()
                && MWE.INSTANCE().getFinalKillCounter() != null) {
            return MWERendererManager.fkCounterHUD.displayText;
        }
        return textIn;
    }

    public static int getSidebarTextLineWidth(int width, FontRenderer fontRenderer, boolean redNumbers) {
        if (MWEConfig.witherHUDinSidebar
                && MWEConfig.lastWitherHUDPosition.isEnabled()
                && ScoreboardTracker.isInMwGame()
                && ScoreboardTracker.getParser().isOnlyOneWitherAlive()) {
            width = Math.max(width, fontRenderer.getStringWidth(MWERendererManager.lastWitherHPHUD.displayText + (redNumbers ? ": 12" : "")));
        }
        if (MWEConfig.fkcounterHUDinSidebar
                && MWEConfig.fkcounterHUDPosition.isEnabled()
                && ScoreboardTracker.isInMwGame()
                && MWE.INSTANCE().getFinalKillCounter() != null) {
            width = Math.max(width, fontRenderer.getStringWidth(MWERendererManager.fkCounterHUD.displayText + (redNumbers ? ": 11" : "")));
        }
        return width;
    }

}
