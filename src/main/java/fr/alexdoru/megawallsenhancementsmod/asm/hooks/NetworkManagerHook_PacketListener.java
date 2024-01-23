package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.S19PacketEntityStatusAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S19PacketEntityStatus;

@SuppressWarnings("unused")
public class NetworkManagerHook_PacketListener {

    private static boolean lastPacketWasSwing;
    private static long lastSwingTime;
    private static int attackerID;

    // Careful, this code isn't called from the main thread
    // Only listens packets that throw a ThreadQuickExitException
    public static void listen(Packet<?> packet) {
        if (!ConfigHandler.hackerDetector) return;
        try { // We need a try catch block to prevent any exception from being throwned, it would discard the packet
            //PacketLogger.logPacket(packet);
            lookForAttacks(packet);
        } catch (Throwable ignored) {}
    }

    private static void lookForAttacks(Packet<?> packet) {
        if (packet instanceof S19PacketEntityStatusAccessor && ((S19PacketEntityStatus) packet).getOpCode() == 2) { // Entity gets hurt
            final long timeDiff = System.currentTimeMillis() - lastSwingTime;
            if (timeDiff < 2) {
                if (lastPacketWasSwing) {
                    HackerDetector.checkPlayerAttack(attackerID, ((S19PacketEntityStatusAccessor) packet).getEntityId(), 1);
                } else {
                    HackerDetector.checkPlayerAttack(attackerID, ((S19PacketEntityStatusAccessor) packet).getEntityId(), 2);
                }
            }
        } else if (packet instanceof S0BPacketAnimation) {
            final S0BPacketAnimation packetAnimation = (S0BPacketAnimation) packet;
            final int animationType = packetAnimation.getAnimationType();
            if (animationType == 0) { // Swing packet
                lastPacketWasSwing = true;
                lastSwingTime = System.currentTimeMillis();
                attackerID = packetAnimation.getEntityID();
                HackerDetector.onEntitySwing(attackerID);
                return;
            } else if (animationType == 4 || animationType == 5) { // crit/enchant particle packet
                final long timeDiff = System.currentTimeMillis() - lastSwingTime;
                if (timeDiff < 2) {
                    if (lastPacketWasSwing) {
                        HackerDetector.checkPlayerAttack(attackerID, packetAnimation.getEntityID(), 1);
                    } else {
                        HackerDetector.checkPlayerAttack(attackerID, packetAnimation.getEntityID(), animationType);
                    }
                }
            }
        } else if (packet instanceof S12PacketEntityVelocity) {
            final long timeDiff = System.currentTimeMillis() - lastSwingTime;
            if (timeDiff < 2) {
                final S12PacketEntityVelocity packetVelo = (S12PacketEntityVelocity) packet;
                if (packetVelo.getMotionX() != 0 || packetVelo.getMotionY() != 0 || packetVelo.getMotionZ() != 0) {
                    HackerDetector.checkPlayerAttack(attackerID, packetVelo.getEntityID(), 0);
                }
            }
        }
        lastPacketWasSwing = false;
    }

}
