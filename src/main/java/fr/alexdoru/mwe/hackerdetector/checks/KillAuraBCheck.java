package fr.alexdoru.mwe.hackerdetector.checks;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.mwe.hackerdetector.utils.ViolationLevelTracker;
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
        if (data.hasAttacked()) {
            if (data.useItemTime > 6 && data.timeEating < 33 && data.usedItemIsConsumable && data.lastEatTime > 32) {
                if (MWEConfig.debugLogging) {
                    final ItemStack itemStack = player.getHeldItem();
                    final Item item = itemStack == null ? null : itemStack.getItem();
                    super.log(player, data, data.killAuraBVL,
                            " | " + data.attackInfo.attackType.name() +
                                    " | useItemTime " + data.useItemTime
                                    + " | lastEatTime " + data.lastEatTime
                                    + (item != null ? " | item held " + item.getRegistryName() : ""));
                }
                return true;
            }
        }
        return false;
    }

    public static ViolationLevelTracker newVL() {
        return new ViolationLevelTracker(100, 1, 110);
    }

}
