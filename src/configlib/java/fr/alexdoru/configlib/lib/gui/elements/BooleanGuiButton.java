package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.lib.gui.MouseButton;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BooleanGuiButton extends ConfigGuiButton {

    private final ClickGuiButton button;
    private boolean toggled;

    public BooleanGuiButton(Field field, Method event, ConfigProperty annotation) throws IllegalAccessException {
        super(field, event, annotation);
        this.toggled = (boolean) this.field.get(null);
        this.button = getMainButton(getBooleanText(toggled));
        this.rightSideContentWidth = button.width + BUTTON_RIGHT_MARGIN;
        this.rightSideContentHeight = button.height;
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY, boolean canMouseBeVisuallyOverElement) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY, canMouseBeVisuallyOverElement);
        button.xPosition = contentLeft;
        button.yPosition = drawY + getCenterYOffset(button.height);
        button.drawButton(colorPalette, mc, mouseX, mouseY, canMouseBeVisuallyOverElement);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) throws IllegalAccessException {
        if (mouseButton.isLeft() && button.mousePressed(mc, mouseX, mouseY)) {
            flipBooleanConfig();
            button.displayString = getBooleanText(toggled);
            button.playPressSound(mc.getSoundHandler());
            return true;
        }
        return false;
    }

    private void flipBooleanConfig() throws IllegalAccessException {
        field.set(null, !((boolean) this.field.get(null)));
        toggled = (boolean) this.field.get(null);
        invokeConfigEvent();
    }
}
