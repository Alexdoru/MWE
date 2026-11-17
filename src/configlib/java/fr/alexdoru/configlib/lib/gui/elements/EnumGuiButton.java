package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.ConfigFieldContainer;
import fr.alexdoru.configlib.lib.gui.*;
import fr.alexdoru.configlib.lib.utils.EnumUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

public class EnumGuiButton extends OverlayConfigGuiButton {

    private static final int ROW_HEIGHT = 12;
    private static final int MAX_VISIBLE_ROWS = 8;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int BORDER = 1;

    private final ConfigGuiScreen parentScreen;
    protected final ClickGuiButton button;
    protected final Enum<?>[] values;
    private final String[] displayNames;
    protected Enum<?> value;
    private final Box dropdownBox = new Box();
    private final Box rowsBox = new Box();
    private final Scrollbar scrollbar = new Scrollbar(BORDER);

    public EnumGuiButton(ConfigFieldContainer container, ConfigGuiScreen parentScreen) throws IllegalAccessException {
        super(container);
        this.parentScreen = parentScreen;
        values = (Enum<?>[]) this.field.getType().getEnumConstants();
        this.displayNames = new String[values.length];
        int width = 0;
        for (int i = 0; i < values.length; i++) {
            final String displayName = EnumUtil.formatEnumName(values[i]);
            displayNames[i] = displayName;
            width = Math.max(width, 2 * 4 + mc.fontRendererObj.getStringWidth(displayName));
        }
        this.button = new ClickGuiButton(width, 20);
        this.setValue((Enum<?>) this.field.get(null));
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
        this.updateDropdownBox();
        final int contentHeight = this.getContentHeight();
        scrollbar.updateScrollPos(contentHeight, dropdownBox.getHeight());
        GuiUtil.drawBoxWithOutline(dropdownBox, colorPalette.SETTING_BACKGROUND, colorPalette.SETTING_BACKGROUND_BORDER);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        dropdownBox.applyScissors(mc, new ScaledResolution(mc), BORDER);

        final int hoveredIndex = this.getHoveredIndex(mouseX, mouseY);
        int rowTop = rowsBox.TOP - scrollbar.getScroll();
        for (int i = 0; i < values.length; i++, rowTop += ROW_HEIGHT) {
            if (rowTop + ROW_HEIGHT <= rowsBox.TOP || rowTop >= rowsBox.BOTTOM) continue;
            if (i == hoveredIndex) {
                Gui.drawRect(rowsBox.LEFT, rowTop, rowsBox.RIGHT, rowTop + ROW_HEIGHT, GuiUtil.brightenColor(colorPalette.BUTTON_BACKGROUND, 0.12f));
            }
            final String text = (values[i] == value ? EnumChatFormatting.GOLD : "") + displayNames[i];
            GuiUtil.drawCenteredString(text, (rowsBox.LEFT + rowsBox.RIGHT) / 2, rowTop + (ROW_HEIGHT - 8) / 2, colorPalette.BUTTON_TEXT);
        }

        scrollbar.drawScrollbar(colorPalette, dropdownBox, mouseY, contentHeight);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) throws IllegalAccessException {
        if (!mouseButton.isLeft()) return false;
        if (isOverlayOpen) {
            if (dropdownBox.isMouseInBox(mouseX, mouseY)) {
                if (!scrollbar.mouseClicked(mouseX, mouseY, mouseButton)) {
                    final int index = this.getHoveredIndex(mouseX, mouseY);
                    if (index != -1) {
                        this.playPressSound();
                        this.setNewValue(values[index]);
                        this.closeOverlay();
                    }
                }
            } else {
                this.closeOverlay();
            }
            return true;
        }
        if (button.mousePressed(mouseX, mouseY)) {
            this.openDropdown();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, MouseButton mouseButton) {
        scrollbar.mouseReleased(mouseButton);
        return false;
    }

    @Override
    public void mouseScrolled(int direction) {
        scrollbar.scheduleScroll(direction, 3 * ROW_HEIGHT);
    }

    protected void setValue(Enum<?> v) {
        this.value = v;
        this.button.displayString = EnumChatFormatting.GOLD + displayNames[v.ordinal()];
    }

    private void setNewValue(Enum<?> v) throws IllegalAccessException {
        this.setValue(v);
        this.field.set(null, v);
        this.invokeConfigEvent();
    }

    private void openDropdown() {
        this.isOverlayOpen = true;
        this.updateDropdownBox();
        final int visibleRows = (dropdownBox.getHeight() - 2 * BORDER) / ROW_HEIGHT;
        scrollbar.resetScroll();
        scrollbar.init((value.ordinal() - visibleRows / 2) * ROW_HEIGHT, this.getContentHeight(), dropdownBox.getHeight());
    }

    private void updateDropdownBox() {
        final Box configBox = parentScreen.getConfigBoxSize();
        final int maxVisibleRows = Math.min(values.length, MAX_VISIBLE_ROWS);
        final int fullHeight = maxVisibleRows * ROW_HEIGHT + 2 * BORDER;
        final int spaceBelow = configBox.BOTTOM - (button.yPosition + button.height);
        final int spaceAbove = button.yPosition - configBox.TOP;
        final boolean openBelow = spaceBelow >= fullHeight || spaceBelow >= spaceAbove;
        final int space = openBelow ? spaceBelow : spaceAbove;
        final int visibleRows = Math.max(1, Math.min(maxVisibleRows, (space - 2 * BORDER) / ROW_HEIGHT));
        final int height = visibleRows * ROW_HEIGHT + 2 * BORDER;
        dropdownBox.setDim(button.xPosition, openBelow ? button.yPosition + button.height : button.yPosition - height, button.width, height);
        rowsBox.LEFT = dropdownBox.LEFT + BORDER;
        rowsBox.RIGHT = dropdownBox.RIGHT - BORDER - (this.getContentHeight() > visibleRows * ROW_HEIGHT ? SCROLLBAR_WIDTH : 0);
        rowsBox.TOP = dropdownBox.TOP + BORDER;
        rowsBox.BOTTOM = dropdownBox.BOTTOM - BORDER;
    }

    private int getHoveredIndex(int mouseX, int mouseY) {
        if (!rowsBox.isMouseInBox(mouseX, mouseY)) return -1;
        final int index = (mouseY - rowsBox.TOP + scrollbar.getScroll()) / ROW_HEIGHT;
        return index < values.length ? index : -1;
    }

    private int getContentHeight() {
        return values.length * ROW_HEIGHT;
    }

}
