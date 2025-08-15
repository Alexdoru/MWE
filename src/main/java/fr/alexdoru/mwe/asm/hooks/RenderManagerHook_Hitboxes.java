package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.EntityPlayerAccessor;
import fr.alexdoru.mwe.asm.interfaces.IHitboxRender;
import fr.alexdoru.mwe.asm.interfaces.IWitherColor;
import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;

public class RenderManagerHook_Hitboxes {

    public static boolean shouldToggleOnStart(boolean original) {
        return MWEConfig.isDebugHitboxOn;
    }

    public static boolean shouldRenderHitbox(Entity entityIn, Entity viewingEntity, boolean renderOutlines) {
        if (renderOutlines) {
            return false;
        }
        if (MWEConfig.hideCloseHitbox) {
            if (entityIn.getDistanceSqToEntity(viewingEntity) <= MWEConfig.hitboxDrawRange * MWEConfig.hitboxDrawRange) {
                return false;
            }
        }
        if (entityIn instanceof IHitboxRender) {
            return ((IHitboxRender) entityIn).mwe$shouldRenderHitbox();
        }
        return MWEConfig.drawHitboxForOtherEntity;
    }

    private static int hitboxColor = 0xFFFFFF;

    public static int getRedHitboxColor(int r, Entity entity) {
        if (MWEConfig.teamColoredPlayerHitbox && entity instanceof EntityPlayerAccessor) {
            hitboxColor = ((EntityPlayerAccessor) entity).getPlayerTeamColorInt();
        } else if (MWEConfig.teamColoredWitherHitbox && entity instanceof IWitherColor) {
            final int i = ((IWitherColor) entity).getmwe$Color();
            hitboxColor = i == 0 ? MWEConfig.hitboxColor : i;
        } else if (MWEConfig.teamColoredArrowHitbox && entity instanceof EntityArrow && ((EntityArrow) entity).shootingEntity instanceof EntityPlayerAccessor) {
            hitboxColor = ((EntityPlayerAccessor) ((EntityArrow) entity).shootingEntity).getPlayerTeamColorInt();
        } else {
            hitboxColor = MWEConfig.hitboxColor;
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
        if (MWEConfig.drawBlueVect) {
            if (MWEConfig.drawBlueVectForPlayersOnly) {
                return entityIn instanceof AbstractClientPlayer;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static double getBlueVectLength(double original) {
        return MWEConfig.makeBlueVect3Meters ? 3.0d : original;
    }

    public static AxisAlignedBB getAxisAlignedBB(AxisAlignedBB axisAlignedBBIn, Entity entityIn) {
        if (MWEConfig.realSizeHitbox) {
            final float f = entityIn.getCollisionBorderSize();
            return axisAlignedBBIn.expand(f, f, f);
        }
        return axisAlignedBBIn;
    }

}
