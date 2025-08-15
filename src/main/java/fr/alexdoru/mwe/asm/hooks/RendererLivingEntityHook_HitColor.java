package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.EntityPlayerAccessor;
import fr.alexdoru.mwe.asm.interfaces.IWitherColor;
import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.entity.EntityLivingBase;

public class RendererLivingEntityHook_HitColor {

    private static int hitColor;

    public static float getRed(float r, EntityLivingBase entity) {
        if (MWEConfig.teamColoredPlayerHurt && entity instanceof EntityPlayerAccessor) {
            hitColor = ((EntityPlayerAccessor) entity).getPlayerTeamColorInt();
        } else if (MWEConfig.teamColoredWitherHurt && entity instanceof IWitherColor) {
            final int i = ((IWitherColor) entity).getmwe$Color();
            hitColor = i == 0 ? MWEConfig.hitColor : i;
        } else {
            hitColor = MWEConfig.hitColor;
        }
        return (float) (hitColor >> 16 & 0xFF) / 255.0F;
    }

    public static float getGreen(float g) {
        return (float) (hitColor >> 8 & 0xFF) / 255.0F;
    }

    public static float getBlue(float b) {
        return (float) (hitColor & 0xFF) / 255.0F;
    }

    public static float getAlpha(float a) {
        return (float) (MWEConfig.hitColor >> 24 & 0xFF) / 255.0F;
    }

}
