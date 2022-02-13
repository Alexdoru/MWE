package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessor.EntityArrowAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.projectile.EntityArrow;

public class RenderManagerHook {

    public static boolean cancelHitboxRender(Entity entityIn) {
        if (entityIn instanceof AbstractClientPlayer) {
            return !ConfigHandler.drawHitboxForPlayers;
        }
        if (entityIn instanceof EntityArrow) {
            if (!(entityIn instanceof EntityArrowAccessor)) {
                return false;
            }
            if (((EntityArrowAccessor) entityIn).getInGround()) {
                return !ConfigHandler.drawHitboxForGroundedArrows;
            }
            if (((EntityArrowAccessor) entityIn).getIsPinnedToPlayer()) {
                return !ConfigHandler.drawHitboxForPinnedArrows;
            }
            return false;
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

    public static double getBlueVectLength(Entity entityIn) {
        if (ConfigHandler.HideBlueVect) {
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

}
