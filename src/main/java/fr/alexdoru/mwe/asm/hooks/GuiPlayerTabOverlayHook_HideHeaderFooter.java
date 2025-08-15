package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;

public class GuiPlayerTabOverlayHook_HideHeaderFooter {

    public static boolean shouldRenderHeader() {
        return MWEConfig.showPlayercountTablist && Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap().size() > 1 || shouldRenderHeaderFooter();
    }

    public static boolean shouldRenderHeaderFooter() {
        if (MWEConfig.hideTablistHeaderFooter) {
            if (MWEConfig.hideTablistHeaderFooterOnlyInMW) {
                return !ScoreboardTracker.isMWEnvironement();
            }
            return false;
        }
        return true;
    }

}
