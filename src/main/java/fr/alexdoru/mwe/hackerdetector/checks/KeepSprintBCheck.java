package fr.alexdoru.mwe.hackerdetector.checks;

import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.mwe.hackerdetector.data.SampleListD;
import fr.alexdoru.mwe.hackerdetector.utils.Vector2D;
import fr.alexdoru.mwe.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class KeepSprintBCheck extends Check {

    @Override
    public String getCheatName() {
        return "KeepSprint";
    }

    @Override
    public String getCheatDescription() {
        return "The player's velocity doesn't decrease after attacking other players";
    }

    @Override
    public String getFlagType() {
        return "B";
    }

    @Override
    public boolean canSendReport() {
        return true;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.keepSprintBVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        if (player.isRiding()) return false;
        if (data.isNotMovingXZ()) return false;
        if (!data.serverPosXList.hasCollected()) return false;
        if (checkAttack(data) && (data.isOnFlatGround() || accel(data.serverPosYList) < -25d)) {
            final double speedXZ = speedXZ(data, 0);
            final double prevSpeedXZ = speedXZ(data, 1);
            if (prevSpeedXZ > 4d) {
                if (speedXZ > 0.9d * prevSpeedXZ) {
                    data.keepSprintBVL.add(10);
                    if (ConfigHandler.debugLogging) {
                        this.log(player, data, data.keepSprintBVL,
                                " | attacked " + data.hasAttacked() +
                                        " | prevSpeedXZ " + String.format("%.4f", prevSpeedXZ) +
                                        " | speedXZ " + String.format("%.4f", speedXZ) +
                                        " | onGround " + player.onGround +
                                        " | moveDiff " + String.format("%.2f", getMoveAngleDiff(data)) +
                                        " | moveLookDiff " + String.format("%.2f", data.getMoveLookAngleDiff()) +
                                        " | posY " + data.serverPosYList);
                        this.fail(player);
                    }
                } else {
                    data.keepSprintBVL.substract(data.hasAttacked() ? 2 : 5);
                }
            }
        }
        return false;
    }

    public static ViolationLevelTracker newVL() {
        return new ViolationLevelTracker(30);
    }

    private static double getMoveAngleDiff(PlayerDataSamples data) {
        return MathHelper.wrapAngleTo180_double(
                new Vector2D(data.serverPosZList.get(0) - data.serverPosZList.get(1), -(data.serverPosXList.get(0) - data.serverPosXList.get(1))).getOrientedAngle()
                        - new Vector2D(data.serverPosZList.get(1) - data.serverPosZList.get(2), -(data.serverPosXList.get(1) - data.serverPosXList.get(2))).getOrientedAngle());
    }

    private static boolean checkAttack(PlayerDataSamples data) {
        return data.serverUpdatesList.get(0) == 1 && (data.attackList.get(0) || !data.attackList.get(0) && data.attackList.get(1));
    }

    private static double speedXZ(PlayerDataSamples data, int i) {
        final double vx = 10d * (data.serverPosXList.get(i) - data.serverPosXList.get(1 + i));
        final double vz = 10d * (data.serverPosZList.get(i) - data.serverPosZList.get(1 + i));
        return Math.sqrt(vx * vx + vz * vz);
    }

    private static double accelXZ(PlayerDataSamples data) {
        final double ax = accel(data.serverPosXList);
        final double az = accel(data.serverPosZList);
        return Math.sqrt(ax * ax + az * az);
    }

    private static double accel(SampleListD list) {
        return 10d * 10d * (list.get(2) - 2d * list.get(1) + list.get(0));
    }

}
