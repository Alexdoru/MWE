package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;

public class KillAuraHeadsnapCheck extends AbstractCheck {

    @Override
    public String getCheatName() {
        return "KillAura (A)";
    }

    @Override
    public String getCheatDescription() {
        return "The player is headsnapping between targets";
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, data.killauraHeadsnapVL, this.check(player, data));
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        // TODO
        //if (player.isSwingInProgress) {
        //    debug("swing is in progress");
        //}
        return false;
    }

    public static ViolationLevelTracker getViolationTracker() {
        return new ViolationLevelTracker(5, 1, 20);
    }

}
