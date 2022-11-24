package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector2D;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;

public class OmniSprintCheck extends AbstractCheck {

    @Override
    public String getCheatName() {
        return "Movement (A)";
        // TODO change name to kill aura if player is holding a weapon or/and if player is swinging ?
    }

    @Override
    public String getCheatDescription() {
        return "The player is sprinting in a different direction than the one they are looking at";
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, data.omnisprintVL, this.check(player, data));
    }

    /**
     * Checks if the player is sprinting in another
     * direction than the one they are looking at
     * It happens when using sprint hacks, killaura or
     * some kind of bow hacks while running
     */
    // TODO seems to have issues with frozen laggy players
    //  frequently triggers on player in PIT, might be due to the slime block jump thing
    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        if (player.hurtTime != 0 || data.sprintTime < 15 || player.isRiding()) {
            // TODO data.sprintTime resets to 0 on every hit when using keepsprint
            //  could instead check the players acceleration and velocity if data.sprintTime < 15
            return false;
        }
        /*Skip check for this tick since the player is sprinting but not moving, might be lagging*/
        if (player.posX == player.lastTickPosX && player.posY == player.lastTickPosY && player.posZ == player.lastTickPosZ) {
            return false;
        }
        if (!data.directionDeltaXZList.hasCollectedSample()) {
            return false;
        }
        for (final Double directionDeltaXZ : data.directionDeltaXZList) {
            // TODO or check if the sum of direction diff < a value -> which means it's moving almsot straight line
            //  on each tick diff check if < another value greater than the one above
            if (directionDeltaXZ > 3D) {
                return false;
            }
        }
        final Vector2D positionDiffXZ = new Vector2D(player.posX - player.lastTickPosX, player.posZ - player.lastTickPosZ);
        final Vector2D playersLook = Vector2D.getVectorFromRotation(player.rotationPitch, player.rotationYaw);
        final double angularDiff = positionDiffXZ.getAngleWithVector(playersLook);
        if (angularDiff < 60d) {
            return false;
        }
        // TODO remove debug stuff
        fail(player, "Omnisprint");
        log(player.getName() + " failed Omnisprint check"
                + " angle diff " + String.format("%.4f", angularDiff)
                + " normpositionDiffXZ " + String.format("%.4f", positionDiffXZ.lengthVector())
                + " xPos " + String.format("%.4f", player.posX)
                + " lastTickPosX " + String.format("%.4f", player.lastTickPosX)
                + " yPos " + String.format("%.4f", player.posY)
                + " lastTickPosY " + String.format("%.4f", player.lastTickPosY)
                + " posZ " + String.format("%.4f", player.posZ)
                + " lastTickPosZ " + String.format("%.4f", player.lastTickPosZ)
                + " rotationPitch " + String.format("%.4f", player.rotationPitch)
                + " rotationYaw " + String.format("%.4f", player.rotationYaw)
                + " sprintTime " + data.sprintTime
                + " lastHurtTime " + data.lastHurtTime
        );
        return true;
    }

    public static ViolationLevelTracker getViolationTracker() {
        return new ViolationLevelTracker(2, 1, 20);
    }

}
