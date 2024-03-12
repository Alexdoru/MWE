package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class KillAuraBCheck extends Check {

    @Override
    public String getCheatName() {
        return "KillAura";
    }

    @Override
    public String getCheatDescription() {
        return "The player can attack while eating and drinking potions";
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
        super.checkViolationLevel(player, this.check(player, data), data.killAuraBVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        if (data.hasSwung) {
            if (data.useItemTime > 3 && data.usedItemIsConsumable && data.lastEatTime > 32) {
                data.killAuraBVL.add(5);
                if (ConfigHandler.debugLogging) {
                    this.log(player, data, data.killAuraBVL, null);
                }
                return true;
            } else if (data.useItemTime == 0) {
                data.killAuraBVL.substract(2);
            }
        }
        return false;
    }

    @Override
    protected void log(EntityPlayer player, PlayerDataSamples data, ViolationLevelTracker vl, String extramsg) {
        final ItemStack itemStack = player.getHeldItem();
        final Item item = itemStack == null ? null : itemStack.getItem();
        super.log(player, data, vl,
                " | useItemTime " + data.useItemTime
                        + " | lastEatTime " + data.lastEatTime
                        + (item != null ? " | item held " + item.getRegistryName() : "")
        );
    }

    public static ViolationLevelTracker newViolationTracker() {
        return new ViolationLevelTracker(25);
    }

}
