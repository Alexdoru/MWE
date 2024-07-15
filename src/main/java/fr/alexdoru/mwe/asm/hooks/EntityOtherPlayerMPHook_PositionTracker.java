package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.EntityPlayerAccessor;
import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.hackerdetector.HackerDetector;
import net.minecraft.client.entity.EntityOtherPlayerMP;

@SuppressWarnings("unused")
public class EntityOtherPlayerMPHook_PositionTracker {

    public static void setPositionAndRotation(EntityOtherPlayerMP player, double x, double y, double z, float yaw, float pitch) {
        if (ConfigHandler.hackerDetector) {
            HackerDetector.addScheduledTask(
                    () -> ((EntityPlayerAccessor) player).getPlayerDataSamples().setPositionAndRotation(x, y, z, yaw, pitch)
            );
        }
    }

}
