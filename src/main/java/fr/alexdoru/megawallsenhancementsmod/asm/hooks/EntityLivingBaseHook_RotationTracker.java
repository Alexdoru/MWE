package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import net.minecraft.entity.EntityLivingBase;

@SuppressWarnings("unused")
public class EntityLivingBaseHook_RotationTracker {
    public static void setRotationYawHead(EntityLivingBase entity, float yawHead) {
        if (ConfigHandler.hackerDetector && entity instanceof EntityPlayerAccessor) {
            HackerDetector.addScheduledTask(() ->
                    ((EntityPlayerAccessor) entity).getPlayerDataSamples().setRotationYawHead(yawHead)
            );
        }
    }
}
