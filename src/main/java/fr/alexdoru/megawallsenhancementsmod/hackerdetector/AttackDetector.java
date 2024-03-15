package fr.alexdoru.megawallsenhancementsmod.hackerdetector;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.asm.accessors.S19PacketEntityStatusAccessor;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.AttackInfo;
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
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.Vec3;

/**
 * Tries to estimate if a player attacked another player by looking at the packets received,
 * the server sends them from here :
 * {@link net.minecraft.entity.player.EntityPlayer#attackTargetEntityWithCurrentItem)}
 * {@link net.minecraft.entity.EntityLivingBase#attackEntityFrom}
 */
public class AttackDetector {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static boolean lastPacketWasSwing;
    private static long lastSwingTime;
    private static int attackerID;
    private static boolean lastPacketWasHurt;
    private static long lastHurtTime;
    private static int lastHurtID;
    private static boolean consecutiveSwingHurt;

    // Careful, this code isn't called from the main thread
    public static void lookForAttacks(Packet<?> packet) {
        if (packet instanceof S0BPacketAnimation) {
            final S0BPacketAnimation packetAnimation = (S0BPacketAnimation) packet;
            final int animationType = packetAnimation.getAnimationType();
            if (animationType == 0) { // Swing packet
                lastPacketWasHurt = false;
                consecutiveSwingHurt = false;
                lastPacketWasSwing = true;
                lastSwingTime = System.currentTimeMillis();
                attackerID = packetAnimation.getEntityID();
                onEntitySwing(attackerID);
                return;
            } else if (animationType == 4 || animationType == 5) { // critical (4) / enchant particle (5)
                if (System.currentTimeMillis() - lastSwingTime < 2) {
                    if (lastPacketWasSwing) {
                        checkPlayerAttack(attackerID, packetAnimation.getEntityID(), animationType == 4 ? AttackType.DIRECT_CRITICAL : AttackType.DIRECT_SHARPNESS, null);
                    } else {
                        checkPlayerAttack(attackerID, packetAnimation.getEntityID(), animationType == 4 ? AttackType.CRITICAL : AttackType.SHARPNESS, null);
                    }
                }
            }
        } else if (packet instanceof S12PacketEntityVelocity) {
            if (System.currentTimeMillis() - lastSwingTime < 2) {
                final S12PacketEntityVelocity packetVelo = (S12PacketEntityVelocity) packet;
                if (packetVelo.getMotionX() != 0 || packetVelo.getMotionY() != 0 || packetVelo.getMotionZ() != 0) {
                    if (lastPacketWasSwing) {
                        checkPlayerAttack(attackerID, packetVelo.getEntityID(), AttackType.DIRECT_VELOCITY, null);
                    } else {
                        checkPlayerAttack(attackerID, packetVelo.getEntityID(), AttackType.VELOCITY, null);
                    }
                }
            }
        } else if (packet instanceof S19PacketEntityStatusAccessor) {
            if (((S19PacketEntityStatus) packet).getOpCode() == 2) { // Entity gets hurt (2)
                if (lastPacketWasSwing) consecutiveSwingHurt = true;
                lastPacketWasSwing = false;
                lastPacketWasHurt = true;
                lastHurtTime = System.currentTimeMillis();
                lastHurtID = ((S19PacketEntityStatusAccessor) packet).getEntityId();
                return;
            }
        } else if (packet instanceof S29PacketSoundEffect) {
            if (lastPacketWasSwing && System.currentTimeMillis() - lastSwingTime < 2) {
                if ("mob.guardian.elder.hit".equals(((S29PacketSoundEffect) packet).getSoundName())) {
                    // TODO dreadlord hit
                } else if ("note.harp".equals(((S29PacketSoundEffect) packet).getSoundName())) {
                    // TODO shaman hit
                }
            } else if (lastPacketWasHurt && System.currentTimeMillis() - lastSwingTime < 2 && System.currentTimeMillis() - lastHurtTime < 2) {
                if ("game.player.hurt".equals(((S29PacketSoundEffect) packet).getSoundName())) {
                    final S29PacketSoundEffect soundPacket = (S29PacketSoundEffect) packet;
                    checkPlayerAttack(
                            attackerID,
                            lastHurtID,
                            consecutiveSwingHurt ? AttackType.DIRECTHURTSOUND : AttackType.HURTSOUND,
                            new Vec3(soundPacket.getX(), soundPacket.getY(), soundPacket.getZ()));
                } else if ("game.player.die".equals(((S29PacketSoundEffect) packet).getSoundName())) {
                    final S29PacketSoundEffect soundPacket = (S29PacketSoundEffect) packet;
                    checkPlayerAttack(
                            attackerID,
                            lastHurtID,
                            consecutiveSwingHurt ? AttackType.DIRECTDEATHSOUND : AttackType.DEATHSOUND,
                            new Vec3(soundPacket.getX(), soundPacket.getY(), soundPacket.getZ()));
                }
            }
        }
        lastPacketWasHurt = false;
        lastPacketWasSwing = false;
        consecutiveSwingHurt = false;
    }

    private static void onEntitySwing(int entityID) {
        HackerDetector.addScheduledTask(() -> {
            final Entity attacker = mc.theWorld.getEntityByID(entityID);
            if (attacker instanceof EntityPlayerAccessor) {
                ((EntityPlayerAccessor) attacker).getPlayerDataSamples().hasSwung = true;
            }
        });
    }

    private static void checkPlayerAttack(int attackerEntityId, int targetEntityId, AttackType attackType, Vec3 soundPos) {
        HackerDetector.addScheduledTask(() -> {
            final Entity attacker = mc.theWorld.getEntityByID(attackerEntityId);
            final Entity target = mc.theWorld.getEntityByID(targetEntityId);
            if (!(attacker instanceof EntityPlayer) || !(target instanceof EntityPlayer) || attacker == target) {
                return;
            }
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
                case HURTSOUND:
                case DEATHSOUND:
                case DIRECTHURTSOUND:
                case DIRECTDEATHSOUND:
                    if (Math.abs(soundPos.xCoord - target.posX) < 1d && Math.abs(soundPos.yCoord - target.posY) < 1d && Math.abs(soundPos.zCoord - target.posZ) < 1d) {
                        onPlayerAttack((EntityPlayer) attacker, (EntityPlayer) target, attackType);
                    }
                    break;
                case DIRECT_VELOCITY:
                case VELOCITY:
                    if (mc.thePlayer == target) {
                        onPlayerAttack((EntityPlayer) attacker, mc.thePlayer, attackType);
                    }
                    break;
                case CRITICAL:
                case DIRECT_CRITICAL:
                    if (attacker.ridingEntity == null) {
                        onPlayerAttack((EntityPlayer) attacker, (EntityPlayer) target, attackType);
                    }
                    break;
                case DIRECT_SHARPNESS:
                case SHARPNESS:
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
        final PlayerDataSamples data = ((EntityPlayerAccessor) attacker).getPlayerDataSamples();
        if (data.attackInfo == null) {
            data.attackInfo = new AttackInfo(target, attackType);
        } else if (data.attackInfo.target != target) {
            data.attackInfo.multiTarget = true;
        }
    }

    public enum AttackType {

        CRITICAL,
        DEATHSOUND,
        DIRECTDEATHSOUND,
        DIRECTHURTSOUND,
        DIRECT_CRITICAL,
        DIRECT_SHARPNESS,
        DIRECT_VELOCITY,
        //DREADLORDHIT,
        HURTSOUND,
        //SHAMANHIT,
        SHARPNESS,
        VELOCITY

    }

}
