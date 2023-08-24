package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.asm.hooks.GuiScreenHook;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatHandler;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.data.WDR;
import fr.alexdoru.megawallsenhancementsmod.data.WdrData;
import fr.alexdoru.megawallsenhancementsmod.features.SquadHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector3D;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportQueue;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

/**
 * Abstract class to hold util methods for the checks
 */
public abstract class AbstractCheck implements ICheck {

    private static final HashSet<String> flagMessages = new HashSet<>();

    @Override
    public final void checkViolationLevel(EntityPlayer player, boolean failedCheck, ViolationLevelTracker... trackers) {
        for (final ViolationLevelTracker tracker : trackers) {
            if (tracker.isFlagging(failedCheck)) {
                this.printFlagMessage(player);
                this.addToReportList(player);
                this.sendReport(player);
            }
        }
    }

    private void printFlagMessage(EntityPlayer player) {
        if (ConfigHandler.debugLogging) {
            HackerDetector.log(player.getName() + " flags " + this.getCheatName());
        }
        if (!ConfigHandler.showFlagMessages) {
            return;
        }
        final String msg = ChatUtil.getTagNoCheaters() + EnumChatFormatting.RESET
                + NameUtil.getFormattedNameWithoutIcons(player.getName())
                + EnumChatFormatting.YELLOW + " flags "
                + EnumChatFormatting.RED + this.getCheatName();
        if (ConfigHandler.oneFlagMessagePerGame) {
            if (flagMessages.contains(msg)) {
                return;
            }
            flagMessages.add(msg);
        }
        final IChatComponent imsg = new ChatComponentText(msg)
                .setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, GuiScreenHook.COPY_TO_CLIPBOARD_COMMAND + EnumChatFormatting.getTextWithoutFormattingCodes(msg)))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(this.getCheatDescription()))));
        if (!(ScoreboardTracker.isInMwGame && ConfigHandler.autoreportFlaggedPlayers)) {
            imsg.appendSibling(ChatUtil.getReportButton(player.getName(), "cheating " + this.getCheatName().toLowerCase(), ClickEvent.Action.RUN_COMMAND));
        }
        if (!ConfigHandler.addToReportList) {
            imsg.appendSibling(ChatUtil.getWDRButton(player.getName(), this.getCheatName().toLowerCase(), ClickEvent.Action.RUN_COMMAND));
        }
        if (ConfigHandler.compactFlagMessages) {
            ChatHandler.deleteMessageFromChat(imsg);
        }
        ChatUtil.addChatMessage(imsg);
    }

    private void addToReportList(EntityPlayer player) {
        if (!ScoreboardTracker.isReplayMode && ConfigHandler.addToReportList && SquadHandler.getSquad().get(player.getName()) == null) {
            final String cheat = this.getCheatName().toLowerCase() + "[H]";
            final UUID uuid = player.getUniqueID();
            final boolean isNicked = uuid.version() != 4;
            final String uuidStr = isNicked ? player.getName() : uuid.toString().replace("-", "");
            final WDR wdr = WdrData.getWdr(uuidStr);
            if (wdr == null) {
                final long time = (new Date()).getTime();
                final ArrayList<String> hacks = new ArrayList<>();
                hacks.add(cheat);
                if (isNicked) {
                    hacks.add(WDR.NICK);
                }
                WdrData.put(uuidStr, new WDR(time, time, hacks));
            } else {
                if (isNicked && !wdr.hacks.contains(WDR.NICK)) {
                    wdr.hacks.add(WDR.NICK);
                }
                if (!wdr.hacks.contains(cheat)) {
                    wdr.hacks.add(cheat);
                }
                wdr.timestamp = (new Date()).getTime();
                wdr.hacks.trimToSize();
            }
            NameUtil.updateMWPlayerDataAndEntityData(player, false);
        }
    }

    private void sendReport(EntityPlayer player) {
        if (ScoreboardTracker.isInMwGame && ConfigHandler.autoreportFlaggedPlayers && SquadHandler.getSquad().get(player.getName()) == null) {
            ReportQueue.INSTANCE.addReportFromHackerDetector(player.getName(), this.getCheatName().toLowerCase());
        }
    }

    protected void fail(EntityPlayer player, String cheat) {
        ChatUtil.debug(NameUtil.getFormattedNameWithoutIcons(player.getName())
                + EnumChatFormatting.GRAY + " failed "
                + EnumChatFormatting.RED + cheat
                + EnumChatFormatting.GRAY + " check ");
    }

    protected void log(EntityPlayer player, String cheat, @Nonnull ViolationLevelTracker vl, PlayerDataSamples data, String extramsg) {
        HackerDetector.log(player.getName() + " failed " + cheat + " check "
                + extramsg
                + " | vl " + vl.getViolationLevel()
                + " | onGround " + player.onGround
                + " | speedXZ (m/s) " + String.format("%.4f", data.getSpeedXZ())
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
    protected double getBaseSprintingSpeed(EntityPlayer player) {
        int speedAmplifier = 0;
        if (player.isPotionActive(Potion.moveSpeed)) {
            final PotionEffect activePotionEffect = player.getActivePotionEffect(Potion.moveSpeed);
            speedAmplifier = activePotionEffect.getAmplifier() + 1;
        }
        return getBaseSprintingSpeed(speedAmplifier);
    }

    protected double getBaseSprintingSpeed(int speedAmplifier) {
        return 0.2806d * (1.0d + 0.2d * speedAmplifier);
    }

    /**
     * Returns the amount of ticks for a certain player to break a certain block
     * The enchantements of the tool must be readable from the client for this to be accurate
     * This accounts for the 5 ticks cooldown there is after breaking a bloc
     * Returns -1 if the block isn't harvestable
     */
    protected int getTimeToHarvestBlock(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
        return getTimeToHarvestBlock(ForgeHooks.blockStrength(state, player, world, pos));
    }

    /**
     * Returns the amount of ticks for a certain player to break a certain block
     * The enchantements of the tool must be readable from the client for this to be accurate
     * This accounts for the 5 ticks cooldown there is after breaking a bloc
     * Returns -1 if the block isn't harvestable
     */
    protected int getTimeToHarvestBlock(float blockStrength) {
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
            /* The player somehow has their head inside the block */
        } else if (distSq < 0.25) {
            return true;
        }
        /* Checks if the player's look vect is within a cone that starts from the player's eyes and
         * whose base is a circle centered on the block's center and with 0.5*sqrt(3) radius */
        final double angleWithVector = Vector3D.getPlayersLookVec(player).getAngleWithVector(eyesToBlockCenter);
        final double maxAngle = Math.toDegrees(Math.atan(0.5D * Math.sqrt(3 / distSq)));
        return angleWithVector < maxAngle;
    }

    public static void clearFlagMessages() {
        flagMessages.clear();
    }

}
