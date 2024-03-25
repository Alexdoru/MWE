package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;

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
        if (!data.hasAttacked()) return false;
        if (player.isRiding()) return false;

        if (data.onGroundTime > 10 && data.serverPosYList.hasCollected()) {

            if (!data.serverPosYList.isSameValues()) {
                return false;
            }

        } else if (data.airTime > 8) {

        }

        return false;
    }

    public static ViolationLevelTracker newVL() {
        return new ViolationLevelTracker(50);
    }

}
