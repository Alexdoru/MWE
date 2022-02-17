package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ChatComponentText;

public class RenderGlobalHook {

    private static int[] entityItemCount = new int[16];
    private static int renderDistance = 16;

    private static int DEBUG_prev_renderDistance = renderDistance;

    public static void resetEntityItemCount() {
        int entityCount = 0;
        for (int i = 0; i < entityItemCount.length; i++) {
            entityCount += entityItemCount[i];
            if (entityCount > ConfigHandler.maxDroppedEntityRendered) {
                renderDistance = i == 0 ? 1 : i;
                if (DEBUG_prev_renderDistance != renderDistance) {
                    ChatUtil.addChatMessage(new ChatComponentText("Changed item render distance to : " + renderDistance));
                }
                DEBUG_prev_renderDistance = renderDistance;
                entityItemCount = new int[16];
                return;
            }
        }
        renderDistance = 16;
        if (DEBUG_prev_renderDistance != renderDistance) {
            ChatUtil.addChatMessage(new ChatComponentText("Changed item render distance to : " + renderDistance));
        }
        DEBUG_prev_renderDistance = renderDistance;
        entityItemCount = new int[16];
    }

    public static void renderEntitySimple(RenderManager renderManagerIn, Entity entityIn, float partialTicks, double viewerX, double viewerY, double viewerZ) {
        if (ConfigHandler.limitDroppedEntityRendered && entityIn instanceof EntityItem) {
            if (shouldRender(entityIn, viewerX, viewerY, viewerZ)) {
                renderManagerIn.renderEntitySimple(entityIn, partialTicks);
            }
            return;
        }
        renderManagerIn.renderEntitySimple(entityIn, partialTicks);
    }

    private static boolean shouldRender(Entity entityIn, double viewerX, double viewerY, double viewerZ) {
        double d = Math.min(entityIn.getDistance(viewerX, viewerY, viewerZ), 15.9999d);
        int i = (int) d;
        entityItemCount[i]++;
        return d <= renderDistance;
    }

}
