package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
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

    protected static void flag(EntityPlayer player, String cheat, String cheatDescription) {
        logger.warn(player.getName() + " flags " + cheat);// TODO remove debug
        ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RESET
                + NameUtil.getFormattedNameWithoutIcons(player.getName())
                + EnumChatFormatting.YELLOW + " flags "
                + EnumChatFormatting.RED + cheat)
                .setChatStyle(new ChatStyle()
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
                + " | speedXZ (m/s) " + String.format("%.4f", data.dXdZVector2D.lengthVector() * 20D)
                + " | posX " + String.format("%.4f", player.posX)
                + " | lastTickPosX " + String.format("%.4f", player.lastTickPosX)
                + " | posY " + String.format("%.4f", player.posY)
                + " | lastTickPosY " + String.format("%.4f", player.lastTickPosY)
                + " | posZ " + String.format("%.4f", player.posZ)
                + " | lastTickPosZ " + String.format("%.4f", player.lastTickPosZ)
                + " | rotationPitch " + String.format("%.4f", player.rotationPitch)
                + " | rotationYaw " + String.format("%.4f", player.rotationYaw)
                + " | sprintTime " + data.sprintTime
                + " | lastHurtTime " + data.lastHurtTime
                + " | ticksExisted " + player.ticksExisted
                + " | isRidingEntity " + player.isRiding()
        );
    }

    protected static void debug(String msg) {
        ChatUtil.debug(msg);
    }

}
