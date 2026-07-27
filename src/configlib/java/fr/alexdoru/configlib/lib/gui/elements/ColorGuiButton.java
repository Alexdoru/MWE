package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.lib.gui.ColorSelectionGuiScreen;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import fr.alexdoru.configlib.lib.gui.MouseButton;

import java.lang.reflect.Field;

public class ColorGuiButton extends ConfigGuiButton {

    private final ConfigGuiScreen parentScreen;
    private final ClickGuiButton button;
    private int color;
    private final int defaultColor;

    public ColorGuiButton(ConfigGuiScreen configGuiScreen, Field field, ConfigProperty annotation, int defaultColor) throws IllegalAccessException {
        super(field, null, annotation);
        this.parentScreen = configGuiScreen;
        this.color = (int) this.field.get(null);
        this.defaultColor = defaultColor;
        this.button = getMainButton("Change");
        this.rightSideContentWidth = button.width + BUTTON_RIGHT_MARGIN + 1 + button.height; // button.height == color_box_size
        this.rightSideContentHeight = button.height;
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY, boolean canMouseBeVisuallyOverElement) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY, canMouseBeVisuallyOverElement);
        final int top = drawY + getCenterYOffset(button.height);
        final int colorBoxSize = button.height;
        GuiUtil.drawBoxWithOutline(contentLeft, top, contentLeft + colorBoxSize, top + colorBoxSize, 255 << 24 | color, colorPalette.COLOR_BUTTON_INDICATOR_BORDER);
        button.xPosition = contentLeft + colorBoxSize + 1;
        button.yPosition = top;
        button.drawButton(colorPalette, mc, mouseX, mouseY, canMouseBeVisuallyOverElement);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) throws IllegalAccessException {
        if (mouseButton.isLeft() && button.mousePressed(mc, mouseX, mouseY)) {
            button.playPressSound(mc.getSoundHandler());
            mc.displayGuiScreen(new ColorSelectionGuiScreen(parentScreen, field, defaultColor, color -> this.color = color));
            return true;
        }
        return false;
    }
}
