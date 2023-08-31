package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector3D;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class KillAuraCheck extends AbstractCheck {

    @Override
    public String getCheatName() {
        return "KillAura";
    }

    @Override
    public String getCheatDescription() {
        return "The player can attack through solid blocks";
    }

    @Override
    public boolean canSendReport() {
        return true;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.killAuraVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {

        if (data.hasAttacked) {

            // check if eyes of the attacker are inside the target's hitbox
            final float f1 = data.targetedPlayer.getCollisionBorderSize();
            final AxisAlignedBB targetAABB = data.targetedPlayer.getEntityBoundingBox().expand(f1, f1, f1);
            final Vector3D playersEyePos = Vector3D.getPlayersEyePos(player);
            if (playersEyePos.isVectInside(targetAABB)) {
                return false;
            }

            final Vector3D playersLookVec = Vector3D.getPlayersLookVec(player);
            final Vector3D vectToTarget = Vector3D.getVectToEntity(player, data.targetedPlayer);
            // return if target is behind the look of the attacker
            if (vectToTarget.dotProduct(playersLookVec) < 0D) {
                return false;
            }

            final float distanceToRayTrace = player.getDistanceToEntity(data.targetedPlayer) + 1F;
            final double STEP_SIZE = 0.1D;
            final int iterMax = (int) (distanceToRayTrace / STEP_SIZE);

            int blockXpos = -1;
            int blockYpos = -1;
            int blockZpos = -1;
            boolean canHitThroughBlock = false;
            int timesInsideBlock = 0;

            for (int i = 1; i < iterMax + 1; i++) {

                final double dx = playersEyePos.x + i * STEP_SIZE * playersLookVec.x;
                final double dy = playersEyePos.y + i * STEP_SIZE * playersLookVec.y;
                final double dz = playersEyePos.z + i * STEP_SIZE * playersLookVec.z;

                // point is inside the target's hitbox
                if ((dx > targetAABB.minX && dx < targetAABB.maxX) && (dy > targetAABB.minY && dy < targetAABB.maxY) && (dz > targetAABB.minZ && dz < targetAABB.maxZ)) {
                    if (timesInsideBlock > 0) {
                        data.killAuraVL.add(Math.min(timesInsideBlock, 10));
                        if (ConfigHandler.debugLogging) {
                            final String msg = "target : " + data.targetedPlayer.getName() + " timesInsideBlock " + timesInsideBlock;
                            this.log(player, data, data.killAuraVL, msg);
                        }
                        return true;
                    } else {
                        data.killAuraVL.substract(10);
                        return false;
                    }
                }

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

                if (!canHitThroughBlock) {
                    timesInsideBlock++;
                }

            }

        }

        return false;

    }

    public static ViolationLevelTracker newViolationTracker() {
        return new ViolationLevelTracker(25);
    }

}
