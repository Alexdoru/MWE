package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

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
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.autoblockVL);
    }

    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        // TODO check if player eat/drank something in the tick before to avoid flagging no hit glitch check
        //  print a log message in case it skips and flags
        if (player.isSwingInProgress && data.useItemTime > 20) {
            final ItemStack itemStack = player.getHeldItem();
            if (itemStack != null && swordSet.contains(itemStack.getItem())) {
                // TODO remove debug
                fail(player, this.getCheatName());
                log(player,this.getCheatName(), data.autoblockVL, data,
                        "useItemTime " + data.useItemTime
                );
                return true;
            }
        }
        return false;
    }

    public static ViolationLevelTracker newViolationTracker() {
        return new ViolationLevelTracker(1, 2, 30);
    }

}

