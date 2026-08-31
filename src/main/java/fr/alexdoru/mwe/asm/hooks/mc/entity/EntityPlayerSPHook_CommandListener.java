package fr.alexdoru.mwe.asm.hooks.mc.entity;

import fr.alexdoru.mwe.api.events.ChatMessageSentEvent;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraftforge.common.MinecraftForge;

public class EntityPlayerSPHook_CommandListener {

    public static void onMessageSent(String message) {
        if (StringUtil.isNullOrEmpty(message)) {
            return;
        }
        MinecraftForge.EVENT_BUS.post(new ChatMessageSentEvent(message));
    }

}
