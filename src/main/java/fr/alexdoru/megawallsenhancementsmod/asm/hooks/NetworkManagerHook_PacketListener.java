package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.AttackDetector;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.debug.ClientPacketLogger;
import net.minecraft.network.Packet;

@SuppressWarnings("unused")
public class NetworkManagerHook_PacketListener {

    // Careful, this code isn't called from the main thread
    // Only listens packets that throw a ThreadQuickExitException
    public static void listen(Packet<?> packet) {
        if (!ConfigHandler.hackerDetector) return;
        try {
            AttackDetector.lookForAttacks(packet);
            //PacketHUD.INSTANCE.logPacket(packet);
            //ServerPacketLogger.logPacket(packet);
        } catch (Throwable ignored) {}
    }

    public static void listenSentPacket(Packet<?> packet) {
        try {
            ClientPacketLogger.logPacket(packet);
        } catch (Throwable ignored) {}
    }

}
