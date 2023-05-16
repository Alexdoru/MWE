package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessor.EntityArrowAccessor;
import fr.alexdoru.megawallsenhancementsmod.asm.accessor.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;

@SuppressWarnings("unused")
public class RenderManagerHook {

    public static boolean cancelHitboxRender(Entity entityIn, Entity viewingEntity) {
        if (ConfigHandler.hideCloseHitbox) {
            if (entityIn.getDistanceSqToEntity(viewingEntity) <= ConfigHandler.hitboxDrawRange * ConfigHandler.hitboxDrawRange) {
                return true;
            }
        }
        if (entityIn instanceof AbstractClientPlayer) {
            return !ConfigHandler.drawHitboxForPlayers;
        }
        if (entityIn instanceof EntityArrow) {
            if (((EntityArrowAccessor) entityIn).isInGround()) {
                return !ConfigHandler.drawHitboxForGroundedArrows;
            }
            if (((EntityArrowAccessor) entityIn).isPinnedToPlayer()) {
                return !ConfigHandler.drawHitboxForPinnedArrows;
            }
            return !ConfigHandler.drawHitboxForFlyingArrows;
        }
        if (entityIn instanceof EntityItem) {
            return !ConfigHandler.drawHitboxForDroppedItems;
        }
        if (entityIn instanceof EntityAnimal) {
            return !ConfigHandler.drawHitboxForPassiveMobs;
        }
        if (entityIn instanceof EntityMob) {
            return !ConfigHandler.drawHitboxForAggressiveMobs;
        }
        if (entityIn instanceof EntityItemFrame) {
            return !ConfigHandler.drawHitboxItemFrame;
        }
        return !ConfigHandler.drawHitboxForOtherEntity;
    }

    private static int hitboxColor = 0xFFFFFF;

    public static int getRedHitboxColor(int r, Entity entity) {
        if (ConfigHandler.teamColoredHitbox && entity instanceof EntityPlayerAccessor && (((EntityPlayerAccessor) entity).getmwe$RenderNametag() || entity instanceof EntityPlayerSP)) {
            hitboxColor = ((EntityPlayerAccessor) entity).getPlayerTeamColorInt();
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

    public static double getBlueVectLength() {
        return ConfigHandler.makeBlueVect3Meters ? 3.0d : 2.0d;
    }

    public static AxisAlignedBB getAxisAlignedBB(AxisAlignedBB axisAlignedBBIn, Entity entityIn) {
        if (ConfigHandler.realSizeHitbox) {
            final float f = entityIn.getCollisionBorderSize();
            return axisAlignedBBIn.expand(f, f, f);
        }
        return axisAlignedBBIn;
    }

}
