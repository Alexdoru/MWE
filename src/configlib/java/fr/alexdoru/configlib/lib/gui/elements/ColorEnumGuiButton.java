package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.lib.gui.ColorEnumPickerScreen;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import net.minecraft.util.EnumChatFormatting;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ColorEnumGuiButton extends ConfigGuiButton {

    private final ConfigGuiScreen parentScreen;
    private final ClickGuiButton button;
    private int color;
    private EnumChatFormatting value;

    public ColorEnumGuiButton(ConfigGuiScreen parentScreen, Field field, Method event, ConfigProperty annotation) throws IllegalAccessException {
        super(field, event, annotation);
        this.parentScreen = parentScreen;
        this.value = (EnumChatFormatting) this.field.get(null);
        this.color = mc.fontRendererObj.getColorCode(this.value.toString().charAt(1));

        int width = 0;
        for (final EnumChatFormatting c : EnumChatFormatting.values()) {
            if (c.isColor()) {
                width = Math.max(width, mc.fontRendererObj.getStringWidth("  " + c.name()));
            }
        }
        this.button = new ClickGuiButton(0, 0, 0, width, 20, "");
        this.button.displayString = this.value + this.value.name();
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY);
        button.xPosition = drawX + boxWidth - button.width - 20;
        button.yPosition = drawY + (hasComment ? 8 + mc.fontRendererObj.FONT_HEIGHT / 2 : (getHeight() - button.height) / 2);
        button.drawButton(colorPalette, mc, mouseX, mouseY);
        final int left = button.xPosition - 20 - 1;
        final int top = button.yPosition;
        GuiUtil.drawBoxWithOutline(left, top, left + 20, top + 20, 255 << 24 | color, colorPalette.COLOR_BUTTON_INDICATOR_BORDER);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) throws IllegalAccessException {
        if (mouseButton.isLeft() && button.mousePressed(mc, mouseX, mouseY)) {
            button.playPressSound(mc.getSoundHandler());
            mc.displayGuiScreen(new ColorEnumPickerScreen(parentScreen, field, this.value,
                    () -> {
                        try {
                            this.value = (EnumChatFormatting) this.field.get(null);
                            this.color = mc.fontRendererObj.getColorCode(this.value.toString().charAt(1));
                            this.button.displayString = this.value + this.value.name();
                        } catch (IllegalAccessException ignored) {}
                        invokeConfigEvent();
                    }));
            return true;
        }
        return false;
    }

}
