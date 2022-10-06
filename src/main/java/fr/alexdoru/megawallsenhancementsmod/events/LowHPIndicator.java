package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.SoundUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LowHPIndicator {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private boolean playedSound = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!ConfigHandler.playSoundLowHP || event.phase == TickEvent.Phase.START || mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        if (!playedSound && mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth() * ConfigHandler.healthThreshold) {
            SoundUtil.playLowHPSound();
            playedSound = true;
            return;
        }
        if (playedSound && mc.thePlayer.getHealth() >= mc.thePlayer.getMaxHealth() * ConfigHandler.healthThreshold) {
            playedSound = false;
        }
    }

}
