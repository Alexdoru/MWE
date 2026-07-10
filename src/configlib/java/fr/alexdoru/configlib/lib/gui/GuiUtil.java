package fr.alexdoru.configlib.lib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public final class GuiUtil {

    private GuiUtil() {}

    public static void drawBoxOutline(int left, int top, int right, int bottom, int color) {
        drawVerticalLine(left, top, bottom, color);
        drawVerticalLine(right - 1, top, bottom, color);
        drawHorizontalLine(left, right, top, color);
        drawHorizontalLine(left, right, bottom - 1, color);
    }

    public static void drawBoxWithOutline(int left, int top, int right, int bot, int boxColor, int borderColor) {
        Gui.drawRect(left + 1, top + 1, right - 1, bot - 1, boxColor);
        drawBoxOutline(left, top, right, bot, borderColor);
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

    /**
     * Begins the stencil clear rect.
     * Call {@link #endClearRect()} after rendering if this returned {@code true}.
     * @return {@code true} if this operation worked
     */
    public static boolean beginClearRect(Minecraft mc, int left, int top, int right, int bottom) {
        if (!mc.getFramebuffer().isStencilEnabled() && !mc.getFramebuffer().enableStencil()) return false;
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);

        GlStateManager.colorMask(false, false, false, false);

        Gui.drawRect(left, top, right, bottom, 0xFFFFFFFF);

        GlStateManager.colorMask(true, true, true, true);

        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF); // skip hole
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

        return true;
        // GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    /** See {@link #beginClearRect(Minecraft, int, int, int, int)} */
    public static void endClearRect() {
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }
}
