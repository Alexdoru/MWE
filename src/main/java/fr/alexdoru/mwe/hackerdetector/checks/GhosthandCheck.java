package fr.alexdoru.mwe.hackerdetector.checks;

import fr.alexdoru.mwe.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.hackerdetector.HackerDetector;
import fr.alexdoru.mwe.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.mwe.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import java.util.List;

public class GhosthandCheck extends Check {

    @Override
    public String getCheatName() {
        return "Ghosthand";
    }

    @Override
    public String getCheatDescription() {
        return "The player can mine blocks through other players";
    }

    @Override
    public boolean canSendReport() {
        return false;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.ghosthandVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        if (data.blockTouched == null) return false;
        if (player.isRiding()) return false;
        if (getEyesToBlockVect(player, data, data.blockTouched).normSquared() > 28.79422863D) return false;
        final double maxReach = 5D;
        final Vec3 eyePos = data.getPositionEyesServer(player);
        final Vec3 lookVect = data.getLookServer();
        final Vec3 lookEndPos = eyePos.addVector(lookVect.xCoord * maxReach, lookVect.yCoord * maxReach, lookVect.zCoord * maxReach);
        final Vec3 hitVect = getHitVectOnBlock(data.blockTouched, eyePos, lookEndPos);
        if (hitVect == null) return false;
        final double distance = eyePos.distanceTo(hitVect);
        final float f = 1.0F;
        final int MAX_TICK_DELAY = 10;
        final List<PlayerDataSamples> nearbyPlayers = getPlayersDataInAABBexcluding(player,
                player.getEntityBoundingBox().addCoord(lookVect.xCoord * distance, lookVect.yCoord * distance, lookVect.zCoord * distance).expand(f, f, f),
                p -> p != mc.thePlayer && p.canBeCollidedWith() && HackerDetector.isValidPlayer(p.getUniqueID()) && !p.isInvisible() && ((EntityPlayerAccessor) p).getPlayerDataSamples().posXList.size() >= MAX_TICK_DELAY);
        if (nearbyPlayers.isEmpty()) {
            data.ghosthandVL.substract(1);
            return false;
        }

        final double STEP_SIZE = 0.2D;
        final int maxSteps = (int) (distance / STEP_SIZE);
        for (int i = 1; i < MAX_TICK_DELAY; i++) {
            boolean isInsidePlayer = false;
            stepLoop:
            for (int j = 0; j < maxSteps + 1; j++) {
                final double dx = eyePos.xCoord + j * STEP_SIZE * lookVect.xCoord;
                final double dy = eyePos.yCoord + j * STEP_SIZE * lookVect.yCoord;
                final double dz = eyePos.zCoord + j * STEP_SIZE * lookVect.zCoord;
                for (final PlayerDataSamples eData : nearbyPlayers) {
                    if (isInsideHitbox(eData.posXList.get(i), eData.posYList.get(i), eData.posZList.get(i), dx, dy, dz)) {
                        isInsidePlayer = true;
                        break stepLoop;
                    }
                }
            }
            if (!isInsidePlayer) {
                data.ghosthandVL.substract(1);
                return false;
            }
        }

        data.ghosthandVL.add(1);
        if (ConfigHandler.debugLogging && data.ghosthandVL.getViolationLevel() > 2) {
            this.log(player, data, data.ghosthandVL, null);
        }
        return true;

    }

    public static ViolationLevelTracker newVl() {
        return new ViolationLevelTracker(8);
    }

}
