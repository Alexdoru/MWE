package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.ConfigFieldContainer;
import fr.alexdoru.configlib.lib.gui.Box;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import fr.alexdoru.configlib.lib.gui.elements.ClickGuiButton.FormattingCodeButton;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class ColorEnumGuiButton extends OverlayConfigGuiButton {

    private static final int SQUARE = 18;
    private static final int GAP = 4;

    private final ConfigGuiScreen parentScreen;
    private final ClickGuiButton button;
    private final List<FormattingCodeButton> colorButtons = new ArrayList<>();
    private final Box panelBox = new Box();
    private int color;

    public ColorEnumGuiButton(ConfigFieldContainer container, ConfigGuiScreen parentScreen) throws IllegalAccessException {
        super(container);
        this.parentScreen = parentScreen;
        int maxWidth = 0;
        for (final EnumChatFormatting c : EnumChatFormatting.values()) {
            if (c.isColor()) {
                maxWidth = Math.max(maxWidth, mc.fontRendererObj.getStringWidth("  " + c.name()));
                this.colorButtons.add(new FormattingCodeButton(SQUARE, c));
            }
        }
        this.button = new ClickGuiButton(maxWidth, 20);
        this.setValue((EnumChatFormatting) this.field.get(null));
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY);
        button.xPosition = drawX + this.getLeftPaddingToCenter(button.width);
        button.yPosition = drawY + this.getTopPaddingToCenter(button.height);
        button.drawButton(colorPalette, mc, mouseX, mouseY);
        final int left = button.xPosition - 20 - 1;
        final int top = button.yPosition;
        GuiUtil.drawBoxWithOutline(left, top, left + 20, top + 20, 255 << 24 | color, colorPalette.COLOR_BUTTON_INDICATOR_BORDER);
    }

    @Override
    protected int getControlsWidth() {
        return this.getReservedWidth() - this.getLeftPaddingToCenter(button.width) + 20 + 1;
    }

    @Override
    public void drawOverlay(ColorPalette colorPalette, int mouseX, int mouseY) {
        final Box configBox = this.parentScreen.getConfigBoxSize();
        final int panelWidth = 8 * SQUARE + 9 * GAP;
        final int panelHeight = GAP + SQUARE + GAP + SQUARE + GAP + mc.fontRendererObj.FONT_HEIGHT + GAP;
        panelBox.setDim(configBox.LEFT + (configBox.getWidth() - panelWidth) / 2, configBox.TOP + (configBox.getHeight() - panelHeight) / 2, panelWidth, panelHeight);
        GuiUtil.drawBoxWithOutline(panelBox, colorPalette.SETTING_BACKGROUND, colorPalette.SETTING_BACKGROUND_BORDER);

        EnumChatFormatting hovered = null;
        final int startX = panelBox.LEFT + GAP;
        final int startY = panelBox.TOP + GAP + mc.fontRendererObj.FONT_HEIGHT + GAP;
        final List<FormattingCodeButton> buttons = this.colorButtons;
        for (int i = 0; i < buttons.size(); i++) {
            final FormattingCodeButton colorButton = buttons.get(i);
            final int row = i / 8;
            final int col = i % 8;
            colorButton.xPosition = startX + col * (SQUARE + GAP);
            colorButton.yPosition = startY + row * (SQUARE + GAP);
            colorButton.drawButton(colorPalette, mc, mouseX, mouseY);
            if (colorButton.isMouseOver()) hovered = colorButton.getFormatting();
        }

        if (hovered != null) {
            this.drawOverlayTitle(hovered + hovered.name(), panelBox, panelBox.TOP + GAP);
        } else {
            this.drawOverlayTitle(colorPalette, panelBox, panelBox.TOP + GAP);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) throws IllegalAccessException {
        if (!mouseButton.isLeft()) return false;
        if (isOverlayOpen) {
            if (panelBox.isMouseInBox(mouseX, mouseY)) {
                for (final FormattingCodeButton colorButton : this.colorButtons) {
                    if (colorButton.mousePressed(mouseX, mouseY)) {
                        this.setNewValue(colorButton.getFormatting());
                        return true;
                    }
                }
            } else {
                this.closeOverlay();
            }
            return true;
        }
        if (button.mousePressed(mouseX, mouseY)) {
            isOverlayOpen = true;
            return true;
        }
        return false;
    }

    private void setValue(EnumChatFormatting v) {
        this.color = mc.fontRendererObj.getColorCode(v.toString().charAt(1));
        this.button.displayString = v + v.name();
    }

    private void setNewValue(EnumChatFormatting v) throws IllegalAccessException {
        this.setValue(v);
        this.field.set(null, v);
        this.invokeConfigEvent();
    }

}
