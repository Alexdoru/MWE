package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.entity.Entity;

@SuppressWarnings("unused")
public class RenderHook_LimitDroppedItems {

    private static final int[] entityItemCount = new int[256];
    private static int renderDistance = 256;
    private static int prevRenderDistance = renderDistance;

    public static void resetEntityItemCount() {
        if (MWEConfig.limitDroppedEntityRendered) {
            int entityCount = 0;
            boolean reachedLimit = false;
            final int itemLimit = MWEConfig.maxDroppedEntityRendered;
            for (int i = 0; i < entityItemCount.length; i++) {
                entityCount += entityItemCount[i];
                entityItemCount[i] = 0;
                if (!reachedLimit && entityCount > itemLimit) {
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

    public static boolean shouldRenderEntityItem(Entity entityItem, double viewerX, double viewerY, double viewerZ) {
        if (MWEConfig.limitDroppedEntityRendered) {
            final double d = Math.min(entityItem.getDistanceSq(viewerX, viewerY, viewerZ), 255.9999d);
            entityItemCount[(int) d]++;
            return d <= prevRenderDistance;
        }
        return true;
    }

}
