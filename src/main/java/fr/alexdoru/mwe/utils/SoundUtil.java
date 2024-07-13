package fr.alexdoru.mwe.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class SoundUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ResourceLocation FIRECHARGE_USE = new ResourceLocation("item.fireCharge.use");
    private static final ResourceLocation NOTE_PLING = new ResourceLocation("note.pling");
    private static final ResourceLocation RANDOM_ORB = new ResourceLocation("random.orb");

    public static void playStrengthSound() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(FIRECHARGE_USE, 0.0F));
    }

    public static void playReportSuggestionSound() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(RANDOM_ORB, 1.0F));
    }

    public static void playLowHPSound() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(NOTE_PLING, 1.0F));
        mc.getSoundHandler().playDelayedSound(PositionedSoundRecord.create(NOTE_PLING, 1.0F), 2);
        mc.getSoundHandler().playDelayedSound(PositionedSoundRecord.create(NOTE_PLING, 1.0F), 4);
        mc.getSoundHandler().playDelayedSound(PositionedSoundRecord.create(NOTE_PLING, 1.0F), 6);
    }

    public static void playNotePling() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(NOTE_PLING, 1.0F));
    }

}
