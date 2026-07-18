package fr.alexdoru.mwe.utils;

import net.minecraft.client.gui.Gui;

public final class RenderHelper {

    private RenderHelper() {}

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
