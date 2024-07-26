package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.EntityPlayerAccessor;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.hackerdetector.HackerDetector;
import net.minecraft.entity.EntityLivingBase;

@SuppressWarnings("unused")
public class EntityLivingBaseHook_RotationTracker {
    public static void setRotationYawHead(EntityLivingBase entity, float yawHead) {
        if (MWEConfig.hackerDetector && entity instanceof EntityPlayerAccessor) {
            HackerDetector.addScheduledTask(() ->
                    ((EntityPlayerAccessor) entity).getPlayerDataSamples().setRotationYawHead(yawHead)
            );
        }
    }
}
