package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.api.IRenderer;
import fr.alexdoru.configlib.api.RendererPosition;
import fr.alexdoru.configlib.lib.RendererManager;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.RendererEditGuiScreen;
import net.minecraft.util.EnumChatFormatting;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RendererGuiButton extends ConfigGuiButton {

    private final ConfigGuiScreen parentScreen;
    private final RendererManager rendererManager;
    private final RendererPosition rendererPosition;
    private boolean toggled;
    private final ClickGuiButton buttonEnabled;
    private final ClickGuiButton buttonMoveHud;

    public RendererGuiButton(
            ConfigGuiScreen configGuiScreen,
            RendererManager rendererManager,
            Field field,
            Method event,
            ConfigProperty annotation) throws IllegalAccessException {
        super(field, event, annotation);
        this.parentScreen = configGuiScreen;
        this.rendererManager = rendererManager;
        this.rendererPosition = ((RendererPosition) field.get(null));
        this.toggled = this.rendererPosition.isEnabled();
        final int btnWidth = mc.fontRendererObj.getStringWidth(" Disabled ");
        this.buttonEnabled = new ClickGuiButton(0, 0, 0, btnWidth, 20, getButtonText());
        this.buttonMoveHud = new ClickGuiButton(0, 0, 0, btnWidth, 20, "Position");
    }

    @Override
    public void setBoxWidth(int boxWidth) {
        super.setBoxWidth(boxWidth - mc.fontRendererObj.getStringWidth("Reset Position") - 10);
        this.boxWidth = boxWidth;
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY);
        buttonEnabled.xPosition = drawX + boxWidth - buttonEnabled.width - 20;
        buttonEnabled.yPosition = drawY + 8;
        buttonEnabled.drawButton(colorPalette, mc, mouseX, mouseY);
        buttonMoveHud.xPosition = buttonEnabled.xPosition;
        buttonMoveHud.yPosition = buttonEnabled.yPosition + buttonEnabled.height + 1;
        buttonMoveHud.drawButton(colorPalette, mc, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            if (buttonEnabled.mousePressed(mc, mouseX, mouseY)) {
                flipBooleanConfig();
                buttonEnabled.displayString = getButtonText();
                buttonEnabled.playPressSound(mc.getSoundHandler());
                return true;
            } else if (buttonMoveHud.mousePressed(mc, mouseX, mouseY)) {
                buttonEnabled.playPressSound(mc.getSoundHandler());
                final IRenderer renderer = this.rendererManager.getRendererFromPosition(rendererPosition);
                if (renderer != null) {
                    mc.displayGuiScreen(new RendererEditGuiScreen(this.rendererManager, renderer, parentScreen, parentScreen.getColorPalette()));
                } else {
                    throw new RuntimeException("No registered renderer associated to " + field.getName());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int getHeight() {
        return Math.max(super.getHeight(), 8 + buttonEnabled.height + 1 + buttonMoveHud.height + 8 - 1);
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
