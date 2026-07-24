package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.network.Packet;

public class NetworkManagerHook_PacketListener {

    // Careful, this code isn't called from the main thread
    // Only listens packets that throw a ThreadQuickExitException
    public static void listen(Packet<?> packet) {
        if (!MWEConfig.hackerDetector) return;
        try {
            MWE.INSTANCE().getHackerDetector().lookForAttacks(packet);
        } catch (Throwable t) {
            MWE.logger.error("Caught exception from Hacker Detector", t);
        }
    }

}
