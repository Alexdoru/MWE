package fr.alexdoru.configlib.lib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public final class GuiUtil {

    private GuiUtil() {}

    public static void drawRect(Box box, int color) {
        Gui.drawRect(box.LEFT, box.TOP, box.RIGHT, box.BOTTOM, color);
    }

    public static void drawBoxWithOutline(Box box, int boxColor, int borderColor) {
        GuiUtil.drawBoxWithOutline(box.LEFT, box.TOP, box.RIGHT, box.BOTTOM, boxColor, borderColor);
    }

    public static void drawBoxWithOutline(int left, int top, int right, int bot, int boxColor, int borderColor) {
        GuiUtil.drawOutline(left, top, right, bot, borderColor);
        Gui.drawRect(left + 1, top + 1, right - 1, bot - 1, boxColor);
    }

    public static void drawOutline(int left, int top, int right, int bot, int borderColor) {
        GuiUtil.drawHorizontalLine(left, right - 1, top, borderColor);
        GuiUtil.drawHorizontalLine(left, right - 1, bot - 1, borderColor);
        GuiUtil.drawVerticalLine(left, top - 1, bot - 1, borderColor);
        GuiUtil.drawVerticalLine(right - 1, top - 1, bot - 1, borderColor);
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

    public static int brightenColor(int color, float amount) {
        final int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        r = r + (int) ((255 - r) * amount);
        g = g + (int) ((255 - g) * amount);
        b = b + (int) ((255 - b) * amount);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static void drawHorizontalGradient(Box box, int leftColor, int rightColor) {
        drawGradient(box.LEFT, box.TOP, box.RIGHT, box.BOTTOM, leftColor, rightColor, rightColor, leftColor);
    }

    public static void drawVerticalGradient(Box box, int topColor, int bottomColor) {
        drawVerticalGradient(box.LEFT, box.TOP, box.RIGHT, box.BOTTOM, topColor, bottomColor);
    }

    public static void drawVerticalGradient(int left, int top, int right, int bottom, int topColor, int bottomColor) {
        drawGradient(left, top, right, bottom, topColor, topColor, bottomColor, bottomColor);
    }

    private static void drawGradient(int leftPos, int topPos, int rightPos, int bottomPos, int topLeftColor, int topRightColor, int bottomRightColor, int bottomLeftColor) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        addColoredVertex(worldrenderer, rightPos, topPos, topRightColor);
        addColoredVertex(worldrenderer, leftPos, topPos, topLeftColor);
        addColoredVertex(worldrenderer, leftPos, bottomPos, bottomLeftColor);
        addColoredVertex(worldrenderer, rightPos, bottomPos, bottomRightColor);
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    private static void addColoredVertex(WorldRenderer worldrenderer, int x, int y, int argb) {
        worldrenderer.pos(x, y, 0d).color(argb >> 16 & 0xFF, argb >> 8 & 0xFF, argb & 0xFF, argb >>> 24).endVertex();
    }

    public static void drawCheckerboard(Box box, int cellSize) {
        for (int y = box.TOP; y < box.BOTTOM; y += cellSize) {
            for (int x = box.LEFT; x < box.RIGHT; x += cellSize) {
                final boolean light = ((x - box.LEFT) / cellSize + (y - box.TOP) / cellSize) % 2 == 0;
                Gui.drawRect(x, y, Math.min(x + cellSize, box.RIGHT), Math.min(y + cellSize, box.BOTTOM), light ? 0xFFFFFFFF : 0xFFBFBFBF);
            }
        }
    }

    public static void drawFullTextureWithCustomSize(int left, int top, int drawWidth, int drawHeight) {
        Gui.drawModalRectWithCustomSizedTexture(left, top, 0, 0, drawWidth, drawHeight, drawWidth, drawHeight);
    }

    public static void drawCenteredString(String text, int x, int y, int color) {
        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        fr.drawStringWithShadow(text, (float) (x - fr.getStringWidth(text) / 2), (float) y, color);
    }

}
