//package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;
//
//import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
//import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector2D;
//import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.util.EnumChatFormatting;
//
//public class OmniSprintCheck extends AbstractCheck {
//
//    @Override
//    public String getCheatName() {
//        return "Movement (A)";
//    }
//
//    @Override
//    public String getCheatDescription() {
//        return EnumChatFormatting.RED + "The player is sprinting in a different direction than the one they are looking at";
//    }
//
//    @Override
//    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
//        super.checkViolationLevel(player, this.check(player, data), data.omnisprintVL);
//    }
//
//    /**
//     * Checks if the player is sprinting in another
//     * direction than the one they are looking at
//     * It happens when using sprint hacks, killaura or
//     * some kind of bow hacks while running
//     */
//    @Override
//    public boolean check(EntityPlayer player, PlayerDataSamples data) {
//        if (player.hurtTime != 0 || data.isNotMoving || data.sprintTime < 15 || player.isRiding() || !player.onGround) {
//            // TODO data.sprintTime resets to 0 on every hit when using keepsprint
//            //  could instead check the players acceleration and velocity if data.sprintTime < 15
//            return false;
//        }
//        if (!data.directionDeltaXZList.hasCollectedSample()) {
//            return false;
//        }
//        for (final Double directionDeltaXZ : data.directionDeltaXZList) {
//            // TODO or check if the sum of direction diff < a value -> which means it's moving almsot straight line
//            //  on each tick diff check if < another value greater than the one above
//            if (directionDeltaXZ > 3D) {
//                return false;
//            }
//        }
//        final Vector2D playersLook = Vector2D.getVectorFromRotation(player.rotationPitch, player.rotationYawHead);
//        final double angularDiff = data.dXdZVector2D.getAngleWithVector(playersLook);
//        if (angularDiff < 60d) {
//            // TODO change the angle it checks here depending on the max angle directionDeltaXZ
//            //  in the loop above on data.directionDeltaXZList
//            return false;
//        }
//        // TODO remove debug stuff
//        fail(player, this.getCheatName());
//        log(player, this.getCheatName(), data.omnisprintVL, data,
//                "angularDiff " + String.format("%.4f", angularDiff)
//        );
//        return true;
//    }
//
//    public static ViolationLevelTracker newViolationTracker() {
//        return new ViolationLevelTracker(2, 1, 20);
//    }
//
//}
