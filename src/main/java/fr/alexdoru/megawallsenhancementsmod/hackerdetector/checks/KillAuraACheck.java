package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.List;

public class KillAuraACheck extends Check {

    @Override
    public String getCheatName() {
        return "KillAura";
    }

    @Override
    public String getCheatDescription() {
        return "The player can attack through blocks/entities";
    }

    @Override
    public String getFlagType() {
        return "A";
    }

    @Override
    public boolean canSendReport() {
        return true;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.killAuraAVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {

        if (!data.hasAttackedTarget()) return false;
        if (player.isRiding()) return false;
        if (data.attackInfo.target == mc.thePlayer) return false;

        final double maxReach = 3.15D;
        final Vec3 attackerEyePos = data.getPositionEyesServer(player);
        final Vec3 lookVect = data.getLookServer();
        final Vec3 lookEndPos = attackerEyePos.addVector(lookVect.xCoord * maxReach, lookVect.yCoord * maxReach, lookVect.zCoord * maxReach);
        final PlayerDataSamples targetData = ((EntityPlayerAccessor) data.attackInfo.target).getPlayerDataSamples();

        final int MAX_TICK_DELAY = 10;
        if (targetData.posXList.size() < MAX_TICK_DELAY) {
            return false;
        }
        final boolean[] hits = new boolean[MAX_TICK_DELAY - 1];
        final double[] hitDistances = new double[MAX_TICK_DELAY - 1];
        double maxDistance = -1D;

        for (int i = 1; i < MAX_TICK_DELAY; i++) {
            if (isInsideHitbox(targetData.posXList.get(i), targetData.posYList.get(i), targetData.posZList.get(i), attackerEyePos)) {
                return false;
            }
            final Vec3 hitOnPlayerPos = getHitVectOnPlayer(targetData.posXList.get(i), targetData.posYList.get(i), targetData.posZList.get(i), attackerEyePos, lookEndPos);
            if (hitOnPlayerPos == null) {
                hits[i - 1] = false;
                hitDistances[i - 1] = -1D;
            } else {
                hits[i - 1] = true;
                hitDistances[i - 1] = attackerEyePos.distanceTo(hitOnPlayerPos);
                maxDistance = Math.max(maxDistance, hitDistances[i - 1]);
            }
        }

        if (maxDistance == -1D) {
            return false;
        }

        final double STEP_SIZE = 0.1D;
        final int maxSteps = (int) (maxDistance / STEP_SIZE);
        final int[] insideBlockArray = new int[maxSteps + 1];
        int blockXpos = -1;
        int blockYpos = -1;
        int blockZpos = -1;
        boolean canHitThroughBlock = false;
        int timesInsideBlock = 0;
        for (int i = 0; i < maxSteps + 1; i++) {
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
            if (!canHitThroughBlock) {
                timesInsideBlock++;
            }
            insideBlockArray[i] = timesInsideBlock;
        }

        final float f = 1.0F;
        maxDistance = maxDistance + 2D;
        final List<EntityPlayer> nearbyPlayers = getPlayersInAABBexcluding(player,
                player.getEntityBoundingBox().addCoord(lookVect.xCoord * maxDistance, lookVect.yCoord * maxDistance, lookVect.zCoord * maxDistance).expand(f, f, f),
                p -> p != data.attackInfo.target && p != mc.thePlayer && p.canBeCollidedWith() && HackerDetector.isValidPlayer(p.getUniqueID()) && !p.isInvisible());

        int b = 1000;
        int p = 1000;
        double reach = 0D;

        for (int i = 1; i < MAX_TICK_DELAY; i++) {
            if (!hits[i - 1]) continue;
            final int iterMax = (int) (hitDistances[i - 1] / STEP_SIZE);
            int timesInsidePlayer = 0;
            if (!nearbyPlayers.isEmpty()) {
                for (int j = 0; j < iterMax + 1; j++) {
                    final double dx = attackerEyePos.xCoord + j * STEP_SIZE * lookVect.xCoord;
                    final double dy = attackerEyePos.yCoord + j * STEP_SIZE * lookVect.yCoord;
                    final double dz = attackerEyePos.zCoord + j * STEP_SIZE * lookVect.zCoord;
                    for (final EntityPlayer entity : nearbyPlayers) {
                        final PlayerDataSamples eData = ((EntityPlayerAccessor) entity).getPlayerDataSamples();
                        if (eData.posXList.size() < MAX_TICK_DELAY) continue;
                        if (isInsideHitbox(eData.posXList.get(i), eData.posYList.get(i), eData.posZList.get(i), dx, dy, dz)) {
                            timesInsidePlayer++;
                            break;
                        }
                    }
                }
            }
            if (b + p > timesInsidePlayer + insideBlockArray[iterMax]) {
                b = insideBlockArray[iterMax];
                p = timesInsidePlayer;
                reach = hitDistances[i - 1];
            }
            if (b + p == 0) {
                return false;
            }
        }

        if (b + p > 0) {
            data.killAuraAVL.add(Math.min(10, Math.min(10, b) + Math.min(8, p)) * 25);
            if (ConfigHandler.debugLogging) {
                final String msg = " | " + data.attackInfo.attackType.name() + " | target : " + data.attackInfo.targetName + " | b " + b + " | p " + p + " | reach " + String.format("%.2f", reach);
                this.log(player, data, data.killAuraAVL, msg);
                //this.fail(player, " b" + b + " p" + p + " vl" + data.killAuraAVL.getViolationLevel());
            }
            return true;
        }
        return false;

    }

    public static ViolationLevelTracker newViolationTracker() {
        return new ViolationLevelTracker(0, 1, 500);
    }

}
