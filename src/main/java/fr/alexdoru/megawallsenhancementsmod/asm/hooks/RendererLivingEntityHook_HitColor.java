package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessor.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;

@SuppressWarnings("unused")
public class RendererLivingEntityHook_HitColor {

    private static int hitColor;

    public static float getRed(float r, EntityLivingBase entity) {
        if (ConfigHandler.useTeamColorWhenHurt && entity instanceof EntityPlayerAccessor && (((EntityPlayerAccessor) entity).getmwe$RenderNametag() || entity instanceof EntityPlayerSP)) {
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
