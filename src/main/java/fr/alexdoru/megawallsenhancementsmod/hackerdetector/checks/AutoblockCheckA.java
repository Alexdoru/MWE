package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class AutoblockCheckA extends AbstractCheck {

    @Override
    public String getCheatName() {
        return "Autoblock";
    }

    @Override
    public String getFlagType() {
        return "A";
    }

    @Override
    public String getCheatDescription() {
        return "The player can attack while their sword is blocked";
    }

    @Override
    public boolean canSendReport() {
        return true;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.autoblockAVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        if (player.isSwingInProgress) {
            final ItemStack itemStack = player.getHeldItem();
            if (itemStack != null && itemStack.getItem() instanceof ItemSword) {
                if (data.useItemTime > 6) {
                    data.autoblockAVL.add(5);
                    if (ConfigHandler.debugLogging) {
                        this.log(player, data, data.autoblockAVL, "useItemTime " + data.useItemTime);
                    }
                    return true;
                } else {
                    data.autoblockAVL.substract(2);
                }
            }
        }
        return false;
    }

    public static ViolationLevelTracker newViolationTracker() {
        return new ViolationLevelTracker(150);
    }

}
