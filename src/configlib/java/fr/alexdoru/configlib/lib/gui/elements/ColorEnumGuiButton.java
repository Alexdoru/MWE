package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.lib.gui.Box;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

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
    private int color;

    public ColorEnumGuiButton(ConfigGuiScreen parentScreen, Field field, Method event, ConfigProperty annotation) throws IllegalAccessException {
        super(field, event, annotation);
        this.parentScreen = parentScreen;
        int maxWidth = 0;
        for (final EnumChatFormatting c : EnumChatFormatting.values()) {
            if (c.isColor()) {
                maxWidth = Math.max(maxWidth, mc.fontRendererObj.getStringWidth("  " + c.name()));
                this.colorButtons.add(new ColorSquareButton(c));
            }
        }
        this.button = new ClickGuiButton(0, 0, 0, maxWidth, 20, "");
        this.setValue((EnumChatFormatting) this.field.get(null));
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY);
        button.xPosition = drawX + boxWidth - button.width - 20;
        button.yPosition = drawY + (this.hasComment() ? 8 + mc.fontRendererObj.FONT_HEIGHT / 2 : (getHeight() - button.height) / 2);
        button.drawButton(colorPalette, mc, mouseX, mouseY);
        final int left = button.xPosition - 20 - 1;
        final int top = button.yPosition;
        GuiUtil.drawBoxWithOutline(left, top, left + 20, top + 20, 255 << 24 | color, colorPalette.COLOR_BUTTON_INDICATOR_BORDER);
        if (isOverlayOpen) {
            GlStateManager.translate(0, 0, 200);

            final Box configBox = this.parentScreen.getConfigBoxSize();
            final int panelWidth = 8 * SQUARE + 9 * GAP;
            final int panelHeight = GAP + SQUARE + GAP + SQUARE + GAP + mc.fontRendererObj.FONT_HEIGHT + GAP;
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
                if (colorButton.isMouseOver()) hovered = colorButton.color;
            }

            if (hovered != null) {
                final String titleText = hovered + hovered.name();
                final int titleX = panelBox.LEFT + panelWidth / 2;
                final int titleY = panelBox.TOP + GAP;
                GuiUtil.drawCenteredString(titleText, titleX, titleY, 0xFFFFFFFF);
            }

            GlStateManager.translate(0, 0, -200);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) throws IllegalAccessException {
        if (!mouseButton.isLeft()) return false;
        if (isOverlayOpen) {
            if (panelBox.isMouseInBox(mouseX, mouseY)) {
                for (final ColorSquareButton colorButton : this.colorButtons) {
                    if (colorButton.isMouseOver()) {
                        button.playPressSound(mc.getSoundHandler());
                        this.setNewValue(colorButton.color);
                        return true;
                    }
                }
            } else {
                isOverlayOpen = false;
            }
            return true;
        }
        if (button.mousePressed(mc, mouseX, mouseY)) {
            button.playPressSound(mc.getSoundHandler());
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
