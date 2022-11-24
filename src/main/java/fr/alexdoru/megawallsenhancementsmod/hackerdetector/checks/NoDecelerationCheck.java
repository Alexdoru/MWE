//package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;
//
//import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.util.EnumChatFormatting;
//
//public class NoDecelerationCheck extends AbstractCheck {
//
//    @Override
//    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
//        final double yawDiff = player.rotationYaw - player.prevRotationYaw;//TODO need to look a prevyawdiff
//        final double accel = Math.abs(data.positionDiffXZ - data.prevPositionDiffXZ) * 100.0;
//        if (yawDiff > 1.5 && data.positionDiffXZ > 0.15 && accel < 1.0E-5) {
//            this.flag(player, "NoDeceleration " + EnumChatFormatting.GRAY + "yawDiff " + String.format("%.1f", yawDiff) + " accel " + accel);
//        }
//    }
//
//}