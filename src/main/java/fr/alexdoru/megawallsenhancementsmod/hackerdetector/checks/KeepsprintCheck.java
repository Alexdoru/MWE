package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class KeepsprintCheck extends AbstractCheck {

    @Override
    public String getCheatName() {
        return "KeepSprint";
    }

    @Override
    public String getCheatDescription() {
        return "The player's sprint doesn't turn off when using items (blocking sword, eating, drinking, using bow...)";
    }

    @Override
    public boolean canSendReport() {
        return false;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.keepsprintVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        if (data.isNotMovingXZ() || player.isRiding()) {
            return false;
        }
        /* It takes 32 ticks to eat/drink one food/potion item */
        if (data.sprintTime > 70 && data.useItemTime > 4) {
            /* If the player is moving slower than the base running speed, we consider it is keepsprint */
            if (player.hurtTime == 0 && data.getSpeedXZ() < 4D) {
                data.keepsprintVL.add(2);
                if (ConfigHandler.debugLogging) this.log(player, data, data.keepsprintVL, null);
            }
            return true;
        } else if (data.sprintTime == 0 && data.useItemTime > 4) {
            data.keepsprintVL.substract(3);
            return false;
        }
        return false;
    }

    @Override
    protected void log(EntityPlayer player, PlayerDataSamples data, ViolationLevelTracker vl, String extramsg) {
        final ItemStack itemStack = player.getHeldItem();
        final Item item = itemStack == null ? null : itemStack.getItem();
        super.log(player, data, data.keepsprintVL,
                "sprintTime " + data.sprintTime
                        + " useItemTime " + data.useItemTime
                        + " speedXZ " + data.getSpeedXZ()
                        + (item != null ? " item held " + item.getRegistryName() : "")
        );
    }

    public static ViolationLevelTracker newViolationTracker() {
        return new ViolationLevelTracker(34);
    }

}
