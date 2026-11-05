package fr.alexdoru.mwe.asm.hooks.mc.gui;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;

public class GuiPlayerTabOverlayHook_HideHeaderFooter {

    public static boolean shouldRenderHeaderFooter() {
        if (MWEConfig.hideTablistHeaderFooter) {
            return MWEConfig.hideTablistHeaderFooterOnlyInMW && !ScoreboardTracker.isMWEnvironement();
        }
        return true;
    }

}
