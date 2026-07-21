package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;

public class NetHandlerPlayClientHook_BlockChangeListener {

    public static void onBlockChange(S23PacketBlockChange packet) {
        if (!MWEConfig.hackerDetector) return;
        try {
            MWE.INSTANCE().getHackerDetector().addPlacedBlock(packet.getBlockPosition(), packet.getBlockState());
        } catch (Throwable ignored) {}
    }

    public static void onMultiBlockChange(S22PacketMultiBlockChange packet) {
        if (!MWEConfig.hackerDetector) return;
        try {
            for (final S22PacketMultiBlockChange.BlockUpdateData blockData : packet.getChangedBlocks()) {
                MWE.INSTANCE().getHackerDetector().addPlacedBlock(blockData.getPos(), blockData.getBlockState());
            }
        } catch (Throwable ignored) {}
    }

}
