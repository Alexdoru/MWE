package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.enums.MWClass;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.BrokenBlock;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class FastbreakCheck extends AbstractCheck {

    @Override
    public String getCheatName() {
        return "Fastbreak";
    }

    @Override
    public String getCheatDescription() {
        return "The player can break blocks faster than normal";
    }

    @Override
    public boolean canSendReport() {
        return true;
    }

    @Override
    public void performCheck(EntityPlayer player, PlayerDataSamples data) {
        this.check(player, data);
    }

    /**
     * See {@link net.minecraft.client.multiplayer.PlayerControllerMP#onPlayerDamageBlock}
     * Since we can't see enchantements of other players on Hypixel
     * This check is hardcoded for a diamond pickaxe with efficiency III with haste II
     * <p>
     * If the broken block is in < 30 m radius of the player,
     * we can retrieve the ID of the entity breaking the block
     * by listening to the handleBlockBreakAnim hook, but we're not
     * doing that here
     */
    @Override
    public boolean check(EntityPlayer player, PlayerDataSamples data) {
        checkPlayerBreakingBlocks(player, data);
        return false;
    }

    /**
     * Checks if the player is breaking some blocks
     *
     * @param player - it's mc.thePlayer
     */
    public void checkPlayerSP(EntityPlayer player) {
        checkPlayerBreakingBlocks(player, null);
    }

    private static void checkPlayerBreakingBlocks(EntityPlayer player, PlayerDataSamples data) {
        if (isCheckActive() && player.isSwingInProgress && !HackerDetector.INSTANCE.brokenBlocksList.isEmpty()) {
            final ItemStack stack = player.getHeldItem();
            if (stack == null) return;
            for (final BrokenBlock brokenBlock : HackerDetector.INSTANCE.brokenBlocksList) {
                if (isAppropriateTool(stack, brokenBlock) && isPlayerLookingAtBlock(player, data, brokenBlock.blockPos)) {
                    brokenBlock.addPlayer(player);
                    // return after one block, otherwise it can false flag when a golem
                    // uses their ability, it does 6 or more breaking block animations
                    // in the same area
                    return;
                }
            }
        }
    }

    private static boolean isAppropriateTool(ItemStack stack, BrokenBlock brokenBlock) {
        if ("pickaxe".equals(brokenBlock.tool)) {
            return stack.isItemEnchanted() && stack.getItem() == Items.diamond_pickaxe;
        } else if (brokenBlock.tool == null || "axe".equals(brokenBlock.tool)) {
            // for trapped chests the tool is null
            return stack.getItem() instanceof ItemAxe;
        }
        return false;
    }

    /**
     * Method to resolve conflicts when multiple players are looking
     * at the same block when breaking it, for example when double-mining
     */
    public void onTickEnd() {
        if (isCheckActive() && mc.theWorld != null && mc.thePlayer != null && mc.theWorld.isRemote) {
            for (final BrokenBlock brokenBlock : HackerDetector.INSTANCE.brokenBlocksList) {
                if (brokenBlock.playerList == null) continue;
                long oldestTime = System.currentTimeMillis();
                EntityPlayer playerBreaking = null;
                for (final EntityPlayer player : brokenBlock.playerList) {
                    if (player instanceof EntityPlayerAccessor) {
                        final PlayerDataSamples data = ((EntityPlayerAccessor) player).getPlayerDataSamples();
                        if (data.lastBreakBlockTime - oldestTime < 0) {
                            oldestTime = data.lastBreakBlockTime;
                            playerBreaking = player;
                        }
                    }
                }
                if (playerBreaking == null) continue;
                final PlayerDataSamples data = ((EntityPlayerAccessor) playerBreaking).getPlayerDataSamples();
                final long recordedBreakTime = brokenBlock.breakTime - data.lastBreakBlockTime;
                data.lastBreakBlockTime = brokenBlock.breakTime;
                if (playerBreaking == mc.thePlayer || !"pickaxe".equals(brokenBlock.tool)) continue;
                final float expectedBreakTime = 50F * getTimeToHarvestBlock(getBlockStrengthMW(playerBreaking, brokenBlock.blockPos, brokenBlock.block));
                final float breakTimeRatio = recordedBreakTime / expectedBreakTime;
                if (breakTimeRatio < 0.95F) {
                    data.fastbreakVL.add(MathHelper.clamp_int(MathHelper.floor_float((1F - breakTimeRatio) * 20F), 1, 4));
                    if (ConfigHandler.debugLogging && data.fastbreakVL.getViolationLevel() > 6) {
                        this.log(playerBreaking, data, data.fastbreakVL,
                                " | breakTimeRatio " + String.format("%.2f", breakTimeRatio) +
                                        " | breakTime " + recordedBreakTime + "/" + (int) expectedBreakTime +
                                        " | block " + brokenBlock.block.getRegistryName());
                        //this.fail(playerBreaking, " " + recordedBreakTime + "/" + expectedBreakTime + " vl" + data.fastbreakVL.getViolationLevel());
                    }
                    super.checkViolationLevel(playerBreaking, true, data.fastbreakVL);
                } else {
                    data.fastbreakVL.substract(2);
                    super.checkViolationLevel(playerBreaking, false, data.fastbreakVL);
                }
            }
        }
        HackerDetector.INSTANCE.brokenBlocksList.clear();
    }

    @Override
    protected void log(EntityPlayer player, PlayerDataSamples data, ViolationLevelTracker vl, String extramsg) {
        if (player.isPotionActive(Potion.digSpeed)) {
            extramsg += " | haste level " + (player.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1);
        } else {
            extramsg += " | hasteAmplifierMW " + getMaxHasteAmplifierMW(player);
        }
        if (player.isPotionActive(Potion.digSlowdown)) {
            extramsg += " | mining fatigue level " + (player.getActivePotionEffect(Potion.digSlowdown).getAmplifier() + 1);
        }
        super.log(player, data, vl, extramsg);
    }

    public static ViolationLevelTracker newViolationTracker() {
        return new ViolationLevelTracker(40);
    }

    public static boolean isCheckActive() {
        return ScoreboardTracker.isInMwGame || ScoreboardTracker.isMWReplay;
    }

    /**
     * Returns the block strenght for the provided block and player
     * Harcoded to diamond pickaxe efficiency III with haste II
     */
    private static float getBlockStrengthMW(EntityPlayer player, BlockPos pos, Block block) {
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

    private static boolean canHarvestBlock(Block block) {
        if (block.getMaterial().isToolNotRequired()) {
            return true;
        }
        final IBlockState state = block.getDefaultState();
        /* Hardcoded for diamond pickaxe */
        final int toolLevel = 3;
        return toolLevel >= block.getHarvestLevel(state);
    }

    private static float getBreakSpeed(EntityPlayer player, Block block) {
        float f = Items.diamond_pickaxe.getStrVsBlock(null, block);
        if (f > 1.0F) {
            final int efficiencyModifier = 3;
            f += (float) (efficiencyModifier * efficiencyModifier + 1);
        }
        if (player.isPotionActive(Potion.digSpeed)) {
            f *= 1.0F + (float) (player.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
        } else {
            f *= 1.0F + getMaxHasteAmplifierMW(player) * 0.2F;
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

    private static int getMaxHasteAmplifierMW(EntityPlayer player) {
        final MWClass mwClass = ((EntityPlayerAccessor) player).getMWClass();
        if (mwClass == null) {
            return 3;
        }
        if (mwClass == MWClass.ZOMBIE) {
            if (ScoreboardTracker.isPrepPhase || ScoreboardTracker.isMWReplay) {
                return 3;
            } else {
                return 2;
            }
        } else if (mwClass == MWClass.HUNTER) {
            return 3;
        } else if (mwClass == MWClass.MOLEMAN) {
            return 2;
        }
        if (ScoreboardTracker.isPrepPhase || ScoreboardTracker.isMWReplay) {
            return 2;
        } else {
            return 0;
        }
    }

}
