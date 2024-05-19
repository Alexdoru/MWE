package fr.alexdoru.megawallsenhancementsmod.gui.elements;

import net.minecraft.client.gui.Gui;

import java.awt.Color;
import java.util.function.Supplier;

public class ColoredSquareElement implements UIElement {

    private final int leftX;
    private final int topY;
    private final int sideLength;
    private final Supplier<Integer> color;

    public ColoredSquareElement(int leftX, int topY, int sideLength, Supplier<Integer> color) {
        this.leftX = leftX;
        this.topY = topY;
        this.sideLength = sideLength;
        this.color = color;
    }

    @Override
    public void render() {
        final int right = leftX + sideLength;
        final int bottom = topY + sideLength;
        drawHorizontalLine(leftX, right, topY, Color.BLACK.getRGB());
        drawHorizontalLine(leftX, right, bottom, Color.BLACK.getRGB());
        drawVerticalLine(leftX, topY, bottom, Color.BLACK.getRGB());
        drawVerticalLine(right, topY, bottom, Color.BLACK.getRGB());
        Gui.drawRect(leftX + 1, topY + 1, right, bottom, 255 << 24 | color.get());
    }

}
