package fr.alexdoru.mwe.hackerdetector.checks;

import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.mwe.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class KeepSprintACheck extends Check {

    @Override
    public String getCheatName() {
        return "KeepSprint";
    }

    @Override
    public String getCheatDescription() {
        return "The player's sprint doesn't turn off when using items (blocking sword, eating, drinking, using bow...)";
    }

    @Override
    public String getFlagType() {
        return "A";
    }

    @Override
    public boolean canSendReport() {
        return false;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.keepsprintAVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        // If the player is moving slower than the base running speed, we consider it is keepsprint
        if (data.isNotMovingXZ() || player.isRiding()) return false;
        if (data.useItemTime > 5 && data.sprintTime > 0) {
            if (Math.abs(data.getMoveLookAngleDiff()) > 135d) {
                data.keepsprintAVL.substract(3);
                return false; // rubber band
            }
            final boolean invalidSprint;
            if (data.usedItemIsConsumable) {
                if (data.useItemTime > 32) return false;
                if (data.sprintTime > 32) {
                    invalidSprint = true;
                } else {
                    if (data.sprintTime == data.useItemTime && data.useItemTime < 12) return false;
                    invalidSprint = data.sprintTime > data.useItemTime + 3 || data.lastEatTime > 32 && data.sprintTime > 5;
                }
            } else {
                if (player.getHeldItem().getItem() instanceof ItemSword) return false;
                invalidSprint = data.sprintTime > 5;
            }
            if (invalidSprint && data.getSpeedXZSq() < 6.25D) {
                data.keepsprintAVL.add(2);
                if (ConfigHandler.debugLogging) {
                    this.log(player, data, data.keepsprintAVL, null);
                }
                return true;
            }
        } else if (data.useItemTime > 5 && data.sprintTime == 0) {
            data.keepsprintAVL.substract(3);
        }
        return false;
    }

    @Override
    protected void log(EntityPlayer player, PlayerDataSamples data, ViolationLevelTracker vl, String extramsg) {
        final ItemStack itemStack = player.getHeldItem();
        final Item item = itemStack == null ? null : itemStack.getItem();
        super.log(player, data, vl,
                " | sprintTime " + data.sprintTime
                        + " | useItemTime " + data.useItemTime
                        + " | lastEatTime " + data.lastEatTime
                        + " | speedXZ " + String.format("%.2f", data.getSpeedXZ())
                        + (item != null ? " | item held " + item.getRegistryName() : "")
                        + " | moveDiff " + String.format("%.2f", Math.abs(data.getMoveLookAngleDiff())));
    }

    public static ViolationLevelTracker newVL() {
        return new ViolationLevelTracker(48);
    }

}
