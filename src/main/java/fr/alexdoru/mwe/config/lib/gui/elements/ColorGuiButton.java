package fr.alexdoru.mwe.config.lib.gui.elements;

import fr.alexdoru.mwe.config.lib.ConfigProperty;
import fr.alexdoru.mwe.config.lib.gui.ColorSelectionGuiScreen;
import fr.alexdoru.mwe.config.lib.gui.ConfigGuiScreen;
import fr.alexdoru.mwe.gui.GuiUtil;
import net.minecraft.client.gui.GuiButton;

import java.awt.Color;
import java.lang.reflect.Field;

public class ColorGuiButton extends ConfigGuiButton {

    private final ConfigGuiScreen parentScreen;
    private final GuiButton button;
    private int color;
    private final int defaultColor;

    public ColorGuiButton(ConfigGuiScreen configGuiScreen, Field field, ConfigProperty annotation, int defaultColor) throws IllegalAccessException {
        super(field, null, annotation);
        this.parentScreen = configGuiScreen;
        this.color = (int) this.field.get(null);
        this.defaultColor = defaultColor;
        this.button = new GuiButton(0, 0, 0, mc.fontRendererObj.getStringWidth(" Disabled "), 20, "Change");
    }

    @Override
    public void draw(int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(drawX, drawY, mouseX, mouseY);
        button.xPosition = drawX + boxWidth - button.width - 20;
        button.yPosition = drawY + (hasComment ? 8 + mc.fontRendererObj.FONT_HEIGHT / 2 : (getHeight() - button.height) / 2);
        button.drawButton(mc, mouseX, mouseY);
        final int left = button.xPosition - 20 - 1;
        final int top = button.yPosition;
        GuiUtil.drawBoxWithOutline(left, top, left + 20, top + 20, 255 << 24 | color, Color.BLACK.getRGB());
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IllegalAccessException {
        if (mouseButton == 0 && button.mousePressed(mc, mouseX, mouseY)) {
            button.playPressSound(mc.getSoundHandler());
            mc.displayGuiScreen(new ColorSelectionGuiScreen(parentScreen, field, defaultColor, color -> this.color = color));
            return true;
        }
        return false;
    }

}
