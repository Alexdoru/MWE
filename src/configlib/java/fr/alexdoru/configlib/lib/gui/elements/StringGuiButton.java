package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.ConfigFieldContainer;
import fr.alexdoru.configlib.lib.gui.Box;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import fr.alexdoru.configlib.lib.gui.elements.ClickGuiButton.FormattingCodeButton;
import fr.alexdoru.configlib.lib.utils.FormattingCodeUtil;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class StringGuiButton extends OverlayConfigGuiButton {

    private static final int SQUARE = 16;
    private static final int GAP = 4;
    private static final int COLUMNS = 11;
    private static final int PANEL_WIDTH = COLUMNS * SQUARE + (COLUMNS + 1) * GAP;
    private static final int FIELD_HEIGHT = 20;
    private static final int BUTTON_HEIGHT = 20;
    private static final int MAX_BUTTON_WIDTH_FACTOR = 2;
    private static final String HINT = EnumChatFormatting.GRAY + "Use & to type formatting codes";

    private final ConfigGuiScreen parentScreen;
    private final String defaultValue;
    private final boolean formatted;
    private final ClickGuiButton button;
    private final GuiTextField textField;
    private final List<FormattingCodeButton> codeButtons = new ArrayList<>();
    private final ClickGuiButton resetButton;
    private final ClickGuiButton doneButton;
    private final Box panelBox = new Box();
    private String value;

    public StringGuiButton(ConfigFieldContainer container, ConfigGuiScreen parentScreen, String defaultValue, boolean formatted) throws IllegalAccessException {
        super(container);
        this.parentScreen = parentScreen;
        this.defaultValue = defaultValue;
        this.formatted = formatted;
        this.button = new ClickGuiButton(switchButtonWidth, 20);
        this.textField = new GuiTextField(0, mc.fontRendererObj, 0, 0, PANEL_WIDTH - 2 * GAP, FIELD_HEIGHT);
        this.textField.setMaxStringLength(256);
        this.textField.setCanLoseFocus(false);
        this.textField.setFocused(true);
        if (formatted) {
            for (final EnumChatFormatting formatting : EnumChatFormatting.values()) {
                this.codeButtons.add(new FormattingCodeButton(SQUARE, formatting));
            }
        }
        final int bottomButtonWidth = (PANEL_WIDTH - 3 * GAP) / 2;
        this.resetButton = new ClickGuiButton(bottomButtonWidth, BUTTON_HEIGHT, "Reset");
        this.doneButton = new ClickGuiButton(bottomButtonWidth, BUTTON_HEIGHT, "Done");
        this.setValue((String) this.field.get(null));
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY);
        button.xPosition = drawX + this.getLeftPaddingToCenter(button.width);
        button.yPosition = drawY + this.getTopPaddingToCenter(button.height);
        button.drawButton(colorPalette, mc, mouseX, mouseY);
    }

    @Override
    protected int getControlsWidth() {
        return this.getReservedWidth() - this.getLeftPaddingToCenter(button.width);
    }

    @Override
    public void drawOverlay(ColorPalette colorPalette, int mouseX, int mouseY) {
        final int fontHeight = mc.fontRendererObj.FONT_HEIGHT;
        final int rows = (codeButtons.size() + COLUMNS - 1) / COLUMNS;
        final int paletteHeight = rows * SQUARE + (rows - 1) * GAP;
        final int formattingHeight = formatted ? fontHeight + GAP + paletteHeight + GAP + fontHeight + GAP : 0;
        final int panelHeight = GAP + fontHeight + GAP + FIELD_HEIGHT + GAP + formattingHeight + BUTTON_HEIGHT + GAP;
        final Box configBox = this.parentScreen.getConfigBoxSize();
        panelBox.LEFT = configBox.LEFT + (configBox.getWidth() - PANEL_WIDTH) / 2;
        panelBox.TOP = configBox.TOP + (configBox.getHeight() - panelHeight) / 2;
        panelBox.RIGHT = panelBox.LEFT + PANEL_WIDTH;
        panelBox.BOTTOM = panelBox.TOP + panelHeight;
        GuiUtil.drawBoxWithOutline(panelBox, colorPalette.SETTING_BACKGROUND, colorPalette.SETTING_BACKGROUND_BORDER);

        final int left = panelBox.LEFT + GAP;
        final int innerWidth = PANEL_WIDTH - 2 * GAP;
        final int centerX = panelBox.LEFT + PANEL_WIDTH / 2;
        int y = panelBox.TOP + GAP;

        final int titleY = y;
        y += fontHeight + GAP;

        textField.xPosition = left;
        textField.yPosition = y;
        textField.drawTextBox();
        y += FIELD_HEIGHT + GAP;

        EnumChatFormatting hovered = null;
        if (formatted) {
            final String preview = FormattingCodeUtil.toFormattedText(textField.getText());
            mc.fontRendererObj.drawStringWithShadow(mc.fontRendererObj.trimStringToWidth(preview, innerWidth), left, y, 0xFFFFFFFF);
            y += fontHeight + GAP;

            for (int i = 0; i < codeButtons.size(); i++) {
                final FormattingCodeButton codeButton = codeButtons.get(i);
                codeButton.xPosition = left + (i % COLUMNS) * (SQUARE + GAP);
                codeButton.yPosition = y + (i / COLUMNS) * (SQUARE + GAP);
                codeButton.drawButton(colorPalette, mc, mouseX, mouseY);
                if (codeButton.isMouseOver()) hovered = codeButton.getFormatting();
            }
            y += paletteHeight + GAP;

            GuiUtil.drawCenteredString(HINT, centerX, y, 0xFFFFFFFF);
            y += fontHeight + GAP;
        }

        resetButton.xPosition = left;
        resetButton.yPosition = y;
        resetButton.drawButton(colorPalette, mc, mouseX, mouseY);
        doneButton.xPosition = panelBox.RIGHT - GAP - doneButton.width;
        doneButton.yPosition = y;
        doneButton.drawButton(colorPalette, mc, mouseX, mouseY);

        final String title;
        if (hovered != null) {
            title = EnumChatFormatting.WHITE + "&" + FormattingCodeUtil.getCode(hovered) + " " + hovered + hovered.name();
        } else {
            title = annotation.name();
        }
        GuiUtil.drawCenteredString(title, centerX, titleY, colorPalette.SETTING_NAME_TEXT);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) throws IllegalAccessException {
        if (isOverlayOpen) {
            if (!panelBox.isMouseInBox(mouseX, mouseY)) {
                this.closeOverlay();
                return true;
            }
            if (!mouseButton.isLeft()) return true;
            textField.mouseClicked(mouseX, mouseY, 0);
            for (final FormattingCodeButton codeButton : this.codeButtons) {
                if (codeButton.mousePressed(mouseX, mouseY)) {
                    textField.writeText("&" + FormattingCodeUtil.getCode(codeButton.getFormatting()));
                    return true;
                }
            }
            if (resetButton.mousePressed(mouseX, mouseY)) {
                textField.setText(this.toEditorText(defaultValue));
            } else if (doneButton.mousePressed(mouseX, mouseY)) {
                this.saveAndClose();
            }
            return true;
        }
        if (mouseButton.isLeft() && button.mousePressed(mouseX, mouseY)) {
            textField.setText(this.toEditorText(value));
            textField.setCursorPositionEnd();
            isOverlayOpen = true;
            return true;
        }
        if (mouseButton.isRight() && button.mousePressed(mouseX, mouseY)) {
            this.setNewValue(defaultValue);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            this.playPressSound();
            try {
                this.saveAndClose();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return textField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        textField.updateCursorCounter();
    }

    private void saveAndClose() throws IllegalAccessException {
        this.setNewValue(this.fromEditorText(textField.getText()));
        this.closeOverlay();
    }

    private String toEditorText(String text) {
        return formatted ? FormattingCodeUtil.toAmpersandText(text) : text;
    }

    private String fromEditorText(String text) {
        return formatted ? FormattingCodeUtil.toFormattedText(text) : text;
    }

    private void setValue(String newValue) {
        this.value = newValue;
        final int maxTextWidth = this.getMaxButtonWidth() - mc.fontRendererObj.getStringWidth("  ");
        if (newValue.isEmpty()) {
            button.displayString = EnumChatFormatting.DARK_GRAY + "<empty>";
        } else if (mc.fontRendererObj.getStringWidth(newValue) > maxTextWidth) {
            button.displayString = mc.fontRendererObj.trimStringToWidth(newValue, maxTextWidth - mc.fontRendererObj.getStringWidth("...")) + EnumChatFormatting.RESET + "...";
        } else {
            button.displayString = newValue;
        }
        button.width = Math.max(switchButtonWidth, mc.fontRendererObj.getStringWidth("  " + button.displayString));
    }

    private void setNewValue(String newValue) throws IllegalAccessException {
        if (newValue.equals(this.value)) return;
        this.setValue(newValue);
        this.resizeCommentLines();
        this.field.set(null, newValue);
        this.invokeConfigEvent();
    }

    private int getMaxButtonWidth() {
        return MAX_BUTTON_WIDTH_FACTOR * switchButtonWidth;
    }

}
