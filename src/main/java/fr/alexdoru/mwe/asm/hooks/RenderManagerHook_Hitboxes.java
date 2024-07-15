package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.EntityPlayerAccessor;
import fr.alexdoru.mwe.asm.interfaces.IHitboxRender;
import fr.alexdoru.mwe.asm.interfaces.IWitherColor;
import fr.alexdoru.mwe.config.ConfigHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;

@SuppressWarnings("unused")
public class RenderManagerHook_Hitboxes {

    public static boolean shouldToggleOnStart(boolean original) {
        return ConfigHandler.isDebugHitboxOn;
    }

    public static boolean shouldRenderHitbox(Entity entityIn, Entity viewingEntity) {
        if (ConfigHandler.hideCloseHitbox) {
            if (entityIn.getDistanceSqToEntity(viewingEntity) <= ConfigHandler.hitboxDrawRange * ConfigHandler.hitboxDrawRange) {
                return false;
            }
        }
        if (entityIn instanceof IHitboxRender) {
            return ((IHitboxRender) entityIn).mwe$shouldRenderHitbox();
        }
        return ConfigHandler.drawHitboxForOtherEntity;
    }

    private static int hitboxColor = 0xFFFFFF;

    public static int getRedHitboxColor(int r, Entity entity) {
        if (ConfigHandler.teamColoredPlayerHitbox && entity instanceof EntityPlayerAccessor) {
            hitboxColor = ((EntityPlayerAccessor) entity).getPlayerTeamColorInt();
        } else if (ConfigHandler.teamColoredWitherHitbox && entity instanceof IWitherColor) {
            final int i = ((IWitherColor) entity).getmwe$Color();
            hitboxColor = i == 0 ? ConfigHandler.hitboxColor : i;
        } else if (ConfigHandler.teamColoredArrowHitbox && entity instanceof EntityArrow && ((EntityArrow) entity).shootingEntity instanceof EntityPlayerAccessor) {
            hitboxColor = ((EntityPlayerAccessor) ((EntityArrow) entity).shootingEntity).getPlayerTeamColorInt();
        } else {
            hitboxColor = ConfigHandler.hitboxColor;
        }
        return hitboxColor >> 16 & 255;
    }

    public static int getGreenHitboxColor(int g) {
        return hitboxColor >> 8 & 255;
    }

    public static int getBlueHitboxColor(int b) {
        return hitboxColor & 255;
    }

    public static boolean shouldRenderBlueVect(Entity entityIn) {
        if (ConfigHandler.drawBlueVect) {
            if (ConfigHandler.drawBlueVectForPlayersOnly) {
                return entityIn instanceof AbstractClientPlayer;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static double getBlueVectLength(double original) {
        return ConfigHandler.makeBlueVect3Meters ? 3.0d : original;
    }

    public static AxisAlignedBB getAxisAlignedBB(AxisAlignedBB axisAlignedBBIn, Entity entityIn) {
        if (ConfigHandler.realSizeHitbox) {
            final float f = entityIn.getCollisionBorderSize();
            return axisAlignedBBIn.expand(f, f, f);
        }
        return axisAlignedBBIn;
    }

}
