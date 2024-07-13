package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.ConfigHandler;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

@SuppressWarnings("unused")
public class RenderGlobalHook_LimitDroppedItems {

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

}
