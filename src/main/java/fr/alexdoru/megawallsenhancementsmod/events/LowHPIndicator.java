package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LowHPIndicator {

    public static final ResourceLocation lowHPSound = new ResourceLocation("note.pling");
    private static boolean playedSound = false;
    private static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {

        if (!ConfigHandler.playSoundLowHP) {
            return;
        }

        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (!playedSound && mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth() * ConfigHandler.healthThreshold) {
            playSound();
            playedSound = true;
            return;
        }

        if (playedSound && mc.thePlayer.getHealth() >= mc.thePlayer.getMaxHealth() * ConfigHandler.healthThreshold) {
            playedSound = false;
        }

    }

    public static void playSound() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(lowHPSound, 1.0F));
        mc.getSoundHandler().playDelayedSound(PositionedSoundRecord.create(lowHPSound, 1.0F), 2);
        mc.getSoundHandler().playDelayedSound(PositionedSoundRecord.create(lowHPSound, 1.0F), 4);
        mc.getSoundHandler().playDelayedSound(PositionedSoundRecord.create(lowHPSound, 1.0F), 6);
    }

}
