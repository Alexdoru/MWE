package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.features.PartyDetection;
import fr.alexdoru.mwe.gui.MWERenderers;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.StringUtil;

public class EntityPlayerSPHook_CommandListener {

    public static void onMessageSent(String message) {
        if (StringUtil.isNullOrEmpty(message)) {
            return;
        }
        if (MWEConfig.killCooldownHUDPosition.isEnabled() && ScoreboardTracker.isInMwGame()) {
            message = message.toLowerCase();
            if (message.equals("/kill") || message.startsWith("/kill ")) {
                MWERenderers.killCooldownHUD.drawCooldownHUD();
            }
            return;
        }
        if (message.toLowerCase().startsWith("/report")) {
            final String[] args = message.split(" ");
            if (args.length > 1) {
                final String playername = args[1];
                PartyDetection.printBoostingReportAdvice(playername);
            }
        }
    }

}
