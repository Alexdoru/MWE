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
                        checkPlayerAttack(attackerID, ((S19PacketEntityStatusAccessor) packet).getEntityId(), AttackType.ATTACK);
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
            } else if (animationType == 4 || animationType == 5) { // critical (4) / enchant particle (5)
                final long timeDiff = System.currentTimeMillis() - lastSwingTime;
                if (timeDiff < 2) {
                    if (lastPacketWasSwing) {
                        checkPlayerAttack(attackerID, packetAnimation.getEntityID(), AttackType.ATTACK);
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
                    checkPlayerAttack(attackerID, packetVelo.getEntityID(), AttackType.VELOCITY);
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
        // TODO ca register plus les attaques
        HackerDetector.addScheduledTask(() -> {
            final Entity attacker = mc.theWorld.getEntityByID(attackerID);
            final Entity target = mc.theWorld.getEntityByID(targetId);
            if (!(attacker instanceof EntityPlayer) || !(target instanceof EntityPlayer) || attacker == target) {
                return;
            }// TODO sur le death packet du coup le players n'est ptet plus la
            // discard attacks when the target is near the
            // entity render distance since the attacker might
            // not be loaded on my client
            final double xDiff = mc.thePlayer.posX - target.posX;
            final double zDiff = mc.thePlayer.posZ - target.posZ;
            if (xDiff < -56D || xDiff > 56D || zDiff < -56D || zDiff > 56D) return;
            if (attacker.getDistanceSqToEntity(target) > 64d) {
                // TODO changer le range check en fonction de la speed du mec et ma speed,
                //  si je prend du kb je vais voir que je me fait taper de 18 000 blocks?
                //  >>OU<< regarder si la position de la target dans les 4-5 derniers ticks (equivalent au potentiel ping de l'attaquer)
                //  se trouve in range de l'attacker, devant lui, mois de 5 blocks
                return;
            }
            if (ScoreboardTracker.isInMwGame && ((EntityPlayerAccessor) attacker).getPlayerTeamColor() != '\0' && ((EntityPlayerAccessor) attacker).getPlayerTeamColor() == ((EntityPlayerAccessor) target).getPlayerTeamColor()) {
                return;
            }
            switch (attackType) {
                case VELOCITY:  // velocity packet
                    if (mc.thePlayer == target) {
                        onPlayerAttack((EntityPlayer) attacker, mc.thePlayer, attackType);
                    }
                    break;
                case ATTACK:  // swing and hurt packet received consecutively
                    onPlayerAttack((EntityPlayer) attacker, (EntityPlayer) target, attackType);
                    break;
                case HURT:  // target hurt
                    // when an ability does damage to multiple players, this can fire multiple times
                    // on different players for the same attacker
                    if (((EntityPlayer) attacker).swingProgressInt == -1 && ((EntityPlayer) target).hurtTime == 10) {// FIXME swingProgressInt == -1 isn't always true
                        onPlayerAttack((EntityPlayer) attacker, (EntityPlayer) target, attackType);
                    }
                    break;
                case CRITICAL:  // target has crit particles
                    if (((EntityPlayer) attacker).swingProgressInt == -1 && !attacker.onGround && attacker.ridingEntity == null) {
                        onPlayerAttack((EntityPlayer) attacker, (EntityPlayer) target, attackType);
                    }
                    break;
                case SHARPNESS:  // target has sharp particles
                    if (((EntityPlayer) attacker).swingProgressInt == -1) {
                        final ItemStack heldItem = ((EntityPlayer) attacker).getHeldItem();
                        if (heldItem != null) {
                            final Item item = heldItem.getItem();
                            if ((item instanceof ItemSword || item instanceof ItemTool) && heldItem.isItemEnchanted()) {
                                onPlayerAttack((EntityPlayer) attacker, (EntityPlayer) target, attackType);
                            }
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

        ATTACK,
        CRITICAL,
        SHARPNESS,
        HURT,
        VELOCITY

    }

}
