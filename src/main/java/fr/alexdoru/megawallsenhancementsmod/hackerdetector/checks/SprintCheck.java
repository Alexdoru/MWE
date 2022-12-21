package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class SprintCheck extends AbstractCheck {

    /**
     * This field is used to return a different cheat description message
     * It is common to all players but since it is set and then accessed while it
     * is checking the same player it is ok
     */
    private boolean isNoslowCheck = false;

    @Override
    public String getCheatName() {
        return isNoslowCheck ? "NoSlowdown" : "KeepSprint";
    }

    @Override
    public String getCheatDescription() {
        return EnumChatFormatting.RED + (isNoslowCheck ?
                "The player is running while using items (blocking sword, eat, drink, use bow...)" :
                "The player's sprint doesn't reset when using items (blocking sword, eat, drink, use bow...)");
    }

    @Override
    public boolean canSendTimestamp() {
        return isNoslowCheck;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.noslowdownVL, data.keepsprintUseItemVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        if (data.isNotMoving || player.isRiding()) {
            return false;
        }
        /* It takes 32 ticks to eat/drink one food/potion item */
        if (data.sprintTime > 33 && data.useItemTime > 4) {
            /* If the player is moving slower than the base running speed, we consider it is keepsprint */
            if (player.hurtTime != 0 || data.dXdZVector2D.norm() < 0.25D) {
                isNoslowCheck = false;
                data.keepsprintUseItemVL.add(2);
                if (ConfigHandler.isDebugMode) {
                    final ItemStack itemStack = player.getHeldItem();
                    final Item item = itemStack == null ? null : itemStack.getItem();
                    log(player, this.getCheatName(), data.keepsprintUseItemVL, data,
                            "sprintTime " + data.sprintTime
                                    + " useItemTime " + data.useItemTime
                                    + " isNoslowCheck " + isNoslowCheck
                                    + (item != null ? " item held " + item.getRegistryName() : "")
                    );
                }
            } else {
                isNoslowCheck = true;
                data.noslowdownVL.add(2);
                if (ConfigHandler.isDebugMode) {
                    final ItemStack itemStack = player.getHeldItem();
                    final Item item = itemStack == null ? null : itemStack.getItem();
                    log(player, this.getCheatName(), data.noslowdownVL, data,
                            "sprintTime " + data.sprintTime
                                    + " useItemTime " + data.useItemTime
                                    + " isNoslowCheck " + isNoslowCheck
                                    + (item != null ? " item held " + item.getRegistryName() : "")
                    );
                }
            }
            return true;
        } else if (player.hurtTime == 0 && data.sprintTime == 0 && data.useItemTime > 8 && !data.dXdZVector2D.isZero()) {
            data.keepsprintUseItemVL.substract(3);
            return false;
        }
        data.noslowdownVL.substract(3);
        return false;
    }

    public static ViolationLevelTracker newNoslowdownViolationTracker() {
        return new ViolationLevelTracker(34);
    }

    public static ViolationLevelTracker newKeepsprintViolationTracker() {
        return new ViolationLevelTracker(34);
    }

}
