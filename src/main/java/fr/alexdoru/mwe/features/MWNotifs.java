package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent.Type;
import fr.alexdoru.mwe.api.events.MegaWallsGameTimeEvent;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.utils.SoundUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

public final class MWNotifs {

    private boolean triggeredWallsFallAlert;
    private boolean triggeredGameEndAlert;

    @SubscribeEvent
    public void onGameStart(MegaWallsGameEvent event) {
        if (event.type == Type.GAME_START) {
            triggeredWallsFallAlert = false;
            triggeredGameEndAlert = false;
        }
    }

    @SubscribeEvent
    public void onMWTime(MegaWallsGameTimeEvent event) {
        if (!triggeredGameEndAlert && event.time == 45 * 60) {
            SoundUtil.playNotePling();
            ChatUtil.addChatMessage(EnumChatFormatting.YELLOW + "Game ends in 5 minutes!");
            triggeredGameEndAlert = true;
        }
        if (!triggeredWallsFallAlert && event.time == 10 + 60 * 6 + 20 && !Display.isActive()) {
            AFKSoundWarning.playWallsFallSound();
            triggeredWallsFallAlert = true;
        }
    }

}
