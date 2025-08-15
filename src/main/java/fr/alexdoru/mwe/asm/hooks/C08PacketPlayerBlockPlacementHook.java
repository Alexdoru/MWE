package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.hackerdetector.HackerDetector;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public class C08PacketPlayerBlockPlacementHook {

    public static void onBlockPlace(BlockPos pos, int placedBlockDirectionIn, ItemStack stack) {
        if (!MWEConfig.hackerDetector) return;
        try {
            if (placedBlockDirectionIn == 255) return;
            if (pos == null || stack == null || !(stack.getItem() instanceof ItemBlock)) return;
            HackerDetector.onPlayerBlockPacket(pos, placedBlockDirectionIn, ((ItemBlock) stack.getItem()).getBlock());
        } catch (Throwable ignored) {}
    }

}
