package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.IWitherColor;
import net.minecraft.entity.EntityLivingBase;

@SuppressWarnings("unused")
public class RendererLivingEntityHook_ColorOutlines {

    public static int getEntityOutlineColor(int color, EntityLivingBase entity) {
        if (entity instanceof IWitherColor) {
            return ((IWitherColor) entity).getmwe$Color();
        }
        return color;
    }

}
