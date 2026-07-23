package fr.alexdoru.mwe.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
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

}
