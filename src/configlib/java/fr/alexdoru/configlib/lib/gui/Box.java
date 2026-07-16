package fr.alexdoru.configlib.lib.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public final class Box {

    public int LEFT, TOP, RIGHT, BOTTOM;

    public int getHeight() {
        return BOTTOM - TOP;
    }

    public int getWidth() {
        return RIGHT - LEFT;
    }

    public boolean isMouseInBox(int mouseX, int mouseY) {
        return mouseX >= LEFT && mouseX < RIGHT && mouseY >= TOP && mouseY < BOTTOM;
    }

    public void applyScissors(Minecraft mc, ScaledResolution res, int margin) {
        final double width = mc.displayWidth / res.getScaledWidth_double();
        final double height = mc.displayHeight / res.getScaledHeight_double();
        GL11.glScissor(
                (int) ((LEFT + margin) * width),
                (int) (mc.displayHeight - ((BOTTOM - margin) * height)),
                (int) ((RIGHT - margin - (LEFT + margin)) * width),
                (int) ((BOTTOM - margin - (TOP + margin)) * height)
        );
    }

}
