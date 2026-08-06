package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.ConfigFieldContainer;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import net.minecraft.util.EnumChatFormatting;

public class BooleanGuiButton extends ConfigGuiButton {

    private final ConfigGuiScreen configGuiScreen;
    private final ClickGuiButton button;
    private boolean toggled;

    public BooleanGuiButton(ConfigFieldContainer container, ConfigGuiScreen configGuiScreen) throws IllegalAccessException {
        super(container);
        this.configGuiScreen = configGuiScreen;
        this.toggled = (boolean) this.field.get(null);
        this.button = new ClickGuiButton(mc.fontRendererObj.getStringWidth(" Disabled "), 20, getButtonText());
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY);
        button.xPosition = drawX + boxWidth - button.width - 20;
        button.yPosition = drawY + (this.hasComment() ? 8 + mc.fontRendererObj.FONT_HEIGHT / 2 : (getHeight() - button.height) / 2);
        button.drawButton(colorPalette, mc, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) throws IllegalAccessException {
        if (mouseButton.isLeft() && button.mousePressed(mouseX, mouseY)) {
            flipBooleanConfig();
            button.displayString = getButtonText();
            toggleDependencies(this.configGuiScreen);
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
