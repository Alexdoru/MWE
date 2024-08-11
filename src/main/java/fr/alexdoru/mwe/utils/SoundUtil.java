package fr.alexdoru.mwe.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class SoundUtil {

    public static void playStrengthSound() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("item.fireCharge.use"), 0.0F));
    }

    public static void playChatNotifSound() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.orb"), 1.0F));
    }

    public static void playLowHPSound() {
        final Minecraft mc = Minecraft.getMinecraft();
        final ResourceLocation NOTE_PLING = new ResourceLocation("note.pling");
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(NOTE_PLING, 1.0F));
        mc.getSoundHandler().playDelayedSound(PositionedSoundRecord.create(NOTE_PLING, 1.0F), 2);
        mc.getSoundHandler().playDelayedSound(PositionedSoundRecord.create(NOTE_PLING, 1.0F), 4);
        mc.getSoundHandler().playDelayedSound(PositionedSoundRecord.create(NOTE_PLING, 1.0F), 6);
    }

    public static void playNotePling() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("note.pling"), 1.0F));
    }

    public static void playButtonPress() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

}
