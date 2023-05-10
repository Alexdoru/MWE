package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessor.EntityArrowAccessor;
import fr.alexdoru.megawallsenhancementsmod.asm.accessor.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;

@SuppressWarnings("unused")
public class RenderManagerHook {

    private static boolean renderNametag;

    // this is actually called from RendererLivingEntityTransformer_NametagRange
    public static void setRenderNametag(boolean b) {
        renderNametag = b;
    }

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

    private static int greenHitboxColor = 255;
    private static int blueHitboxColor = 255;

    public static int getRedHitboxColor(int originalColor, Entity entity) {
        if (ConfigHandler.teamColoredHitbox && renderNametag && entity instanceof EntityPlayerAccessor) {
            final int colorCode = ((EntityPlayerAccessor) entity).getPlayerTeamColorInt();
            greenHitboxColor = colorCode >> 8 & 255;
            blueHitboxColor = colorCode & 255;
            return colorCode >> 16 & 255;
        } else if (ConfigHandler.hitboxColor != 0xFFFFFF) {
            greenHitboxColor = ConfigHandler.hitboxColor >> 8 & 255;
            blueHitboxColor = ConfigHandler.hitboxColor & 255;
            return ConfigHandler.hitboxColor >> 16 & 255;
        }
        greenHitboxColor = 255;
        blueHitboxColor = 255;
        return originalColor;
    }

    public static int getGreenHitboxColor(int originalColor) {
        return greenHitboxColor == 255 ? originalColor : greenHitboxColor;
    }

    public static int getBlueHitboxColor(int originalColor) {
        return blueHitboxColor == 255 ? originalColor : blueHitboxColor;
    }

    public static double getBlueVectLength(Entity entityIn) {
        if (!ConfigHandler.drawBlueVect) {
            return 0;
        }
        if (ConfigHandler.drawBlueVectForPlayersOnly) {
            if (entityIn instanceof AbstractClientPlayer) {
                return ConfigHandler.makeBlueVect3Meters ? 3.0d : 2.0d;
            } else {
                return 0;
            }
        }
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
