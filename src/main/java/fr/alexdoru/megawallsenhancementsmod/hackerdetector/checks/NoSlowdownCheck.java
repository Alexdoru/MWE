package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;

public class NoSlowdownCheck extends AbstractCheck {

    @Override
    public String getCheatName() {
        return "NoSlowdown";
    }

    @Override
    public String getCheatDescription() {
        return "The player can sprint while using items (blocking sword, eat, drink, use bow...)";
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, data.noslowdownVL, this.check(player, data));
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        /* It takes 32 ticks to eat/drink one food/potion item */
        if (data.sprintTime > 40 && data.useItemTime > 8) {
            // TODO remove debug
            fail(player, "noslowdown");
            log(player.getName() + " failed noslowdown check"
                    + " sprintTime " + data.sprintTime
                    + " useItemTime " + data.useItemTime
            );
            return true;
        }
        return false;
    }

    public static ViolationLevelTracker getViolationTracker() {
        return new ViolationLevelTracker(1, 2, 15);
    }

}
