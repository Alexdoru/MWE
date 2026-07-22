package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.asm.interfaces.EntityPlayerAccessor;
import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.entity.EntityLivingBase;

public class EntityLivingBaseHook_RotationTracker {

    public static void setRotationYawHead(EntityLivingBase entity, float yawHead) {
        if (MWEConfig.hackerDetector && entity instanceof EntityPlayerAccessor) {
            MWE.INSTANCE().getHackerDetector().addScheduledTask(() ->
                    ((EntityPlayerAccessor) entity).getPlayerDataSamples().setRotationYawHead(yawHead)
            );
        }
    }

}
