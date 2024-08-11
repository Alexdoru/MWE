package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;

@SuppressWarnings("unused")
public class RendererLivingEntityHook_AprilFun {

    public static void doFunny(EntityLivingBase entity) {
        if (!MWEConfig.aprilFools) return;
        GlStateManager.translate(0.0F, getVisibleHeight(entity) + 0.1F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
    }

    /**
     * Patcher hook
     */
    private static float getVisibleHeight(Entity entity) {
        return entity instanceof EntityZombie && ((EntityZombie) entity).isChild() ? entity.height / 2 : entity.height;
    }

}
