package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.RendererPosition;
import fr.alexdoru.configlib.lib.ConfigFieldContainer;
import fr.alexdoru.configlib.lib.RendererManager;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import fr.alexdoru.configlib.lib.gui.RendererEditGuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class RendererGuiButton extends ConfigGuiButton {

    private final ConfigGuiScreen parentScreen;
    private final RendererManager rendererManager;
    private final RendererPosition rendererPosition;
    private boolean toggled;
    private final ClickGuiButton buttonEnabled;
    private final ClickGuiButton buttonMoveHud;

    public RendererGuiButton(ConfigFieldContainer container, ConfigGuiScreen configGuiScreen, RendererManager rendererManager) throws IllegalAccessException {
        super(container);
        this.parentScreen = configGuiScreen;
        this.rendererManager = rendererManager;
        this.rendererPosition = ((RendererPosition) field.get(null));
        this.toggled = this.rendererPosition.isEnabled();
        this.buttonEnabled = new ClickGuiButton(mc.fontRendererObj.getStringWidth(" Disabled "), 20, getButtonText());
        this.buttonMoveHud = new ClickGuiButton(20, 20);
        this.buttonMoveHud.setTexture(new ResourceLocation("configlib", "move.png"));
    }

    @Override
    public void setBoxWidth(int boxWidth) {
        super.setBoxWidth(boxWidth - 20 - 1);
        this.boxWidth = boxWidth;
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY);
        buttonEnabled.xPosition = drawX + boxWidth - buttonEnabled.width - 20;
        buttonEnabled.yPosition = drawY + 8;
        buttonEnabled.drawButton(colorPalette, mc, mouseX, mouseY);

        buttonMoveHud.xPosition = buttonEnabled.xPosition - buttonMoveHud.width - 1;
        buttonMoveHud.yPosition = buttonEnabled.yPosition;
        buttonMoveHud.drawButton(colorPalette, mc, mouseX, mouseY);

        if (buttonMoveHud.isMouseOver()) {
            final String text = "Move HUD";
            final int textX = buttonMoveHud.xPosition - 4 - mc.fontRendererObj.getStringWidth(text);
            final int textY = buttonMoveHud.yPosition + (buttonMoveHud.height - 8) / 2;
            mc.fontRendererObj.drawStringWithShadow(text, textX, textY, colorPalette.HUD_BUTTON_HINT_TEXT);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) {
        if (mouseButton.isLeft()) {
            if (buttonEnabled.mousePressed(mouseX, mouseY)) {
                flipBooleanConfig();
                buttonEnabled.displayString = getButtonText();
                toggleDependencies(this.parentScreen);
                return true;
            } else if (buttonMoveHud.mousePressed(mouseX, mouseY)) {
                mc.displayGuiScreen(new RendererEditGuiScreen(rendererManager, rendererPosition, parentScreen, field));
                return true;
            }
        }
        return false;
    }

    private void flipBooleanConfig() {
        rendererPosition.setEnabled(!rendererPosition.isEnabled());
        toggled = rendererPosition.isEnabled();
        invokeConfigEvent();
    }

    private String getButtonText() {
        return toggled ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled";
    }

}
