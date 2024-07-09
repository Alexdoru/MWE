package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.mwe.config.ConfigHandler;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;

@SuppressWarnings("unused")
public class NetHandlerPlayClientHook_BlockBreakAnim {

    public static void handleBlockBreakAnim(WorldClient world, S25PacketBlockBreakAnim packet) {
        if (ConfigHandler.hackerDetector) {
            final int progress = packet.getProgress();
            if (progress >= 0 && progress < 255) {
                final Entity entity = world.getEntityByID(packet.getBreakerId());
                if (entity instanceof EntityOtherPlayerMP) {
                    final EntityOtherPlayerMP player = (EntityOtherPlayerMP) entity;
                    if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemTool) {
                        ((EntityPlayerAccessor) player).getPlayerDataSamples().blockTouched = packet.getPosition();
                    }
                }
            }
        }
    }

}
