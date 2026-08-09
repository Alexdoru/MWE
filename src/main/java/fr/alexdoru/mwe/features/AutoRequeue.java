package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.DelayedTask;
import fr.alexdoru.mwe.utils.SoundUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

public final class AutoRequeue {

    @SubscribeEvent
    public void onGameEnd(MegaWallsGameEvent event) {
        if (MWEConfig.autoRequeue && event.type == MegaWallsGameEvent.Type.GAME_END) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.YELLOW + "Queing next game in 8s");
            new DelayedTask(() -> {
                final Minecraft mc = Minecraft.getMinecraft();
                if (mc.thePlayer != null && mc.theWorld != null && !ScoreboardTracker.isPreGameLobby()) {
                    if (!Display.isActive()) {
                        SoundUtil.playChatNotifSound();
                    }
                    mc.thePlayer.sendChatMessage("/play mw_standard");
                }
            }, 8 * 20);
        }
    }

}
