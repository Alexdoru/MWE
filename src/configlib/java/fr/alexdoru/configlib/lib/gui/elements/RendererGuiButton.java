package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.RendererPosition;
import fr.alexdoru.configlib.lib.ConfigFieldContainer;
import fr.alexdoru.configlib.lib.RendererManager;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import fr.alexdoru.configlib.lib.gui.RendererEditGuiScreen;
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
        this.buttonEnabled = new ClickGuiButton(switchButtonWidth, 20, getSwitchButtonText(toggled));
        this.buttonMoveHud = new ClickGuiButton(20, 20);
        this.buttonMoveHud.setTexture(new ResourceLocation("configlib", "move.png"));
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY);
        buttonEnabled.xPosition = drawX + this.getLeftPaddingToCenter(buttonEnabled.width);
        buttonEnabled.yPosition = drawY + this.getTopPaddingToCenter(buttonEnabled.height);
        buttonEnabled.drawButton(colorPalette, mc, mouseX, mouseY);

        buttonMoveHud.xPosition = buttonEnabled.xPosition - buttonMoveHud.width - 1;
        buttonMoveHud.yPosition = buttonEnabled.yPosition;
        buttonMoveHud.drawButton(colorPalette, mc, mouseX, mouseY);

        if (buttonMoveHud.isMouseOver()) {
            this.drawEditHint(colorPalette);
        }
    }

    private void drawEditHint(ColorPalette colorPalette) {
        final String text = "Edit HUD";
        final int PADDING = 3;
        final int GAP_TO_BUTTON = 2;
        final int textWidth = mc.fontRendererObj.getStringWidth(text);
        final int left = buttonMoveHud.xPosition + buttonMoveHud.width / 2 - textWidth / 2 - PADDING;
        final int bottom = buttonMoveHud.yPosition - GAP_TO_BUTTON;
        final int top = bottom - 8 - 2 * PADDING;
        GuiUtil.drawBoxWithOutline(left, top, left + textWidth + 2 * PADDING, bottom, colorPalette.SETTING_BACKGROUND, colorPalette.SETTING_BACKGROUND_BORDER);
        mc.fontRendererObj.drawStringWithShadow(text, left + PADDING, top + PADDING, colorPalette.HUD_BUTTON_HINT_TEXT);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) {
        if (mouseButton.isLeft()) {
            if (buttonEnabled.mousePressed(mouseX, mouseY)) {
                flipBooleanConfig();
                buttonEnabled.displayString = getSwitchButtonText(toggled);
                toggleDependencies(this.parentScreen);
                return true;
            } else if (buttonMoveHud.mousePressed(mouseX, mouseY)) {
                mc.displayGuiScreen(new RendererEditGuiScreen(rendererManager, rendererPosition, parentScreen, field));
                return true;
            }
        }
        return false;
    }

    @Override
    protected int getControlsWidth() {
        return this.getReservedWidth() - this.getLeftPaddingToCenter(buttonEnabled.width) + buttonMoveHud.width + 1;
    }

    private void flipBooleanConfig() {
        rendererPosition.setEnabled(!rendererPosition.isEnabled());
        toggled = rendererPosition.isEnabled();
        invokeConfigEvent();
    }

}
