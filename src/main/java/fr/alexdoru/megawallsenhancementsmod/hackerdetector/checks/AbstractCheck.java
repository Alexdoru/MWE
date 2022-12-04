package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.asm.hooks.GuiScreenHook;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.SampleList;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector3D;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

/**
 * Abstract class to hold util methods for the checks
 */
public abstract class AbstractCheck implements ICheck {

    protected static final Logger logger = LogManager.getLogger("HackerDetector");

    @Override
    public final void checkViolationLevel(EntityPlayer player, boolean failedCheck, ViolationLevelTracker... trackers) {
        for (final ViolationLevelTracker tracker : trackers) {
            if (tracker.isFlagging(failedCheck)) {
                flag(player, this.getCheatName(), this.getCheatDescription());
            }
        }
    }

    protected static void flag(EntityPlayer player, String cheat, String cheatDescription) {
        logger.warn(player.getName() + " flags " + cheat);// TODO remove debug
        final String msg = ChatUtil.getTagNoCheaters() + EnumChatFormatting.RESET
                + NameUtil.getFormattedNameWithoutIcons(player.getName())
                + EnumChatFormatting.YELLOW + " flags "
                + EnumChatFormatting.RED + cheat;
        ChatUtil.addChatMessage(new ChatComponentText(msg)
                .setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, GuiScreenHook.COPY_TO_CLIPBOARD_COMMAND + EnumChatFormatting.getTextWithoutFormattingCodes(msg)))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(cheatDescription))))
                .appendSibling(ChatUtil.getReportButtons(player.getName(),
                        "cheating " + cheat.toLowerCase(),
                        cheat.toLowerCase(),
                        ClickEvent.Action.RUN_COMMAND,
                        ClickEvent.Action.RUN_COMMAND)
                )
        );
    }

    protected static void fail(EntityPlayer player, String cheat) {
        ChatUtil.debug(NameUtil.getFormattedNameWithoutIcons(player.getName())
                + EnumChatFormatting.GRAY + " failed "
                + EnumChatFormatting.RED + cheat
                + EnumChatFormatting.GRAY + " check ");
    }

    protected static void log(EntityPlayer player, String cheat, @Nonnull ViolationLevelTracker vl, PlayerDataSamples data, String extramsg) {
        logger.info(player.getName() + " failed " + cheat + " check "
                + extramsg
                + " | vl " + vl.getViolationLevel()
                + " | onGround " + player.onGround
                + " | speedXZ (m/s) " + String.format("%.4f", data.dXdZVector2D.norm() * 20D)
                + " | posX " + String.format("%.4f", player.posX)
                + " | lastTickPosX " + String.format("%.4f", player.lastTickPosX)
                + " | posY " + String.format("%.4f", player.posY)
                + " | lastTickPosY " + String.format("%.4f", player.lastTickPosY)
                + " | posZ " + String.format("%.4f", player.posZ)
                + " | lastTickPosZ " + String.format("%.4f", player.lastTickPosZ)
                + " | rotationPitch " + String.format("%.4f", player.rotationPitch)
                + " | rotationYawHead " + String.format("%.4f", player.rotationYawHead)
                + " | sprintTime " + data.sprintTime
                + " | lastHurtTime " + data.lastHurtTime
                + " | lastSwingTime " + data.lastSwingTime
                + " | ticksExisted " + player.ticksExisted
                + " | isRidingEntity " + player.isRiding()
        );
    }

    protected static void debug(String msg) {
        ChatUtil.debug(msg);
    }

    /**
     * Returns the base speed for a player sprinting without jumping.
     * Without speed, jumping while sprinting is ~29% faster than normal sprinting
     * That makes normal sprinting ~21% slower than sprint while jumping
     * Result is in meters per tick
     */
    protected static double getBaseSprintingSpeed(EntityPlayer player) {
        int speedAmplifier = 0;
        if (player.isPotionActive(Potion.moveSpeed)) {
            final PotionEffect activePotionEffect = player.getActivePotionEffect(Potion.moveSpeed);
            speedAmplifier = activePotionEffect.getAmplifier() + 1;
        }
        return getBaseSprintingSpeed(speedAmplifier);
    }

    protected static double getBaseSprintingSpeed(int speedAmplifier) {
        return 0.2806d * (1.0d + 0.2d * speedAmplifier);
    }

    /**
     * Returns the amount of ticks for a certain player to break a certain block
     * The enchantements of the tool must be readable from the client for this to be accurate
     * This accounts for the 5 ticks cooldown there is after breaking a bloc
     * Returns -1 if the block isn't harvestable
     */
    protected static int getTimeToHarvestBlock(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
        return getTimeToHarvestBlock(ForgeHooks.blockStrength(state, player, world, pos));
    }

    /**
     * Returns the amount of ticks for a certain player to break a certain block
     * The enchantements of the tool must be readable from the client for this to be accurate
     * This accounts for the 5 ticks cooldown there is after breaking a bloc
     * Returns -1 if the block isn't harvestable
     */
    protected static int getTimeToHarvestBlock(float blockStrength) {
        if (blockStrength <= 0) {
            return -1;
        }
        int i = 1;
        if (blockStrength < 1.0F) {
            float breakProgression = 0F;
            while (breakProgression < 1.0f) {
                i++;
                breakProgression += blockStrength;
            }
        }
        return i == 1 ? i : i + 5;
    }

    protected float average(SampleList<Float> list) {
        float sum = 0;
        for (final Float f : list) {
            sum += f;
        }
        return sum / list.size();
    }

    protected boolean isPlayerLookingAtBlock(EntityPlayer player, BlockPos pos) {
        final Vector3D eyesToBlockCenter = new Vector3D(
                pos.getX() + 0.5D - player.posX,
                pos.getY() + 0.5D - (player.posY + player.getEyeHeight()),
                pos.getZ() + 0.5D - player.posZ
        );
        /* (0.5*sqrt(3) + 4.5)^2 | Squared distance from center of the block to the corner + player's break reach */
        final double distSq = eyesToBlockCenter.normSquared();
        if (distSq > 28.79422863D) {
            return false;
            /* The player somehow has its head inside the block */
        } else if (distSq < 0.25) {
            return true;
        }
        /* Checks if the player's look vect is within a cone that starts from the player's eyes and
         * whose base is a circle centered on the block's center and with 0.5*sqrt(3) radius */
        final double angleWithVector = Vector3D.getPlayersLookVec(player).getAngleWithVector(eyesToBlockCenter);
        final double maxAngle = Math.toDegrees(Math.atan(0.5D * Math.sqrt(3 / distSq)));
        return angleWithVector < maxAngle;
    }

}
