package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.BrokenBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.BlockPos;

@SuppressWarnings("unused")
public class RenderGlobalHook {

    private static int[] entityItemCount = new int[256];
    private static int renderDistance = 256;
    private static int prevRenderDistance = renderDistance;

    public static void resetEntityItemCount() {
        if (ConfigHandler.limitDroppedEntityRendered) {
            int entityCount = 0;
            for (int i = 0; i < entityItemCount.length; i++) {
                entityCount += entityItemCount[i];
                if (entityCount > ConfigHandler.maxDroppedEntityRendered) {
                    renderDistance = i == 0 ? 1 : i;
                    prevRenderDistance = renderDistance;
                    entityItemCount = new int[256];
                    return;
                }
            }
            renderDistance = 256;
            prevRenderDistance = renderDistance;
            entityItemCount = new int[256];
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
        if (ConfigHandler.hackerDetector && FKCounterMod.isInMwGame && "pickaxe".equals(state.getBlock().getHarvestTool(state))) {
            HackerDetector.INSTANCE.brokenBlocksList.add(new BrokenBlock(state.getBlock(), blockPos, System.currentTimeMillis()));
        }
    }

}
