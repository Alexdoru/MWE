package fr.alexdoru.configlib.gui;

import net.minecraft.client.gui.Gui;

public final class GuiUtil {

    public static void drawBoxWithOutline(int left, int top, int right, int bot, int boxColor, int outlineColor) {
        drawHorizontalLine(left, right - 1, top, outlineColor);
        drawHorizontalLine(left, right - 1, bot - 1, outlineColor);
        drawVerticalLine(left, top - 1, bot - 1, outlineColor);
        drawVerticalLine(right - 1, top - 1, bot - 1, outlineColor);
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

}
