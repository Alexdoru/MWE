package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.entity.EntityLivingBase;

@SuppressWarnings("unused")
public class RendererLivingEntityHook_HitColor {

    private static int hitColor;

    public static float getRed(float r, EntityLivingBase entity) {
        if (ConfigHandler.useTeamColorWhenHurt && entity instanceof EntityPlayerAccessor) {
            hitColor = ((EntityPlayerAccessor) entity).getPlayerTeamColorInt();
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
