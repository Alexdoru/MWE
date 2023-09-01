package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.S19PacketEntityStatusAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;

@SuppressWarnings("unused")
public class NetworkManagerHook_PacketListener {

    private static final Logger logger = LogManager.getLogger("PacketListener");
    private static boolean lastPacketWasSwing;
    private static int attackerID;

    // Careful, this code isn't called from the main thread
    // Only listens packets that throw a ThreadQuickExitException
    public static void listen(Packet<?> packet) {
        if (!ConfigHandler.hackerDetector) return;
        try { // We need a try catch block to prevent any exception from being throwned, it would discard the packet
            lookForAttacks(packet);
        } catch (Throwable ignored) {}
    }

    private static void lookForAttacks(Packet<?> packet) {
        if (packet instanceof S19PacketEntityStatusAccessor && ((S19PacketEntityStatus) packet).getOpCode() == 2) { // Entity gets hurt
            if (lastPacketWasSwing) {
                HackerDetector.checkPlayerAttack(attackerID, ((S19PacketEntityStatusAccessor) packet).getEntityId(), 1);
                //log("Attacker : " + attackerID + " target : " + ((S19PacketEntityStatusAccessor) packet).getEntityId() + " [attack]" + " | time diff " + (System.currentTimeMillis() - lastSwingTime) + "ms");
            } else {
                HackerDetector.checkPlayerAttack(attackerID, ((S19PacketEntityStatusAccessor) packet).getEntityId(), 2);
                //log("Attacker : " + attackerID + " target : " + ((S19PacketEntityStatusAccessor) packet).getEntityId() + " [hurt]" + " | time diff " + (System.currentTimeMillis() - lastSwingTime) + "ms");
            }
        } else if (packet instanceof S0BPacketAnimation) {
            if (((S0BPacketAnimation) packet).getAnimationType() == 0) { // Swing packet
                lastPacketWasSwing = true;
                attackerID = ((S0BPacketAnimation) packet).getEntityID();
                HackerDetector.onEntitySwing(attackerID);
                return;
            }
            if (((S0BPacketAnimation) packet).getAnimationType() == 4 || ((S0BPacketAnimation) packet).getAnimationType() == 5) { // crit particle packet
                if (lastPacketWasSwing) {
                    HackerDetector.checkPlayerAttack(attackerID, ((S0BPacketAnimation) packet).getEntityID(), 1);
                } else {
                    HackerDetector.checkPlayerAttack(attackerID, ((S0BPacketAnimation) packet).getEntityID(), ((S0BPacketAnimation) packet).getAnimationType());
                }
                //log("Attacker : " + attackerID + " target : " + ((S0BPacketAnimation) packet).getEntityID() + " [crit/sharp]" + " | time diff " + (System.currentTimeMillis() - lastSwingTime) + "ms");
            }
        } else if (packet instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity packetVelo = (S12PacketEntityVelocity) packet;
            if (packetVelo.getMotionX() != 0 || packetVelo.getMotionY() != 0 || packetVelo.getMotionZ() != 0) {
                HackerDetector.checkPlayerAttack(attackerID, packetVelo.getEntityID(), 0);
            }
        }
        lastPacketWasSwing = false;
    }

    private static void logPacket(Packet<?> packet) {
        if (packet instanceof S14PacketEntity) {
            final S14PacketEntity packetEntity = (S14PacketEntity) packet;
            log(
                    stripClassName(packet.getClass().getName()) +
                            " | serverPosDiffXYZ : " + formatXYZ(packetEntity.func_149062_c() / 32.0D, packetEntity.func_149061_d() / 32.0D, packetEntity.func_149064_e() / 32.0D) +
                            " | setRotation " + packetEntity.func_149060_h() +
                            " | yaw : " + (float) (packetEntity.func_149066_f() * 360) / 256.0F +
                            " | pitch " + (float) (packetEntity.func_149063_g() * 360) / 256.0F +
                            " | onGround " + packetEntity.getOnGround()
            );
        } else if (packet instanceof S19PacketEntityHeadLook) {
            log(
                    stripClassName(packet.getClass().getName()) +
                            " | yaw : " + ((float) (((S19PacketEntityHeadLook) packet).getYaw() * 360) / 256.0F)
            );
        } else if (packet instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity packetVelo = (S12PacketEntityVelocity) packet;
            log(
                    stripClassName(packet.getClass().getName()) +
                            " | entityId : " + packetVelo.getEntityID() +
                            " | motionXYZ : " + formatXYZ((double) packetVelo.getMotionX() / 8000.0D, (double) packetVelo.getMotionY() / 8000.0D, (double) packetVelo.getMotionZ() / 8000.0D)
            );
        } else if (packet instanceof S20PacketEntityProperties) {
            final StringBuilder str = new StringBuilder();
            for (final S20PacketEntityProperties.Snapshot snapshot : ((S20PacketEntityProperties) packet).func_149441_d()) {
                str.append(snapshot.func_151409_a());
            }
            log(
                    stripClassName(packet.getClass().getName()) +
                            " | entityId : " + ((S20PacketEntityProperties) packet).getEntityId() +
                            " | attribues updated " + str);
        } else if (packet instanceof S2APacketParticles) {
            final S2APacketParticles packetParticles = (S2APacketParticles) packet;
            log(
                    stripClassName(packet.getClass().getName()) +
                            " | particleType : " + packetParticles.getParticleType().name() +
                            " | particleCount : " + packetParticles.getParticleCount() +
                            " | posXYZ : " + formatXYZ(packetParticles.getXCoordinate(), packetParticles.getYCoordinate(), packetParticles.getZCoordinate())
            );
        } else if (packet instanceof S0BPacketAnimation) {
            log(
                    stripClassName(packet.getClass().getName()) +
                            " | entityId : " + ((S0BPacketAnimation) packet).getEntityID() +
                            " | type : " + ((S0BPacketAnimation) packet).getAnimationType()
            );
        } else if (packet instanceof S19PacketEntityStatus) {
            log(
                    stripClassName(packet.getClass().getName()) +
                            " | entityId : " + ((S19PacketEntityStatusAccessor) packet).getEntityId() +
                            " | logicOpcode : " + ((S19PacketEntityStatus) packet).getOpCode()
            );
        } else if (packet instanceof S04PacketEntityEquipment) {
            log(
                    stripClassName(packet.getClass().getName()) +
                            " | entityId : " + ((S04PacketEntityEquipment) packet).getEntityID() +
                            " | equipmentSlot : " + ((S04PacketEntityEquipment) packet).getEquipmentSlot() +
                            (((S04PacketEntityEquipment) packet).getItemStack().getItem() == null ? "" :
                                    " | itemStack : " + ((S04PacketEntityEquipment) packet).getItemStack().getItem().getUnlocalizedName())
            );
        } else if (packet instanceof S29PacketSoundEffect) {
            log(
                    stripClassName(packet.getClass().getName()) +
                            " | soundName : " + ((S29PacketSoundEffect) packet).getSoundName()
            );
        } else if (packet instanceof S06PacketUpdateHealth) {
            log(
                    stripClassName(packet.getClass().getName()) +
                            " | health : " + ((S06PacketUpdateHealth) packet).getHealth() +
                            " | foodLevel : " + ((S06PacketUpdateHealth) packet).getFoodLevel() +
                            " | saturationLevel : " + ((S06PacketUpdateHealth) packet).getSaturationLevel()
            );
        } else {
            log(stripClassName(packet.getClass().getName()));
        }
    }

    private static String stripClassName(String targetClassName) {
        final String[] split = targetClassName.split("\\.");
        return split[split.length - 1];
    }

    private static String formatXYZ(double x, double y, double z) {
        return "{x=" + String.format("%.4f", x) + ", y=" + String.format("%.4f", y) + ", z=" + String.format("%.4f", z) + '}';
    }

    private static void log(String message) {
        final String time = new SimpleDateFormat("HH:mm:ss.SSS").format(System.currentTimeMillis());
        logger.info("[" + time + "] " + message);
    }

}
