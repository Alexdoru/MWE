package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.hackerdetector.HackerDetector;
import fr.alexdoru.mwe.hackerdetector.checks.FastbreakCheck;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.BlockPos;

@SuppressWarnings("unused")
public class RenderGlobalHook {

    private static final int[] entityItemCount = new int[256];
    private static int renderDistance = 256;
    private static int prevRenderDistance = renderDistance;

    public static void resetEntityItemCount() {
        if (ConfigHandler.limitDroppedEntityRendered) {
            int entityCount = 0;
            boolean reachedLimit = false;
            for (int i = 0; i < entityItemCount.length; i++) {
                entityCount += entityItemCount[i];
                entityItemCount[i] = 0;
                if (entityCount > ConfigHandler.maxDroppedEntityRendered && !reachedLimit) {
                    reachedLimit = true;
                    renderDistance = i == 0 ? 1 : i;
                }
            }
            if (!reachedLimit) {
                renderDistance = 256;
            }
            prevRenderDistance = renderDistance;
        }
    }

    public static void renderEntitySimple(RenderManager renderManagerIn, Entity entityIn, float partialTicks, double viewerX, double viewerY, double viewerZ) {
        if (ConfigHandler.limitDroppedEntityRendered && entityIn instanceof EntityItem) {
            final double d = Math.min(entityIn.getDistanceSq(viewerX, viewerY, viewerZ), 255.9999d);
            entityItemCount[(int) d]++;
            if (d <= prevRenderDistance) {
                renderManagerIn.renderEntitySimple(entityIn, partialTicks);
            }
            return;
        }
        renderManagerIn.renderEntitySimple(entityIn, partialTicks);
    }

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
