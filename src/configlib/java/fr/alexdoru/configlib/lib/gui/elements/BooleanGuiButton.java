package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import net.minecraft.util.EnumChatFormatting;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BooleanGuiButton extends ConfigGuiButton {

    private final ClickGuiButton button;
    private boolean toggled;

    public BooleanGuiButton(Field field, Method event, ConfigProperty annotation) throws IllegalAccessException {
        super(field, event, annotation);
        this.toggled = (boolean) this.field.get(null);
        this.button = new ClickGuiButton(0, 0, 0, mc.fontRendererObj.getStringWidth(" Disabled "), 20, getButtonText());
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY);
        button.xPosition = drawX + boxWidth - button.width - 20;
        button.yPosition = drawY + (hasComment ? 8 + mc.fontRendererObj.FONT_HEIGHT / 2 : (getHeight() - button.height) / 2);
        button.drawButton(colorPalette, mc, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) throws IllegalAccessException {
        if (mouseButton.isLeft() && button.mousePressed(mc, mouseX, mouseY)) {
            flipBooleanConfig();
            button.displayString = getButtonText();
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

    private String getButtonText() {
        return toggled ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled";
    }

}
