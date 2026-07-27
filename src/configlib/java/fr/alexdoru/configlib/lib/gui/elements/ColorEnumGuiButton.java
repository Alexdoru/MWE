package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.lib.gui.Box;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ColorEnumGuiButton extends OverlayConfigGuiButton {

    private static final int SQUARE = 18;
    private static final int GAP = 4;

    private final ConfigGuiScreen parentScreen;
    private final ClickGuiButton button;
    private final List<ColorSquareButton> colorButtons = new ArrayList<>();
    private final Box panelBox = new Box();
    private final int defaultColorIndex;
    private int lastSavedColorIndex;
    private int color;
    private int selectedColorIndex;

    // don't make this 'final', they should only be constructed when needed and set to null after (much cheaper)
    private ClickGuiButton doneButton;
    private ClickGuiButton resetButton;
    private ClickGuiButton undoButton;

    public ColorEnumGuiButton(ConfigGuiScreen parentScreen, Field field, Method event, ConfigProperty annotation, EnumChatFormatting defaultColor) throws IllegalAccessException {
        super(field, event, annotation);
        this.parentScreen = parentScreen;
        int maxWidth = 0;
        for (final EnumChatFormatting c : EnumChatFormatting.values()) {
            if (c.isColor()) {
                maxWidth = Math.max(maxWidth, mc.fontRendererObj.getStringWidth("  " + c.name()));
                this.colorButtons.add(new ColorSquareButton(c));
            }
        }
        this.defaultColorIndex = defaultColor.getColorIndex();
        this.button = new ClickGuiButton(0, 0, 0, maxWidth, 20, "");
        this.setValue((EnumChatFormatting) this.field.get(null));
        this.lastSavedColorIndex = this.selectedColorIndex;
        this.rightSideContentWidth = button.width + BUTTON_RIGHT_MARGIN + 1 + button.height; // button.height == color_box_size
        this.rightSideContentHeight = button.height;
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY, boolean canMouseBeVisuallyOverElement) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY, canMouseBeVisuallyOverElement);
        final int top = drawY + getCenterYOffset(button.height);
        final int colorBoxSize = button.height;
        GuiUtil.drawBoxWithOutline(contentLeft, top, contentLeft + colorBoxSize, top + colorBoxSize, 255 << 24 | color, colorPalette.COLOR_BUTTON_INDICATOR_BORDER);
        button.xPosition = contentLeft + 1 + colorBoxSize;
        button.yPosition = top;
        button.drawButton(colorPalette, mc, mouseX, mouseY, canMouseBeVisuallyOverElement);
    }

    @Override
    public void drawOverlay(ColorPalette colorPalette, int mouseX, int mouseY) {
        final Box configBox = this.parentScreen.getConfigBoxSize();
        final int panelWidth = 8 * SQUARE + 9 * GAP;
        final int panelHeight = GAP + mc.fontRendererObj.FONT_HEIGHT + GAP + SQUARE + GAP + SQUARE + 6 + doneButton.height + GAP;
        panelBox.LEFT = configBox.LEFT + (configBox.getWidth() - panelWidth) / 2;
        panelBox.TOP = configBox.TOP + (configBox.getHeight() - panelHeight) / 2;
        panelBox.RIGHT = panelBox.LEFT + panelWidth;
        panelBox.BOTTOM = panelBox.TOP + panelHeight;
        GuiUtil.drawBoxWithOutline(panelBox.LEFT, panelBox.TOP, panelBox.RIGHT, panelBox.BOTTOM, colorPalette.SETTING_BACKGROUND, colorPalette.SETTING_BACKGROUND_BORDER);

        EnumChatFormatting hovered = null;
        final int startX = panelBox.LEFT + GAP;
        final int startY = panelBox.TOP + GAP + mc.fontRendererObj.FONT_HEIGHT + GAP;
        final List<ColorSquareButton> buttons = this.colorButtons;
        for (int i = 0; i < buttons.size(); i++) {
            final ColorSquareButton colorButton = buttons.get(i);
            final int row = i / 8;
            final int col = i % 8;
            colorButton.xPosition = startX + col * (SQUARE + GAP);
            colorButton.yPosition = startY + row * (SQUARE + GAP);
            colorButton.drawButton(mc, mouseX, mouseY);
            if (this.selectedColorIndex == i) {
                GuiUtil.drawOutline(colorButton.xPosition-1, colorButton.yPosition-1, colorButton.xPosition + colorButton.width + 1, colorButton.yPosition + colorButton.height + 1, 0xFF3C6EFF);
            }
            if (colorButton.isMouseOver()) hovered = colorButton.color;
        }

        if (hovered != null) {
            final String titleText = hovered + hovered.name();
            final int titleX = panelBox.LEFT + panelWidth / 2;
            final int titleY = panelBox.TOP + GAP;
            GuiUtil.drawCenteredString(titleText, titleX, titleY, 0xFFFFFFFF);
        }

        final int buttonsTop = panelBox.BOTTOM - GAP - doneButton.height;
        final int totalButtonsWidth = resetButton.width + GAP + doneButton.width + GAP + undoButton.width;
        resetButton.xPosition = panelBox.LEFT + (panelWidth - totalButtonsWidth) / 2;
        resetButton.yPosition = buttonsTop;
        resetButton.drawButton(colorPalette, mc, mouseX, mouseY);

        doneButton.xPosition = resetButton.xPosition + resetButton.width + GAP;
        doneButton.yPosition = buttonsTop;
        doneButton.drawButton(colorPalette, mc, mouseX, mouseY);

        undoButton.xPosition = doneButton.xPosition + doneButton.width + GAP;
        undoButton.yPosition = buttonsTop;
        undoButton.drawButton(colorPalette, mc, mouseX, mouseY);
    }

    @Override
    public List<String> getOverlayHoveringTextLines() {
        if (resetButton.isMouseOver() && resetButton.hasHoveringText()) {
            return resetButton.getHoveringTextLines();
        }
        else if (undoButton.isMouseOver() && undoButton.hasHoveringText()) {
            return undoButton.getHoveringTextLines();
        }
        return null;
    }

    @Override
    public boolean isMouseOverOverlay(int mouseX, int mouseY) {
        return panelBox.isMouseInBox(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) throws IllegalAccessException {
        if (mouseButton.isLeft() && button.mousePressed(mc, mouseX, mouseY)) {
            button.playPressSound(mc.getSoundHandler());
            // only do work if the overlay is closed.
            // if its open only play sound
            if (!isOverlayOpen()) {
                this.doneButton = new ClickGuiButton(-1, 0, 0, getMainButtonWidth(), 14, "Done");
                final int ICON_BUTTON_SIZE = doneButton.height;
                this.resetButton = new ClickGuiButton(-1, 0, 0, ICON_BUTTON_SIZE, ICON_BUTTON_SIZE, "");
                this.resetButton.setTexture(new ResourceLocation("configlib", "reload.png"));
                this.resetButton.setHoveringText("Reset to Default Position");
                this.undoButton = new ClickGuiButton(-1, 0, 0, ICON_BUTTON_SIZE, ICON_BUTTON_SIZE, "");
                this.undoButton.setTexture(new ResourceLocation("configlib", "undo.png"));
                this.undoButton.setHoveringText("Undo Changes");
                isOverlayOpen = true;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClickedOnOverlay(int mouseX, int mouseY, MouseButton button) throws IllegalAccessException {
        if (isMouseOverOverlay(mouseX, mouseY)) {
            if (button.isLeft()) {
                for (final ColorSquareButton colorButton : this.colorButtons) {
                    if (colorButton.mousePressed(mc, mouseX, mouseY)) {
                        handleButtonClick(colorButton);
                        return true;
                    }
                }
                if (doneButton.mousePressed(mc, mouseX, mouseY)) {
                    closeOverlay();
                }
                else if (resetButton.mousePressed(mc, mouseX, mouseY)) {
                    handleButtonClick(colorButtons.get(defaultColorIndex));
                }
                else if (undoButton.mousePressed(mc, mouseX, mouseY)) {
                    handleButtonClick(colorButtons.get(lastSavedColorIndex));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean handleOverlayMouseInput() {
        if (Mouse.getEventDWheel() != 0) {
            final int mouseX = Mouse.getEventX() * parentScreen.width / mc.displayWidth;
            final int mouseY = parentScreen.height - Mouse.getEventY() * parentScreen.height / mc.displayHeight - 1;
            return parentScreen.getConfigBoxSize().isMouseInBox(mouseX, mouseY); // don't allow 'config box scroll'
        }
        return false;
    }

    @Override
    public boolean overlayKeyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            closeOverlay();
        }
        return true;
    }

    private void setValue(EnumChatFormatting v) {
        this.color = mc.fontRendererObj.getColorCode(v.toString().charAt(1));
        this.button.displayString = v + v.name();
        this.selectedColorIndex = v.getColorIndex();
    }

    private void handleButtonClick(ColorSquareButton button) throws IllegalAccessException {
        button.playPressSound(mc.getSoundHandler());
        this.field.set(null, button.color);
        this.setValue(button.color);
        invokeConfigEvent();
    }

    @Override
    public void closeOverlay() {
        super.closeOverlay();
        this.lastSavedColorIndex = this.selectedColorIndex;
        this.doneButton = null;
        this.resetButton = null;
        this.undoButton = null;
    }

    private static class ColorSquareButton extends GuiButton {

        private final EnumChatFormatting color;

        private ColorSquareButton(EnumChatFormatting color) {
            super(0, 0, 0, SQUARE, SQUARE, "");
            this.color = color;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            final int argb = 255 << 24 | mc.fontRendererObj.getColorCode(color.toString().charAt(1));
            hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
            GuiUtil.drawBoxWithOutline(
                    xPosition, yPosition,
                    xPosition + width, yPosition + height,
                    argb,
                    hovered ? Color.WHITE.getRGB() : Color.BLACK.getRGB()
            );
        }

    }
}
