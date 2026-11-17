package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.ConfigFieldContainer;
import fr.alexdoru.configlib.lib.gui.Box;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.util.regex.Pattern;

public class ColorGuiButton extends OverlayConfigGuiButton {

    private static final int PADDING = 6;
    private static final int GAP = 6;
    private static final int SV_HEIGHT = 100;
    private static final int MIN_SV_WIDTH = 100;
    private static final int BAR_WIDTH = 10;
    private static final int PREVIEW_WIDTH = 20;
    private static final int ROW_HEIGHT = 20;
    private static final int CHECKER_SIZE = 3;
    private static final float HUE_SCROLL_STEP = 1f / 72f;
    private static final int HEX_TEXT_COLOR = 0xE0E0E0;
    private static final int HEX_INVALID_TEXT_COLOR = 0xFF5555;
    private static final Pattern RGB_HEX = Pattern.compile("[0-9a-fA-F]{6}");
    private static final Pattern ARGB_HEX = Pattern.compile("[0-9a-fA-F]{8}");

    private final ConfigGuiScreen parentScreen;
    private final ClickGuiButton button;
    private final ClickGuiButton resetButton;
    private final GuiTextField hexField;
    private final int defaultColor;
    private final boolean hasAlpha;
    private final int contentWidth;
    private int color;
    private int colorOnOpen;
    private float hue, saturation, brightness;
    private int alpha;
    private DragTarget dragTarget = DragTarget.NONE;

    private final Box panelBox = new Box();
    private final Box svBox = new Box();
    private final Box hueBox = new Box();
    private final Box alphaBox = new Box();
    private final Box oldPreviewBox = new Box();
    private final Box newPreviewBox = new Box();
    private final Box hexBox = new Box();

    public ColorGuiButton(ConfigFieldContainer container, ConfigGuiScreen configGuiScreen, int defaultColor) throws IllegalAccessException {
        super(container);
        this.parentScreen = configGuiScreen;
        this.color = (int) this.field.get(null);
        this.defaultColor = defaultColor;
        this.hasAlpha = ((defaultColor >> 24) & 0xFF) != 0;
        this.button = new ClickGuiButton(switchButtonWidth, 20, "Change");
        this.resetButton = new ClickGuiButton(mc.fontRendererObj.getStringWidth("Default") + 12, ROW_HEIGHT, "Default");
        final int hexWidth = mc.fontRendererObj.getStringWidth("#DDDDDDDD") + 10;
        final int barsWidth = BAR_WIDTH + (hasAlpha ? GAP + BAR_WIDTH : 0);
        final int bottomRowWidth = 2 * PREVIEW_WIDTH + GAP + hexWidth + GAP + resetButton.width;
        this.contentWidth = Math.max(MIN_SV_WIDTH + GAP + barsWidth, bottomRowWidth);
        final int hexBoxWidth = contentWidth - 2 * PREVIEW_WIDTH - GAP - GAP - resetButton.width;
        this.hexField = new GuiTextField(0, mc.fontRendererObj, 0, 0, hexBoxWidth - 8, 8);
        this.hexField.setEnableBackgroundDrawing(false);
        this.hexField.setMaxStringLength(9);
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
        this.updateLayout();
        if (dragTarget != DragTarget.NONE) {
            this.updateFromDrag(mouseX, mouseY);
        }
        GuiUtil.drawBoxWithOutline(panelBox, colorPalette.SETTING_BACKGROUND, colorPalette.SETTING_BACKGROUND_BORDER);
        this.drawOverlayTitle(colorPalette, panelBox, panelBox.TOP + PADDING);

        // drawn as two single-axis gradients because GL splits a 4 color quad into two triangles, leaving a diagonal seam
        GuiUtil.drawHorizontalGradient(svBox, 0xFFFFFFFF, Color.HSBtoRGB(hue, 1f, 1f));
        GuiUtil.drawVerticalGradient(svBox, 0x00000000, 0xFF000000);
        drawFrame(svBox, colorPalette);
        final int svX = svBox.LEFT + Math.round(saturation * (svBox.getWidth() - 1));
        final int svY = svBox.TOP + Math.round((1f - brightness) * (svBox.getHeight() - 1));
        GuiUtil.drawOutline(svX - 3, svY - 3, svX + 4, svY + 4, 0xFF000000);
        GuiUtil.drawOutline(svX - 2, svY - 2, svX + 3, svY + 3, 0xFFFFFFFF);

        for (int i = 0; i < 6; i++) {
            final int segmentTop = hueBox.TOP + hueBox.getHeight() * i / 6;
            final int segmentBottom = hueBox.TOP + hueBox.getHeight() * (i + 1) / 6;
            GuiUtil.drawVerticalGradient(hueBox.LEFT, segmentTop, hueBox.RIGHT, segmentBottom, Color.HSBtoRGB(i / 6f, 1f, 1f), Color.HSBtoRGB((i + 1) / 6f, 1f, 1f));
        }
        drawFrame(hueBox, colorPalette);
        drawBarMarker(hueBox, hue);

        if (hasAlpha) {
            final int rgb = this.getRgb();
            GuiUtil.drawCheckerboard(alphaBox, CHECKER_SIZE);
            GuiUtil.drawVerticalGradient(alphaBox, 0xFF000000 | rgb, rgb);
            drawFrame(alphaBox, colorPalette);
            drawBarMarker(alphaBox, 1f - alpha / 255f);
        }

        this.drawPreview(oldPreviewBox, colorOnOpen);
        this.drawPreview(newPreviewBox, color);
        GuiUtil.drawOutline(oldPreviewBox.LEFT - 1, oldPreviewBox.TOP - 1, newPreviewBox.RIGHT + 1, newPreviewBox.BOTTOM + 1, colorPalette.COLOR_BUTTON_INDICATOR_BORDER);

        final boolean hexHovered = hexBox.isMouseInBox(mouseX, mouseY);
        final int hexBackground = hexHovered || hexField.isFocused() ? GuiUtil.brightenColor(colorPalette.BUTTON_BACKGROUND, 0.12f) : colorPalette.BUTTON_BACKGROUND;
        GuiUtil.drawBoxWithOutline(hexBox, hexBackground, colorPalette.BUTTON_BACKGROUND_BORDER);
        hexField.drawTextBox();

        resetButton.drawButton(colorPalette, mc, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) throws IllegalAccessException {
        if (!isOverlayOpen) {
            if (mouseButton.isLeft() && button.mousePressed(mouseX, mouseY)) {
                this.openOverlay();
                return true;
            }
            return false;
        }
        if (!mouseButton.isLeft()) return true;
        if (!panelBox.isMouseInBox(mouseX, mouseY)) {
            this.closeOverlay();
            return true;
        }
        hexField.setFocused(hexBox.isMouseInBox(mouseX, mouseY));
        if (svBox.isMouseInBox(mouseX, mouseY)) {
            dragTarget = DragTarget.SV;
        } else if (hueBox.isMouseInBox(mouseX, mouseY)) {
            dragTarget = DragTarget.HUE;
        } else if (hasAlpha && alphaBox.isMouseInBox(mouseX, mouseY)) {
            dragTarget = DragTarget.ALPHA;
        }
        if (dragTarget != DragTarget.NONE) {
            this.updateFromDrag(mouseX, mouseY);
        } else if (resetButton.mousePressed(mouseX, mouseY)) {
            this.setColor(defaultColor);
            this.refreshHexField();
        } else if (oldPreviewBox.isMouseInBox(mouseX, mouseY)) {
            this.playPressSound();
            this.setColor(colorOnOpen);
            this.refreshHexField();
        }
        return true;
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, MouseButton mouseButton) {
        if (mouseButton.isLeft() && dragTarget != DragTarget.NONE) {
            dragTarget = DragTarget.NONE;
            return true;
        }
        return false;
    }

    @Override
    public void mouseScrolled(int direction) {
        hue = (hue - direction * HUE_SCROLL_STEP + 1f) % 1f;
        this.applyHsb();
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        if (!hexField.isFocused()) return false;
        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            hexField.setFocused(false);
            this.refreshHexField();
            return true;
        }
        if (hexField.textboxKeyTyped(typedChar, keyCode)) {
            this.onHexEdited();
        }
        return true;
    }

    @Override
    public void updateScreen() {
        hexField.updateCursorCounter();
    }

    @Override
    public void closeOverlay() {
        super.closeOverlay();
        dragTarget = DragTarget.NONE;
        hexField.setFocused(false);
    }

    private void openOverlay() {
        isOverlayOpen = true;
        colorOnOpen = color;
        this.loadHsb(color);
        this.refreshHexField();
        this.updateLayout();
    }

    private void updateLayout() {
        final int barsWidth = BAR_WIDTH + (hasAlpha ? GAP + BAR_WIDTH : 0);
        final int panelWidth = contentWidth + 2 * PADDING;
        final int panelHeight = PADDING + mc.fontRendererObj.FONT_HEIGHT + GAP + SV_HEIGHT + GAP + ROW_HEIGHT + PADDING;
        final Box configBox = parentScreen.getConfigBoxSize();
        panelBox.setDim(configBox.LEFT + (configBox.getWidth() - panelWidth) / 2, configBox.TOP + (configBox.getHeight() - panelHeight) / 2, panelWidth, panelHeight);

        final int left = panelBox.LEFT + PADDING;
        final int svTop = panelBox.TOP + PADDING + mc.fontRendererObj.FONT_HEIGHT + GAP;
        svBox.setDim(left, svTop, contentWidth - GAP - barsWidth, SV_HEIGHT);
        hueBox.setDim(svBox.RIGHT + GAP, svTop, BAR_WIDTH, SV_HEIGHT);
        alphaBox.setDim(hueBox.RIGHT + GAP, svTop, BAR_WIDTH, SV_HEIGHT);

        final int rowTop = svBox.BOTTOM + GAP;
        oldPreviewBox.setDim(left, rowTop, PREVIEW_WIDTH, ROW_HEIGHT);
        newPreviewBox.setDim(oldPreviewBox.RIGHT, rowTop, PREVIEW_WIDTH, ROW_HEIGHT);
        resetButton.xPosition = panelBox.RIGHT - PADDING - resetButton.width;
        resetButton.yPosition = rowTop;
        hexBox.setDim(newPreviewBox.RIGHT + GAP, rowTop, resetButton.xPosition - GAP - (newPreviewBox.RIGHT + GAP), ROW_HEIGHT);
        hexField.xPosition = hexBox.LEFT + 4;
        hexField.yPosition = hexBox.TOP + (ROW_HEIGHT - 8) / 2;
    }

    private void updateFromDrag(int mouseX, int mouseY) {
        switch (dragTarget) {
            case SV:
                saturation = getRatio(mouseX, svBox.LEFT, svBox.getWidth());
                brightness = 1f - getRatio(mouseY, svBox.TOP, svBox.getHeight());
                break;
            case HUE:
                hue = getRatio(mouseY, hueBox.TOP, hueBox.getHeight());
                break;
            case ALPHA:
                alpha = Math.round(255f * (1f - getRatio(mouseY, alphaBox.TOP, alphaBox.getHeight())));
                break;
            default:
                return;
        }
        this.applyHsb();
    }

    private void onHexEdited() {
        String text = hexField.getText().trim();
        if (text.startsWith("#")) text = text.substring(1);
        final boolean isRgb = RGB_HEX.matcher(text).matches();
        final boolean isArgb = hasAlpha && ARGB_HEX.matcher(text).matches();
        hexField.setTextColor(isRgb || isArgb ? HEX_TEXT_COLOR : HEX_INVALID_TEXT_COLOR);
        if (isRgb) {
            this.setColor((hasAlpha ? alpha << 24 : 0) | Integer.parseInt(text, 16));
        } else if (isArgb) {
            this.setColor((int) Long.parseLong(text, 16));
        }
    }

    private void refreshHexField() {
        hexField.setText(hasAlpha ? String.format("#%08X", color) : String.format("#%06X", color & 0xFFFFFF));
        hexField.setTextColor(HEX_TEXT_COLOR);
    }

    private void loadHsb(int argb) {
        final float[] hsb = Color.RGBtoHSB(argb >> 16 & 0xFF, argb >> 8 & 0xFF, argb & 0xFF, null);
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
        alpha = argb >>> 24;
    }

    /** HSB is not converted back here, so that colors loaded from a hex value or a reset are stored exactly */
    private void setColor(int newColor) {
        this.loadHsb(newColor);
        this.writeColor(newColor);
    }

    private void applyHsb() {
        this.writeColor((hasAlpha ? alpha << 24 : 0) | this.getRgb());
        if (!hexField.isFocused()) {
            this.refreshHexField();
        }
    }

    private void writeColor(int newColor) {
        if (newColor == color) return;
        color = newColor;
        try {
            field.set(null, newColor);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot set value to : " + field.getName(), e);
        }
        this.invokeConfigEvent();
    }

    private int getRgb() {
        return Color.HSBtoRGB(hue, saturation, brightness) & 0xFFFFFF;
    }

    private void drawPreview(Box box, int previewColor) {
        if (hasAlpha) {
            GuiUtil.drawCheckerboard(box, CHECKER_SIZE);
            GuiUtil.drawRect(box, previewColor);
        } else {
            GuiUtil.drawRect(box, 0xFF000000 | previewColor);
        }
    }

    private static void drawFrame(Box box, ColorPalette colorPalette) {
        GuiUtil.drawOutline(box.LEFT - 1, box.TOP - 1, box.RIGHT + 1, box.BOTTOM + 1, colorPalette.COLOR_BUTTON_INDICATOR_BORDER);
    }

    private static void drawBarMarker(Box bar, float ratio) {
        final int y = bar.TOP + Math.round(ratio * (bar.getHeight() - 1));
        Gui.drawRect(bar.LEFT - 1, y - 1, bar.RIGHT + 1, y + 2, 0xFF000000);
        Gui.drawRect(bar.LEFT, y, bar.RIGHT, y + 1, 0xFFFFFFFF);
    }

    private static float getRatio(int mousePos, int start, int length) {
        return MathHelper.clamp_float((mousePos - start) / (float) (length - 1), 0f, 1f);
    }

    private enum DragTarget {
        NONE, SV, HUE, ALPHA
    }

}
