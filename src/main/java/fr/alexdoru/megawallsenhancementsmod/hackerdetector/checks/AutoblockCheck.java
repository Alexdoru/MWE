package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashSet;
import java.util.Set;

public class AutoblockCheck extends AbstractCheck {

    private static final Set<Item> swordSet = new HashSet<>();

    static {
        swordSet.add(Items.wooden_sword);
        swordSet.add(Items.stone_sword);
        swordSet.add(Items.golden_sword);
        swordSet.add(Items.iron_sword);
        swordSet.add(Items.diamond_sword);
    }

    @Override
    public String getCheatName() {
        return "Autoblock";
    }

    @Override
    public String getCheatDescription() {
        return EnumChatFormatting.RED + "The player can attack while their sword is blocked";
    }

    @Override
    public boolean canSendTimestamp() {
        return true;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.autoblockVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        if (data.isNotMoving) {
            return false;
        }
        final ItemStack itemStack = player.getHeldItem();
        if (itemStack != null) {
            if (swordSet.contains(itemStack.getItem())) {
                if (data.disabledAutoblockCheck) {
                    return false;
                }
                if (player.isSwingInProgress && data.useItemTime > 20) {
                    if (data.lastEatDrinkTime < 30) {
                        data.disabledAutoblockCheck = true;
                        if (ConfigHandler.isDebugMode) {
                            logger.info("Disabled autoblock check for " + player.getName() + " lastEatDrinkTime" + data.lastEatDrinkTime);
                        }
                        return false;
                    }
                    if (ConfigHandler.isDebugMode) {
                        log(player, this.getCheatName(), data.autoblockVL, data, "useItemTime " + data.useItemTime + " lastEatDrinkTime" + data.lastEatDrinkTime);
                    }
                    return true;
                }
            } else {
                data.disabledAutoblockCheck = false;
            }
        } else {
            data.disabledAutoblockCheck = false;
        }
        return false;
    }

    public static ViolationLevelTracker newViolationTracker() {
        return new ViolationLevelTracker(5, 2, 150);
    }

}

