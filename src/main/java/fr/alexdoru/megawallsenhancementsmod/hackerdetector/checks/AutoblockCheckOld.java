package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class AutoblockCheckOld extends AbstractCheck {

    @Override
    public String getCheatName() {
        return "Autoblock";
    }

    @Override
    public String getFlagType() {
        return "Old";
    }

    @Override
    public String getCheatDescription() {
        return "The player can attack while their sword is blocked";
    }

    @Override
    public boolean canSendReport() {
        return false;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.autoblockVLOld);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        if (data.isNotMovingXZ()) {
            return false;
        }
        final ItemStack itemStack = player.getHeldItem();
        if (itemStack != null) {
            if (itemStack.getItem() instanceof ItemSword) {
                if (player.isSwingInProgress && data.useItemTime > 20) {
                    if (ConfigHandler.debugLogging) {
                        this.log(player, data, data.autoblockVLOld, "useItemTime " + data.useItemTime);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static ViolationLevelTracker newViolationTracker() {
        return new ViolationLevelTracker(5, 2, 150);
    }

}

