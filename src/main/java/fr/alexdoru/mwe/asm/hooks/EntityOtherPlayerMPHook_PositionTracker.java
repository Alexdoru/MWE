package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.asm.interfaces.EntityPlayerAccessor;
import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.entity.EntityOtherPlayerMP;

public class EntityOtherPlayerMPHook_PositionTracker {

    public static void setPositionAndRotation(EntityOtherPlayerMP player, double x, double y, double z, float yaw, float pitch) {
        if (MWEConfig.hackerDetector) {
            MWE.INSTANCE().getHackerDetector().addScheduledTask(() ->
                    ((EntityPlayerAccessor) player).getPlayerDataSamples().setPositionAndRotation(x, y, z, yaw, pitch)
            );
        }
    }

}
