package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.S19PacketEntityStatusAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class NetworkManagerHook_PacketListener {

    private static final Logger logger = LogManager.getLogger("PacketListener");
    private static boolean lastPacketWasSwing;
    private static int attackerID;

    // Careful, this code isn't called from the main thread
    // Only listens packets that throw a ThreadQuickExitException
    public static void listen(@SuppressWarnings("rawtypes") Packet packet) {
        if (!ConfigHandler.hackerDetector) return;
        try { // We need a try catch block to prevent any exception from being throwned, it would discard the packet
            lookForAttacks(packet);
        } catch (Throwable ignored) {}
    }

    private static void logPacket(@SuppressWarnings("rawtypes") Packet packet) {
        if (packet instanceof S2APacketParticles) return;
        if (packet instanceof S0BPacketAnimation) {
            logger.info(
                    "S0BPacketAnimation" +
                            " entityId : " + ((S0BPacketAnimation) packet).getEntityID() +
                            " type : " + ((S0BPacketAnimation) packet).getAnimationType()
            );
        } else if (packet instanceof S19PacketEntityStatusAccessor) {
            logger.info(
                    stripClassName(packet.getClass().getName()) +
                            " entityId : " + ((S19PacketEntityStatusAccessor) packet).getEntityId() +
                            " logicOpcode : " + ((S19PacketEntityStatus) packet).getOpCode()
            );
        } else if (packet instanceof S04PacketEntityEquipment) {
            logger.info(
                    stripClassName(packet.getClass().getName()) +
                            " entityId : " + ((S04PacketEntityEquipment) packet).getEntityID() +
                            " equipmentSlot : " + ((S04PacketEntityEquipment) packet).getEquipmentSlot() +
                            (((S04PacketEntityEquipment) packet).getItemStack().getItem() == null ? "" :
                                    " itemStack : " + ((S04PacketEntityEquipment) packet).getItemStack().getItem().getUnlocalizedName())
            );
        } else if (packet instanceof S29PacketSoundEffect) {
            logger.info(
                    stripClassName(packet.getClass().getName()) +
                            " soundName : " + ((S29PacketSoundEffect) packet).getSoundName()
            );
        } else if (packet instanceof S06PacketUpdateHealth) {
            logger.info(
                    stripClassName(packet.getClass().getName()) +
                            " health : " + ((S06PacketUpdateHealth) packet).getHealth() +
                            " foodLevel : " + ((S06PacketUpdateHealth) packet).getFoodLevel() +
                            " saturationLevel : " + ((S06PacketUpdateHealth) packet).getSaturationLevel()
            );
        } else {
            logger.info(stripClassName(packet.getClass().getName()));
        }
    }

    private static String stripClassName(String targetClassName) {
        final String[] split = targetClassName.split("\\.");
        return split[split.length - 1];
    }

    private static void lookForAttacks(@SuppressWarnings("rawtypes") Packet packet) {
        if (packet instanceof S19PacketEntityStatusAccessor && ((S19PacketEntityStatus) packet).getOpCode() == 2) { // Entity gets hurt
            if (lastPacketWasSwing) {
                HackerDetector.checkPlayerAttack(attackerID, ((S19PacketEntityStatusAccessor) packet).getEntityId(), 1);
            } else {
                HackerDetector.checkPlayerAttack(attackerID, ((S19PacketEntityStatusAccessor) packet).getEntityId(), 2);
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
            }
        }
        lastPacketWasSwing = false;
    }

}
