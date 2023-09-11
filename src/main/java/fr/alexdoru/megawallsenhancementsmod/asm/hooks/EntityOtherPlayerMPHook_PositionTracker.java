package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
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
