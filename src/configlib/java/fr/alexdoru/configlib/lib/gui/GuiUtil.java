package fr.alexdoru.configlib.lib.gui;

import net.minecraft.client.gui.Gui;

public final class GuiUtil {

    private GuiUtil() {}

    public static void drawBoxWithOutline(int left, int top, int right, int bot, int boxColor, int borderColor) {
        drawHorizontalLine(left, right - 1, top, borderColor);
        drawHorizontalLine(left, right - 1, bot - 1, borderColor);
        drawVerticalLine(left, top - 1, bot - 1, borderColor);
        drawVerticalLine(right - 1, top - 1, bot - 1, borderColor);
        Gui.drawRect(left + 1, top + 1, right - 1, bot - 1, boxColor);
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

}
