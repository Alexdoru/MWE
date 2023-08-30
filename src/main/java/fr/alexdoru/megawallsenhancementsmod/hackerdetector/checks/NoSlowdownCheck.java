package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class NoSlowdownCheck extends AbstractCheck {

    @Override
    public String getCheatName() {
        return "NoSlowdown";
    }

    @Override
    public String getCheatDescription() {
        return "The player is running while using items (blocking sword, eating, drinking, using bow...)";
    }

    @Override
    public boolean canSendReport() {
        return true;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.noSlowdownVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        /* If the player is moving slower than the base running speed, we consider it is keepsprint */
        if (!player.isRiding() && player.hurtTime == 0 && data.getSpeedXZ() >= 4D) {
            /* It takes 32 ticks to eat/drink one food/potion item */
            if (data.useItemTime > 5) {
                if (data.sprintTime > 70) {
                    data.noSlowdownVL.add(2);
                    if (ConfigHandler.debugLogging) {
                        this.log(player, data, data.noSlowdownVL, null);
                    }
                    return true;
                } else if (data.sprintTime == 0) {
                    data.noSlowdownVL.substract(3);
                }
            }
        }
        return false;
    }

    @Override
    protected void log(EntityPlayer player, PlayerDataSamples data, ViolationLevelTracker vl, String extramsg) {
        final ItemStack itemStack = player.getHeldItem();
        final Item item = itemStack == null ? null : itemStack.getItem();
        super.log(player, data, data.noSlowdownVL,
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
