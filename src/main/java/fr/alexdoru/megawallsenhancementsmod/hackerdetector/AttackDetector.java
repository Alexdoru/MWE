package fr.alexdoru.megawallsenhancementsmod.hackerdetector;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.asm.accessors.S19PacketEntityStatusAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S19PacketEntityStatus;

public class AttackDetector {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static boolean lastPacketWasSwing;
    private static long lastSwingTime;
    private static int attackerID;

    // Careful, this code isn't called from the main thread
    public static void lookForAttacks(Packet<?> packet) {
        if (packet instanceof S19PacketEntityStatusAccessor) {
            final byte opCode = ((S19PacketEntityStatus) packet).getOpCode();
            if (opCode == 2 || opCode == 3) { // Entity gets hurt (2) or dies (3)
                final long timeDiff = System.currentTimeMillis() - lastSwingTime;
                if (timeDiff < 2) {
                    if (lastPacketWasSwing) {
                        checkPlayerAttack(attackerID, ((S19PacketEntityStatusAccessor) packet).getEntityId(), AttackType.DIRECT_HURT);
                    } else {
                        checkPlayerAttack(attackerID, ((S19PacketEntityStatusAccessor) packet).getEntityId(), AttackType.HURT);
                    }
                }
            }
        } else if (packet instanceof S0BPacketAnimation) {
            final S0BPacketAnimation packetAnimation = (S0BPacketAnimation) packet;
            final int animationType = packetAnimation.getAnimationType();
            if (animationType == 0) { // Swing packet
                lastPacketWasSwing = true;
                lastSwingTime = System.currentTimeMillis();
                attackerID = packetAnimation.getEntityID();
                onEntitySwing(attackerID);
                return;
            } else if (animationType == 1) { // hurt animation
                final long timeDiff = System.currentTimeMillis() - lastSwingTime;
                if (timeDiff < 2) {
                    if (lastPacketWasSwing) {
                        checkPlayerAttack(attackerID, packetAnimation.getEntityID(), AttackType.DIRECT_HURT);
                    } else {
                        checkPlayerAttack(attackerID, packetAnimation.getEntityID(), AttackType.HURT);
                    }
                }
            } else if (animationType == 4 || animationType == 5) { // critical (4) / enchant particle (5)
                final long timeDiff = System.currentTimeMillis() - lastSwingTime;
                if (timeDiff < 2) {
                    if (lastPacketWasSwing) {
                        checkPlayerAttack(attackerID, packetAnimation.getEntityID(), animationType == 4 ? AttackType.DIRECT_CRITICAL : AttackType.DIRECT_SHARPNESS);
                    } else {
                        checkPlayerAttack(attackerID, packetAnimation.getEntityID(), animationType == 4 ? AttackType.CRITICAL : AttackType.SHARPNESS);
                    }
                }
            }
        } else if (packet instanceof S12PacketEntityVelocity) {
            final long timeDiff = System.currentTimeMillis() - lastSwingTime;
            if (timeDiff < 2) {
                final S12PacketEntityVelocity packetVelo = (S12PacketEntityVelocity) packet;
                if (packetVelo.getMotionX() != 0 || packetVelo.getMotionY() != 0 || packetVelo.getMotionZ() != 0) {
                    if (lastPacketWasSwing) {
                        checkPlayerAttack(attackerID, packetVelo.getEntityID(), AttackType.DIRECT_VELOCITY);
                    } else {
                        checkPlayerAttack(attackerID, packetVelo.getEntityID(), AttackType.VELOCITY);
                    }
                }
            }
        }
        lastPacketWasSwing = false;
    }

    private static void onEntitySwing(int attackerID) {
        HackerDetector.addScheduledTask(() -> {
            final Entity attacker = mc.theWorld.getEntityByID(attackerID);
            if (attacker instanceof EntityPlayerAccessor) {
                final PlayerDataSamples data = ((EntityPlayerAccessor) attacker).getPlayerDataSamples();
                data.hasSwung = true;
                data.lastSwingTime = -1;
            }
        });
    }

    private static void checkPlayerAttack(int attackerID, int targetId, AttackType attackType) {
        HackerDetector.addScheduledTask(() -> {
            final Entity attacker = mc.theWorld.getEntityByID(attackerID);
            final Entity target = mc.theWorld.getEntityByID(targetId);
            if (!(attacker instanceof EntityPlayer) || !(target instanceof EntityPlayer) || attacker == target) {
                return;
            }// TODO sur le death packet du coup le players n'est ptet plus la
            final double xDiff = Math.abs(mc.thePlayer.posX - target.posX);
            final double zDiff = Math.abs(mc.thePlayer.posZ - target.posZ);
            if (xDiff > 56D || zDiff > 56D) {
                // discard attacks when the target is near the
                // entity render distance since the attacker might
                // not be loaded on my client
                return;
            }
            if (attacker.getDistanceSqToEntity(target) > 64d) {
                return;
            }
            if ((ScoreboardTracker.isInMwGame || ScoreboardTracker.isMWReplay)
                    && ((EntityPlayerAccessor) attacker).getPlayerTeamColor() != '\0'
                    && ((EntityPlayerAccessor) attacker).getPlayerTeamColor() == ((EntityPlayerAccessor) target).getPlayerTeamColor()) {
                // discard attack if both players are on the same team
                return;
            }
            switch (attackType) {
                case DIRECT_VELOCITY:
                case VELOCITY:  // velocity packet
                    if (mc.thePlayer == target) {
                        onPlayerAttack((EntityPlayer) attacker, mc.thePlayer, attackType);
                    }
                    break;
                case DIRECT_CRITICAL:
                case DIRECT_HURT: // swing and hurt packet received consecutively
                case DIRECT_SHARPNESS:
                case HURT:  // target hurt
                    // when an ability does damage to multiple players, this can fire multiple times
                    // on different players for the same attacker
                    onPlayerAttack((EntityPlayer) attacker, (EntityPlayer) target, attackType);
                    break;
                case CRITICAL:  // target has crit particles
                    if (attacker.ridingEntity == null) {
                        onPlayerAttack((EntityPlayer) attacker, (EntityPlayer) target, attackType);
                    }
                    break;
                case SHARPNESS:  // target has sharp particles
                    final ItemStack heldItem = ((EntityPlayer) attacker).getHeldItem();
                    if (heldItem != null) {
                        final Item item = heldItem.getItem();
                        if ((item instanceof ItemSword || item instanceof ItemTool) && heldItem.isItemEnchanted()) {
                            onPlayerAttack((EntityPlayer) attacker, (EntityPlayer) target, attackType);
                        }
                    }
                    break;
            }
        });
    }

    private static void onPlayerAttack(EntityPlayer attacker, EntityPlayer target, AttackType attackType) {
        final PlayerDataSamples dataAttacker = ((EntityPlayerAccessor) attacker).getPlayerDataSamples();
        if (dataAttacker.hasAttackedMultiTarget) {
            return;
        }
        if (dataAttacker.targetedPlayer != null && dataAttacker.targetedPlayer != target) {
            dataAttacker.hasAttackedMultiTarget = true;
            dataAttacker.hasAttacked = false;
            dataAttacker.targetedPlayer = null;
            return;
        }
        dataAttacker.hasAttacked = true;
        dataAttacker.targetedPlayer = target;
        if (ConfigHandler.debugLogging) {
            HackerDetector.log(attacker.getName() + " attacked " + target.getName() + " [" + attackType.name() + "]");
        }
    }

    public enum AttackType {

        CRITICAL,
        DIRECT_CRITICAL,
        DIRECT_HURT,
        DIRECT_SHARPNESS,
        DIRECT_VELOCITY,
        HURT,
        SHARPNESS,
        VELOCITY

    }

}
