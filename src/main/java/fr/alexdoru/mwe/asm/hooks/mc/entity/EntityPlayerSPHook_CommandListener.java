package fr.alexdoru.mwe.asm.hooks.mc.entity;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.api.events.ChatMessageSentEvent;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraftforge.common.MinecraftForge;

public class EntityPlayerSPHook_CommandListener {

    public static void onMessageSent(String message) {
        if (StringUtil.isNullOrEmpty(message)) {
            return;
        }
        MinecraftForge.EVENT_BUS.post(new ChatMessageSentEvent(message));
        if (MWEConfig.killCooldownHUDPosition.isEnabled() && ScoreboardTracker.isInMwGame()) {
            message = message.toLowerCase();
            if (message.equals("/kill") || message.startsWith("/kill ")) {
                MWE.INSTANCE().getMweRenderers().killCooldownHUD.drawCooldownHUD();
            }
        }
    }

}
