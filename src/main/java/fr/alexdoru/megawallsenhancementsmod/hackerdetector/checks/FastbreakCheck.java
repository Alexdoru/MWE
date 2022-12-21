package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.BrokenBlock;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.Iterator;

public class FastbreakCheck extends AbstractCheck {

    @Override
    public String getCheatName() {
        return "Fastbreak";
    }

    @Override
    public String getCheatDescription() {
        return EnumChatFormatting.RED + "The player can break blocks faster than normal";
    }

    @Override
    public boolean canSendTimestamp() {
        return true;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        super.checkViolationLevel(player, this.check(player, data), data.fastbreakVL);
    }

    /**
     * See {@link net.minecraft.client.multiplayer.PlayerControllerMP#onPlayerDamageBlock}
     * Since we can't see enchantements of other players on Hypixel
     * This check is hardcoded for a diamond pickaxe with efficiency III
     * <p>
     * If the block broken is in < 30 m radius of the player,
     * we can retrieve the ID of the entity breaking the block
     * by listening to the handleBlockBreakAnim hook, but we're not
     * doing that here
     */
    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        if (FKCounterMod.isInMwGame && player.isSwingInProgress && !HackerDetector.INSTANCE.brokenBlocksList.isEmpty()) {
            final ItemStack itemStack = player.getHeldItem();
            if (itemStack != null && itemStack.isItemEnchanted() && itemStack.getItem() == Items.diamond_pickaxe) {
                final Iterator<BrokenBlock> iterator = HackerDetector.INSTANCE.brokenBlocksList.iterator();
                while (iterator.hasNext()) {
                    final BrokenBlock brokenBlock = iterator.next();
                    if (isPlayerLookingAtBlock(player, brokenBlock.blockPos)) {
                        iterator.remove();
                        final long recordedBreakTime = brokenBlock.breakTime - data.lastBreakBlockTime;
                        data.lastBreakBlockTime = brokenBlock.breakTime;
                        final float expectedTimeToBreak = 50F * getTimeToHarvestBlock(getBlockStrengthMW(player, brokenBlock.blockPos, brokenBlock.block));
                        data.breakTimeRatio.add(recordedBreakTime / expectedTimeToBreak);
                        if (data.breakTimeRatio.hasCollectedSample()) {
                            final float avg = average(data.breakTimeRatio);
                            if (avg < 0.8F) {
                                data.fastbreakVL.add((int) Math.floor((0.8F - avg) * 10F));
                                if (ConfigHandler.isDebugMode) {
                                    logger.info(player.getName() + " failed Fastbreak check" +
                                            " | vl " + data.fastbreakVL.getViolationLevel() +
                                            " | avg " + String.format("%.4f", avg) +
                                            " | expectedTimeToBreak " + expectedTimeToBreak +
                                            " | recordedBreakTime " + recordedBreakTime);
                                }
                                return true;
                            } else {
                                data.fastbreakVL.substract(1);
                                return false;
                            }
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public static ViolationLevelTracker newViolationTracker() {
        return new ViolationLevelTracker(40);
    }

    /**
     * Returns the block strenght for the provided block and player
     * Harcoded to diamond pickaxe efficiency III
     */
    private float getBlockStrengthMW(EntityPlayer player, BlockPos pos, Block block) {
        final float hardness = block.getBlockHardness(null, pos);
        if (hardness < 0.0F) {
            return 0.0F;
        }
        if (canHarvestBlock(block)) {
            return getBreakSpeed(player, block) / hardness / 30F;
        } else {
            return getBreakSpeed(player, block) / hardness / 100F;
        }
    }

    private boolean canHarvestBlock(Block block) {
        if (block.getMaterial().isToolNotRequired()) {
            return true;
        }
        final IBlockState state = block.getDefaultState();
        /* Hardcoded for diamond pickaxe */
        final int toolLevel = 3;
        return toolLevel >= block.getHarvestLevel(state);
    }

    private float getBreakSpeed(EntityPlayer player, Block block) {
        float f = Items.diamond_pickaxe.getStrVsBlock(null, block);
        if (f > 1.0F) {
            final int efficiencyModifier = 3;
            f += (float) (efficiencyModifier * efficiencyModifier + 1);
        }
        if (player.isPotionActive(Potion.digSpeed)) {
            f *= 1.0F + (float) (player.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
        }
        if (player.isPotionActive(Potion.digSlowdown)) {
            final float f1;
            switch (player.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
            }
            f *= f1;
        }
        return f < 0 ? 0 : f;
    }

}
