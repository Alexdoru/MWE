package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BooleanGuiButton extends ConfigGuiButton {

    private final ClickGuiButton button;
    private boolean toggled;

    public BooleanGuiButton(Field field, Method event, ConfigProperty annotation) throws IllegalAccessException {
        super(field, event, annotation);
        this.toggled = (boolean) this.field.get(null);
        this.button = getMainButton(getBooleanText(toggled));
    }

    @Override
    protected int getRightSideContentWidth() {
        return button.width + BUTTON_RIGHT_MARGIN;
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY);
        button.xPosition = contentLeft;
        button.yPosition = drawY + (hasComment ? PADDING + mc.fontRendererObj.FONT_HEIGHT / 2 : (getHeight() - button.height) / 2);
        button.drawButton(colorPalette, mc, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IllegalAccessException {
        if (mouseButton == 0 && button.mousePressed(mc, mouseX, mouseY)) {
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
