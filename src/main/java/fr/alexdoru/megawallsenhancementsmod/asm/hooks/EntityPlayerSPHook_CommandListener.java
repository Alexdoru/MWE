package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiManager;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.StringUtil;

@SuppressWarnings("unused")
public class EntityPlayerSPHook_CommandListener {

    public static void onMessageSent(String message) {
        if (StringUtil.isNullOrEmpty(message)) {
            return;
        }
        if (ConfigHandler.showKillCooldownHUD && ScoreboardTracker.isInMwGame()) {
            message = message.toLowerCase();
            if (message.equals("/kill") || message.startsWith("/kill ")) {
                GuiManager.killCooldownHUD.drawCooldownHUD();
            }
        }
    }

}
