package fr.alexdoru.mwe.features;

import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AFKSoundWarning {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ResourceLocation soundResource;
    private final int maxTicks;
    private int ticks;

    private AFKSoundWarning(ResourceLocation soundResource, int maxTicks) {
        this.soundResource = soundResource;
        this.maxTicks = maxTicks;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && mc.thePlayer != null && mc.theWorld != null) {
            ticks++;
            if (ticks % 2 == 0) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(soundResource, 1F));
            }
            if (ticks > maxTicks || !isPlayerStandingStill()) {
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }

    private boolean isPlayerStandingStill() {
        return mc.thePlayer.movementInput.moveForward == 0.0F
                && mc.thePlayer.movementInput.moveStrafe == 0.0F
                && !mc.thePlayer.movementInput.jump
                && !mc.thePlayer.movementInput.sneak
                && !mc.gameSettings.keyBindAttack.isKeyDown()
                && !mc.gameSettings.keyBindUseItem.isKeyDown()
                && mc.thePlayer.prevRotationYawHead == mc.thePlayer.rotationYawHead;
    }

    public static void playAFKKickSound() {
        if (ConfigHandler.afkSoundWarning && ScoreboardTracker.isInMwGame()) {
            MinecraftForge.EVENT_BUS.register(new AFKSoundWarning(new ResourceLocation("note.pling"), 20 * 15));
        }
    }

    public static void playWallsFallSound() {
        if (ConfigHandler.afkSoundWarning) {
            MinecraftForge.EVENT_BUS.register(new AFKSoundWarning(new ResourceLocation("random.orb"), 10));
        }
    }

}
