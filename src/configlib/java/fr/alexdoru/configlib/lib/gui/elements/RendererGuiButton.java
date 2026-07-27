package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.api.RendererPosition;
import fr.alexdoru.configlib.lib.RendererManager;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import fr.alexdoru.configlib.lib.gui.RendererEditGuiScreen;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

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
        this.buttonEnabled = getMainButton(getBooleanText(toggled));
        final int moveHudBtnSize = buttonEnabled.height;
        this.buttonMoveHud = new ClickGuiButton(0, 0, 0, moveHudBtnSize, moveHudBtnSize, "");
        this.buttonMoveHud.setTexture(new ResourceLocation("configlib", "move.png"));
        this.buttonMoveHud.setHoveringText("Move HUD");
        this.rightSideContentWidth = buttonEnabled.width + BUTTON_RIGHT_MARGIN + 1 + buttonMoveHud.width;
        this.rightSideContentHeight = buttonEnabled.height;
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY, boolean canMouseBeVisuallyOverElement) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY, canMouseBeVisuallyOverElement);

        final int top = drawY + getCenterYOffset(buttonEnabled.height);

        buttonMoveHud.xPosition = contentLeft;
        buttonMoveHud.yPosition = top;
        buttonMoveHud.drawButton(colorPalette, mc, mouseX, mouseY, canMouseBeVisuallyOverElement);

        buttonEnabled.xPosition = buttonMoveHud.xPosition + buttonMoveHud.width + 1;
        buttonEnabled.yPosition = top;
        buttonEnabled.drawButton(colorPalette, mc, mouseX, mouseY, canMouseBeVisuallyOverElement);
    }

    @Override
    public List<String> getHoveringTextLines() {
        return (buttonMoveHud.isMouseOver() && buttonMoveHud.hasHoveringText()) ? buttonMoveHud.getHoveringTextLines() : null;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) {
        if (mouseButton.isLeft()) {
            if (buttonEnabled.mousePressed(mc, mouseX, mouseY)) {
                flipBooleanConfig();
                buttonEnabled.displayString = getBooleanText(toggled);
                buttonEnabled.playPressSound(mc.getSoundHandler());
                return true;
            } else if (buttonMoveHud.mousePressed(mc, mouseX, mouseY)) {
                buttonEnabled.playPressSound(mc.getSoundHandler());
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
}