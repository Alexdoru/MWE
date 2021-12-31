package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LowHPIndicator {

    private static boolean playedSound = false;
    private final Minecraft mc = Minecraft.getMinecraft();
    public static final ResourceLocation lowHPSound = new ResourceLocation("note.pling");

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {

        if (!ConfigHandler.playSoundLowHP) {
            return;
        }

        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (!playedSound && mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth() * ConfigHandler.healthThreshold) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(lowHPSound, 1.0F));
            playedSound = true;
            return;
        }

        if (playedSound && mc.thePlayer.getHealth() >= mc.thePlayer.getMaxHealth() * ConfigHandler.healthThreshold) {
            playedSound = false;
        }

    }

}
