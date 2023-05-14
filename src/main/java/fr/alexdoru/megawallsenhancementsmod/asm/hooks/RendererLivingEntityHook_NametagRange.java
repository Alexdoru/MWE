package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessor.EntityPlayerAccessor;
import net.minecraft.entity.EntityLivingBase;

@SuppressWarnings("unused")
public class RendererLivingEntityHook_NametagRange {

    public static void setRenderNametag(EntityLivingBase entity, boolean b) {
        if (entity instanceof EntityPlayerAccessor) {
            ((EntityPlayerAccessor) entity).setmwe$RenderNametag(b);
        }
    }

}
