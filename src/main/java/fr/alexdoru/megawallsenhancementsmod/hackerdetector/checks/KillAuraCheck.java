package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;

import java.util.List;

public class KillAuraCheck extends AbstractCheck {

    @Override
    public String getCheatName() {
        return "KillAura";
    }

    @Override
    public String getCheatDescription() {
        return "The player can attack through blocks/entities";
    }

    @Override
    public boolean canSendReport() {
        return false;// TODO change once it's done
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.killAuraVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {

        if (data.hasAttacked) {

            // check if eyes of the attacker are inside the target's hitbox
            final Vec3 attackerEyePos = data.getPositionEyesServer(player);
            if (isInsideHitbox(data.targetedPlayer, attackerEyePos)) {
                return false;
            }
            final Vec3 lookVect = data.getLookServer();
            final Vec3 lookEndPos = attackerEyePos.addVector(lookVect.xCoord * 8D, lookVect.yCoord * 8D, lookVect.zCoord * 8D);
            final Vec3 hitOnPlayerPos = getHitVectOnPlayer(data.targetedPlayer, attackerEyePos, lookEndPos);
            // doesn't look at the target's hitbox
            if (hitOnPlayerPos == null) {
                return false;
            }

            final double distanceToRayTrace = attackerEyePos.distanceTo(hitOnPlayerPos);
            final double STEP_SIZE = 0.1D;
            final int iterMax = (int) (distanceToRayTrace / STEP_SIZE);

            int blockXpos = -1;
            int blockYpos = -1;
            int blockZpos = -1;
            boolean canHitThroughBlock = false;
            int timesInsideBlock = 0;
            int timesInsidePlayer = 0;

            // TODO compléxité d'un tel algo ?
            // TODO est ce que l'autre algo serait moins complexe
            // TODO arreter de regarder à travers les blocs
            // TODO faire avec les pos des enemis sur 4-5 ticks

            final float f = 1.0F;
            final List<EntityPlayer> list = getPlayersInAABBexcluding(player,
                    player.getEntityBoundingBox().addCoord(lookVect.xCoord * distanceToRayTrace, lookVect.yCoord * distanceToRayTrace, lookVect.zCoord * distanceToRayTrace).expand(f, f, f),
                    p -> p != data.targetedPlayer && p != mc.thePlayer && !p.isSpectator() && p.canBeCollidedWith());

            for (int i = 0; i < iterMax + 1; i++) {

                final double dx = attackerEyePos.xCoord + i * STEP_SIZE * lookVect.xCoord;
                final double dy = attackerEyePos.yCoord + i * STEP_SIZE * lookVect.yCoord;
                final double dz = attackerEyePos.zCoord + i * STEP_SIZE * lookVect.zCoord;
                final int xpos = MathHelper.floor_double(dx);
                final int ypos = MathHelper.floor_double(dy);
                final int zpos = MathHelper.floor_double(dz);

                if (xpos != blockXpos || ypos != blockYpos || zpos != blockZpos) {
                    final IBlockState iblockstate = mc.theWorld.getBlockState(new BlockPos(xpos, ypos, zpos));
                    final Block block = iblockstate.getBlock();
                    canHitThroughBlock = !(block.canCollideCheck(iblockstate, false) && block.isFullBlock());
                    blockXpos = xpos;
                    blockYpos = ypos;
                    blockZpos = zpos;
                }

                for (final EntityPlayer entity : list) {
                    if (isInsideHitbox(entity, dx, dy, dz)) {
                        timesInsidePlayer++;
                        break;
                    }
                }

                if (!canHitThroughBlock) {
                    timesInsideBlock++;
                }

            }

            if (timesInsideBlock + timesInsidePlayer > 0) {
                data.killAuraVL.add(Math.min(timesInsideBlock, 10) * 2 + timesInsidePlayer);
                if (ConfigHandler.debugLogging) {
                    final String msg = "target : " + data.targetedPlayer.getName() + " timesInsideBlock " + timesInsideBlock + " timesInsidePlayer " + timesInsidePlayer;
                    this.log(player, data, data.killAuraVL, msg);
                }
                return true;
            } else {
                data.killAuraVL.substract(15);
                return false;
            }
        }

        return false;

    }

    private void checkThroughEntity(EntityPlayer player, EntityPlayer target, Vec3 positionEyes, Vec3 lookVect, double distance) {
        final Vec3 lookEndVect = positionEyes.addVector(lookVect.xCoord * distance, lookVect.yCoord * distance, lookVect.zCoord * distance);
        final float f = 1.0F;
        final List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(
                player,
                player.getEntityBoundingBox().addCoord(lookVect.xCoord * distance, lookVect.yCoord * distance, lookVect.zCoord * distance).expand(f, f, f),
                e -> e != target && e != mc.thePlayer && (e instanceof EntityPlayer) && !((EntityPlayer) e).isSpectator() && e.canBeCollidedWith());
        final StringBuilder str = new StringBuilder();
        int timesInside = 0;
        for (final Entity otherPlayer : list) {
            final float f1 = otherPlayer.getCollisionBorderSize();
            final AxisAlignedBB entityAABB = otherPlayer.getEntityBoundingBox().expand(f1, f1, f1);
            if (entityAABB.isVecInside(positionEyes)) {
                timesInside++;
                str.append(NameUtil.getFormattedNameWithoutIcons(otherPlayer.getName())).append(EnumChatFormatting.RESET).append(" (inside) ");
            } else {
                final MovingObjectPosition movingobjectposition = entityAABB.calculateIntercept(positionEyes, lookEndVect);
                if (movingobjectposition != null) {
                    final double d3 = positionEyes.distanceTo(movingobjectposition.hitVec);
                    if (d3 < distance) {
                        timesInside++;
                        str.append(NameUtil.getFormattedNameWithoutIcons(otherPlayer.getName())).append(EnumChatFormatting.RESET).append(" dist ").append(String.format("%.4f", d3)).append(" ");
                    }
                }
            }
        }
        final String s = str.toString();
        if (!s.isEmpty()) {
            ChatUtil.debug(
                    NameUtil.getFormattedNameWithoutIcons(player.getName()) + EnumChatFormatting.RESET
                            + " attacked " + NameUtil.getFormattedNameWithoutIcons(target.getName()) + EnumChatFormatting.RESET
                            + " dist " + String.format("%.4f", distance) + " through " + s
            );
        }
    }

    public static ViolationLevelTracker newViolationTracker() {
        return new ViolationLevelTracker(25 * 2);
    }

}
