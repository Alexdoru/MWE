package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.ConfigFieldContainer;
import fr.alexdoru.configlib.lib.gui.ColorSelectionGuiScreen;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import fr.alexdoru.configlib.lib.gui.MouseButton;

public class ColorGuiButton extends ConfigGuiButton {

    private final ConfigGuiScreen parentScreen;
    private final ClickGuiButton button;
    private int color;
    private final int defaultColor;

    public ColorGuiButton(ConfigFieldContainer container, ConfigGuiScreen configGuiScreen, int defaultColor) throws IllegalAccessException {
        super(container);
        this.parentScreen = configGuiScreen;
        this.color = (int) this.field.get(null);
        this.defaultColor = defaultColor;
        this.button = new ClickGuiButton(mc.fontRendererObj.getStringWidth(" Disabled "), 20, "Change");
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY);
        button.xPosition = drawX + boxWidth - button.width - 20;
        button.yPosition = drawY + (this.hasComment() ? 8 + mc.fontRendererObj.FONT_HEIGHT / 2 : (getHeight() - button.height) / 2);
        button.drawButton(colorPalette, mc, mouseX, mouseY);
        final int left = button.xPosition - 20 - 1;
        final int top = button.yPosition;
        GuiUtil.drawBoxWithOutline(left, top, left + 20, top + 20, 255 << 24 | color, colorPalette.COLOR_BUTTON_INDICATOR_BORDER);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) throws IllegalAccessException {
        if (mouseButton.isLeft() && button.mousePressed(mouseX, mouseY)) {
            mc.displayGuiScreen(new ColorSelectionGuiScreen(parentScreen, field, defaultColor, color -> {
                this.color = color;
                this.invokeConfigEvent();
            }));
            return true;
        }
        return false;
    }

}
