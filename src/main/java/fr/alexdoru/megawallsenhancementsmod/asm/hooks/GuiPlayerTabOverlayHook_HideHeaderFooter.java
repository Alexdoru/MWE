package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;

@SuppressWarnings("unused")
public class GuiPlayerTabOverlayHook_HideHeaderFooter {

    public static boolean shouldRenderHeader() {
        return ConfigHandler.showPlayercountTablist || !shouldHideFooter();
    }

    public static boolean shouldHideFooter() {
        if (ConfigHandler.showHeaderFooterOutsideMW && !ScoreboardTracker.isMWEnvironement()) {
            return false;
        }
        return ConfigHandler.hideTablistHeaderFooter;
    }

}
