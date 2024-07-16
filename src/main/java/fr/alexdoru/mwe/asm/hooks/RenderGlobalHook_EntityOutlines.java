package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.asm.interfaces.IWitherColor;
import fr.alexdoru.mwe.asm.interfaces.RenderManagerAccessor;
import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.MinecraftForgeClient;

import java.util.List;

@SuppressWarnings("unused")
public class RenderGlobalHook_EntityOutlines {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static boolean hasRendered = false;

    public static boolean renderWitherOutline(
            boolean original,
            Entity renderViewEntity,
            ICamera camera,
            float partialTicks,
            Framebuffer entityOutlineFramebuffer,
            ShaderGroup entityOutlineShader) {
        if (original) {
            hasRendered = false;
            return true;
        }
        final boolean b = MinecraftForgeClient.getRenderPass() == 1 &&
                ConfigHandler.renderWitherOutline &&
                entityOutlineFramebuffer != null &&
                entityOutlineShader != null &&
                mc.thePlayer != null &&
                ScoreboardTracker.isInMwGame() &&
                !ScoreboardTracker.getParser().isDeathmatch();
        if (!b) {
            hasRendered = false;
            return false;
        }
        final List<Entity> list = mc.theWorld.getLoadedEntityList();
        for (final Entity e : list) {
            if (e instanceof IWitherColor && ((IWitherColor) e).getmwe$Color() != 0) {
                renderWitherOutline(renderViewEntity, camera, partialTicks, entityOutlineFramebuffer, entityOutlineShader, e);
                return false;
            }
        }
        hasRendered = false;
        return false;
    }

    public static boolean shouldDoFinalDraw(boolean original) {
        if (original || hasRendered) {
            hasRendered = false;
            return true;
        } else {
            return false;
        }
    }

    // removed the loop from the original code because there is only going to be 1 wither in mega walls
    private static void renderWitherOutline(
            Entity renderViewEntity,
            ICamera camera,
            float partialTicks,
            Framebuffer entityOutlineFramebuffer,
            ShaderGroup entityOutlineShader,
            Entity entity) {
        final double d0 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * (double) partialTicks;
        final double d1 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * (double) partialTicks;
        final double d2 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * (double) partialTicks;
        final boolean flag = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase) mc.getRenderViewEntity()).isPlayerSleeping();
        final boolean flag1 = entity.isInRangeToRender3d(d0, d1, d2) &&
                (entity.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(entity.getEntityBoundingBox()) || entity.riddenByEntity == mc.thePlayer);
        final boolean doRender = (entity != mc.getRenderViewEntity() || mc.gameSettings.thirdPersonView != 0 || flag) && flag1;
        if (!doRender) {
            hasRendered = false;
            return;
        }
        GlStateManager.depthFunc(519);
        GlStateManager.disableFog();
        entityOutlineFramebuffer.framebufferClear();
        entityOutlineFramebuffer.bindFramebuffer(false);
        mc.theWorld.theProfiler.endStartSection("entityOutlines");
        RenderHelper.disableStandardItemLighting();
        mc.getRenderManager().setRenderOutlines(true);
        mc.getRenderManager().renderEntitySimple(entity, partialTicks);
        mc.getRenderManager().setRenderOutlines(false);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.depthMask(false);
        entityOutlineShader.loadShaderGroup(partialTicks);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        mc.getFramebuffer().bindFramebuffer(false);
        GlStateManager.enableFog();
        GlStateManager.enableBlend();
        GlStateManager.enableColorMaterial();
        GlStateManager.depthFunc(515);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        hasRendered = true;
    }

    public static boolean isRenderOutlines() {
        return ((RenderManagerAccessor) mc.getRenderManager()).isRenderOutlines();
    }

}
