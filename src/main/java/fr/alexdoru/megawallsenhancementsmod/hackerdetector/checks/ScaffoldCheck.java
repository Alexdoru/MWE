package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.SampleListD;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector2D;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class ScaffoldCheck extends Check {

    @Override
    public String getCheatName() {
        return "Scaffold";
    }

    @Override
    public String getCheatDescription() {
        return "The player places blocks under their feet automatically while gaining height rapidly";
    }

    @Override
    public boolean canSendReport() {
        return true;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.scaffoldVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        if (player.isRiding() || data.serverPosYList.size() < 4) return false;
        if (player.isSwingInProgress && player.hurtTime == 0 && data.serverPitchList.get(0) > 50f && data.getSpeedXZSq() > 9d) {
            final ItemStack itemStack = player.getHeldItem();
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                final double angleDiff = Math.abs(getAngleDiff(data.speedXList.get(0), data.speedZList.get(0), data.serverYawHeadList.get(0)));
                final double speedXZSq = data.getSpeedXZSq();
                if (angleDiff > 165d && speedXZSq < 100d) {
                    final double speedY = data.speedYList.get(0);
                    final double avgAccelY = avgAccel(data.serverPosYList);
                    if (speedY < 15d && speedY > 4d && avgAccelY > -25d) {
                        if (ConfigHandler.debugLogging) {
                            final String msg = " | pitch " + String.format("%.2f", data.serverPitchList.get(0)) + " | speedXZ " + String.format("%.2f", data.getSpeedXZ()) + " | angleDiff " + String.format("%.2f", angleDiff) + " | speedY " + String.format("%.2f", speedY) + " | avgAccelY " + String.format("%.2f", avgAccelY);
                            this.log(player, data, data.scaffoldVL, msg);
                        }
                        return true;
                    } else if (speedY < 4d && speedY > -1d && Math.abs(speedY) > 0.005d && speedXZSq > 25d) {
                        if (ConfigHandler.debugLogging) {
                            final String msg = " | pitch " + String.format("%.2f", data.serverPitchList.get(0)) + " | speedXZ " + String.format("%.2f", data.getSpeedXZ()) + " | angleDiff " + String.format("%.2f", angleDiff) + " | speedY " + String.format("%.2f", speedY) + " | avgAccelY " + String.format("%.2f", avgAccelY);
                            this.log(player, data, data.scaffoldVL, msg);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static ViolationLevelTracker newViolationTracker() {
        return new ViolationLevelTracker(2, 1, 24);
    }

    private static double getAngleDiff(double dx, double dz, double yaw) {
        return MathHelper.wrapAngleTo180_double(new Vector2D(dz, -dx).getOrientedAngle() - yaw);
    }

    private static double avgAccel(SampleListD list) {
        return 50d * (list.get(3) - list.get(2) - list.get(1) + list.get(0));
    }

}
