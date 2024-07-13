package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.hackerdetector.HackerDetector;
import fr.alexdoru.mwe.hackerdetector.checks.FastbreakCheck;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

@SuppressWarnings("unused")
public class RenderGlobalHook_ListenDestroyedBlocks {

    public static void listenDestroyedBlocks(IBlockState state, BlockPos blockPos) {
        if (ConfigHandler.hackerDetector && FastbreakCheck.isCheckActive()) {
            final String tool = state.getBlock().getHarvestTool(state);
            // for trapped chests the tool is null
            if ("pickaxe".equals(tool) || "axe".equals(tool) || tool == null) {
                HackerDetector.addBrokenBlock(state.getBlock(), blockPos, tool);
            }
        }
    }

}
