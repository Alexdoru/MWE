package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.ConfigHandler;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;

@SuppressWarnings("unused")
public class EntityFXHook_CleanView {

    public static boolean shouldHideParticle(EntityFX entityFXIn, Entity entityIn) {
        return ConfigHandler.clearVision && entityFXIn.getDistanceSq(entityIn.posX, entityIn.posY + entityIn.getEyeHeight(), entityIn.posZ) < 0.5625d;
    }

}