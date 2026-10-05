package fr.alexdoru.mwe.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public final class RenderHelper {

    private RenderHelper() {}

    public static void renderSkinHead(ResourceLocation locationSkin, int x, int y, boolean renderHatLayer, int skinSize) {
        renderSkinHead(locationSkin, x, y, renderHatLayer, skinSize, 1.0F);
    }

    public static void renderSkinHead(ResourceLocation locationSkin, int x, int y, boolean renderHatLayer, int skinSize, float alpha) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        Minecraft.getMinecraft().getTextureManager().bindTexture(locationSkin);
        Gui.drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, skinSize, skinSize, 64.0F, 64.0F);
        if (renderHatLayer) {
            Gui.drawScaledCustomSizeModalRect(x, y, 40, 8, 8, 8, skinSize, skinSize, 64.0F, 64.0F);
        }
    }

    public static void drawOutline(int left, int top, int right, int bot, int borderColor) {
        RenderHelper.drawHorizontalLine(left, right - 1, top, borderColor);
        RenderHelper.drawHorizontalLine(left, right - 1, bot - 1, borderColor);
        RenderHelper.drawVerticalLine(left, top - 1, bot - 1, borderColor);
        RenderHelper.drawVerticalLine(right - 1, top - 1, bot - 1, borderColor);
    }

    public static void drawHorizontalLine(int startX, int endX, int y, int color) {
        if (endX < startX) {
            final int i = startX;
            startX = endX;
            endX = i;
        }
        Gui.drawRect(startX, y, endX + 1, y + 1, color);
    }

    public static void drawVerticalLine(int x, int startY, int endY, int color) {
        if (endY < startY) {
            final int i = startY;
            startY = endY;
            endY = i;
        }
        Gui.drawRect(x, startY + 1, x + 1, endY, color);
    }

    public static void renderLabelInWorld(String label, double x, double y, double z, float partialTicks) {

        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayer player = mc.thePlayer;
        final FontRenderer fr = mc.fontRendererObj;

        final double camX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        final double camY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        final double camZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        final double dx = x - camX;
        final double dy = y - camY;
        final double dz = z - camZ;

        GlStateManager.pushMatrix();
        GlStateManager.translate(dx, dy, dz);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        final float viewX = (mc.gameSettings.thirdPersonView == 2 ? -1 : 1) * mc.getRenderManager().playerViewX;
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(viewX, 1.0F, 0.0F, 0.0F);
        final float scale = 0.016666668F * 1.6F;
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        final int stringWidth = fr.getStringWidth(label);
        final int j = stringWidth / 2;
        GlStateManager.disableTexture2D();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(-j - 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos(-j - 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos(j + 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos(j + 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        fr.drawString(label, -stringWidth / 2, 0, 0xFFFFFFFF);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        // we don't re-enable lightning contrary to vanilla nametag rendering code
        //GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static void renderTextureInWorld(ResourceLocation resource, double x, double y, double z, float partialTicks) {

        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayer player = mc.thePlayer;

        final double camX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        final double camY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        final double camZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        final double dx = x - camX;
        final double dy = y - camY;
        final double dz = z - camZ;

        GlStateManager.pushMatrix();
        GlStateManager.translate(dx, dy, dz);
        final float viewX = (mc.gameSettings.thirdPersonView == 2 ? -1 : 1) * mc.getRenderManager().playerViewX;
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(viewX, 1.0F, 0.0F, 0.0F);
        final float scale = 1F;
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1f, 1f, 1f, 1f);
        mc.getTextureManager().bindTexture(resource);
        final float s = 0.5f;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(-s, -s, 0).tex(0, 1).endVertex();
        worldRenderer.pos(s, -s, 0).tex(1, 1).endVertex();
        worldRenderer.pos(s, s, 0).tex(1, 0).endVertex();
        worldRenderer.pos(-s, s, 0).tex(0, 0).endVertex();
        tessellator.draw();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        // we don't re-enable lightning contrary to vanilla nametag rendering code
        //GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

}
