//package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;
//
//import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
//import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.util.EnumChatFormatting;
//
//public class KillAuraSwitchCheck extends AbstractCheck {
//
//    @Override
//    public String getCheatName() {
//        return "KillAura (A)";
//    }
//
//    @Override
//    public String getCheatDescription() {
//        return EnumChatFormatting.RED + "The player is headsnapping between targets";
//    }
//
//    @Override
//    public boolean canSendTimestamp() {
//        return false;
//    }
//
//    @Override
//    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
//        super.checkViolationLevel(player, this.check(player, data), data.killauraSwitchVL);
//    }
//
//    @Override
//    public boolean check(EntityPlayer player, PlayerDataSamples data) {
//        //if (player.isSwingInProgress && player.swingProgressInt == 0) {
//        //    // TODO check on which tick it looks at the entity when hitting
//        //    //if (player.swingProgressInt == 0) {
//        //    //    final MovingObjectPosition movingObjectPosition = player.rayTrace(3.5, 1.F);
//        //    //}
//        //}
//        //logger.info("swingProgressInt " + player.swingProgressInt
//        //        + " | lastSwingTime " + data.lastSwingTime
//        //        + " | lookAngleDiff " + String.format("%.4f", data.lookAngleDiff)
//        //        + " | dYaw " + String.format("%.4f", data.dYaw)
//        //        + " | lastTime_dYawChangedSign " + data.lastTime_dYawChangedSign
//        //);
//        return false;
//    }
//
//    public static ViolationLevelTracker newViolationTracker() {
//        return new ViolationLevelTracker(100);// TODO adjust
//    }
//
//}
