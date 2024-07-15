package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.EntityPlayerAccessor;
import fr.alexdoru.mwe.asm.interfaces.IWitherColor;
import fr.alexdoru.mwe.config.ConfigHandler;
import net.minecraft.entity.EntityLivingBase;

@SuppressWarnings("unused")
public class RendererLivingEntityHook_HitColor {

    private static int hitColor;

    public static float getRed(float r, EntityLivingBase entity) {
        if (ConfigHandler.teamColoredPlayerHurt && entity instanceof EntityPlayerAccessor) {
            hitColor = ((EntityPlayerAccessor) entity).getPlayerTeamColorInt();
        } else if (ConfigHandler.teamColoredWitherHurt && entity instanceof IWitherColor) {
            final int i = ((IWitherColor) entity).getmwe$Color();
            hitColor = i == 0 ? ConfigHandler.hitColor : i;
        } else {
            hitColor = ConfigHandler.hitColor;
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
        return (float) (ConfigHandler.hitColor >> 24 & 0xFF) / 255.0F;
    }

}
