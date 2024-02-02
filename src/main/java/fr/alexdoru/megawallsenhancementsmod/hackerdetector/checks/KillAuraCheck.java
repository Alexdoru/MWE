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

import java.util.Arrays;
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
        return true;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.killAuraVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {

        if (!data.hasAttacked) return false;
        if (player.isRiding()) return false;

        // TODO débugger le systeme de détéction d'attaques en affichant la distance
        //  entre les deux players, le type d'attaque, l'angle entre la camera et le joueur

        final Vec3 attackerEyePos = data.getPositionEyesServer(player);
        // TODO est ce qu'il faut utiliser le pos/yaw du tick d'avant,
        //  quand est ce qu'on recoit le paquet de position par rapport au attack paquet
        // TODO il faut utiliser la pos/angles du tick d'avant, ce que l'on recoit déja quand on recoit le swing
        final Vec3 lookVect = data.getLookServer();
        final Vec3 lookEndPos = attackerEyePos.addVector(lookVect.xCoord * 8D, lookVect.yCoord * 8D, lookVect.zCoord * 8D);
        final PlayerDataSamples targetData = ((EntityPlayerAccessor) data.targetedPlayer).getPlayerDataSamples();

        final int MAX_TICK_DELAY = 5;// TODO mettre 8 ? et un tick sur deux ?

        if (targetData.posXList.size() < MAX_TICK_DELAY) {
            return false;
        }

        final boolean[] hits = new boolean[MAX_TICK_DELAY];
        final double[] hitDistances = new double[MAX_TICK_DELAY];
        double maxDistance = -1D;

        for (int i = 0; i < MAX_TICK_DELAY; i++) {
            if (isInsideHitbox(targetData.posXList.get(i), targetData.posYList.get(i), targetData.posZList.get(i), attackerEyePos)) {
                return false;
            }
            final Vec3 hitOnPlayerPos = getHitVectOnPlayer(targetData.posXList.get(i), targetData.posYList.get(i), targetData.posZList.get(i), attackerEyePos, lookEndPos);
            if (hitOnPlayerPos == null) {
                hits[i] = false;
                hitDistances[i] = -1D;
            } else {
                hits[i] = true;
                hitDistances[i] = attackerEyePos.distanceTo(hitOnPlayerPos);
                maxDistance = Math.max(maxDistance, hitDistances[i]);
            }
        }

        final double STEP_SIZE = 0.1D;
        final float f = 1.0F;
        maxDistance = maxDistance + 1D;// TODO mettre plus de max distance ? en fonction de la speed ?
        final List<EntityPlayer> list = getPlayersInAABBexcluding(player,// TODO look all entity ? through withers
                player.getEntityBoundingBox().addCoord(lookVect.xCoord * maxDistance, lookVect.yCoord * maxDistance, lookVect.zCoord * maxDistance).expand(f, f, f),
                p -> p != data.targetedPlayer && p != mc.thePlayer && !p.isSpectator() && p.canBeCollidedWith());

        int globalInsidePlayer = Integer.MAX_VALUE;
        int globalInsideBlock = Integer.MAX_VALUE;
        final int[] insidePlayerArray = new int[MAX_TICK_DELAY];
        final int[] insideBlockArray = new int[MAX_TICK_DELAY];

        for (int i = 0; i < MAX_TICK_DELAY; i++) {
            if (!hits[i]) continue;
            final int iterMax = (int) (hitDistances[i] / STEP_SIZE);
            int blockXpos = -1;
            int blockYpos = -1;
            int blockZpos = -1;
            boolean canHitThroughBlock = false;
            int timesInsideBlock = 0;
            int timesInsidePlayer = 0;
            for (int j = 0; j < iterMax + 1; j++) {
                final double dx = attackerEyePos.xCoord + j * STEP_SIZE * lookVect.xCoord;
                final double dy = attackerEyePos.yCoord + j * STEP_SIZE * lookVect.yCoord;
                final double dz = attackerEyePos.zCoord + j * STEP_SIZE * lookVect.zCoord;
                final int xpos = MathHelper.floor_double(dx);
                final int ypos = MathHelper.floor_double(dy);
                final int zpos = MathHelper.floor_double(dz);
                if (xpos != blockXpos || ypos != blockYpos || zpos != blockZpos) {// TODO cache c'est tjrs les meme blocks
                    final IBlockState iblockstate = mc.theWorld.getBlockState(new BlockPos(xpos, ypos, zpos));
                    final Block block = iblockstate.getBlock();
                    canHitThroughBlock = !(block.canCollideCheck(iblockstate, false) && block.isFullBlock());
                    blockXpos = xpos;
                    blockYpos = ypos;
                    blockZpos = zpos;
                }
                for (final EntityPlayer entity : list) {
                    final PlayerDataSamples eData = ((EntityPlayerAccessor) entity).getPlayerDataSamples();
                    if (eData.speedXList.size() < MAX_TICK_DELAY) continue;
                    if (isInsideHitbox(eData.posXList.get(i), eData.posYList.get(i), eData.posZList.get(i), dx, dy, dz)) {
                        timesInsidePlayer++;
                        break;
                    }
                }
                if (!canHitThroughBlock) {
                    timesInsideBlock++;
                }
            }
            insideBlockArray[i] = timesInsideBlock;
            insidePlayerArray[i] = timesInsidePlayer;
            globalInsideBlock = Math.min(globalInsideBlock, timesInsideBlock);
            globalInsidePlayer = Math.min(globalInsidePlayer, timesInsidePlayer);
            if (globalInsideBlock + globalInsidePlayer == 0) {
                data.killAuraVL.substract(15);// TODO decrease over time not attacks
                HackerDetector.log(player.getName() + " attacked " + data.targetedPlayer.getName() + " passed check");
                return false;
            }
        }

        if (globalInsideBlock + globalInsidePlayer > 0) {
            data.killAuraVL.add(Math.min(globalInsideBlock, 10) * 2 + globalInsidePlayer);
            if (ConfigHandler.debugLogging) {
                final String msg = "target : " + data.targetedPlayer.getName() + " globalInsideBlock " + globalInsideBlock + " globalInsidePlayer " + globalInsidePlayer
                        + " hits " + Arrays.toString(hits)
                        + " insideBlockArray " + Arrays.toString(insideBlockArray)
                        + " insidePlayerArray " + Arrays.toString(insidePlayerArray)
                        + " hitDistances " + Arrays.toString(hitDistances);
                this.fail(player, " b " + globalInsideBlock + " p " + globalInsidePlayer);
                this.log(player, data, data.killAuraVL, msg);
            }
            return true;
        } else {
            data.killAuraVL.substract(10);
            return false;
        }

    }

    public static ViolationLevelTracker newViolationTracker() {
        return new ViolationLevelTracker(50);
    }

}
