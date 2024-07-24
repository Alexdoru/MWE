package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public class GuiPlayerTabOverlayHook_HideHeaderFooter {

    public static boolean shouldRenderHeader() {
        return ConfigHandler.showPlayercountTablist && Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap().size() > 1 || shouldRenderHeaderFooter();
    }

    public static boolean shouldRenderHeaderFooter() {
        if (ConfigHandler.showHeaderFooterOutsideMW && !ScoreboardTracker.isMWEnvironement()) {
            return true;
        }
        return !ConfigHandler.hideTablistHeaderFooter;
    }

}
