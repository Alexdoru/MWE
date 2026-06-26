package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ColorEnumGuiButton extends ConfigGuiButton {

    private final ClickGuiButton button;
    private EnumChatFormatting chatColor;
    private int color;

    public ColorEnumGuiButton(Field field, Method event, ConfigProperty annotation) throws IllegalAccessException {
        super(field, event, annotation);
        this.button = new ClickGuiButton(0, 0, 0, mc.fontRendererObj.getStringWidth(" LIGHT_PURPLE "), 20, "");
        this.setColor((EnumChatFormatting) this.field.get(null));
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
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IllegalAccessException {
        if ((mouseButton == 0 || mouseButton == 1) && button.mousePressed(mc, mouseX, mouseY)) {
            button.playPressSound(mc.getSoundHandler());
            this.cycleColor(mouseButton == 0 ? 1 : -1);
            return true;
        }
        return false;
    }

    private void setColor(EnumChatFormatting color) {
        this.chatColor = color;
        this.color = Minecraft.getMinecraft().fontRendererObj.getColorCode(color.toString().charAt(1));
        this.button.displayString = color + color.name();
    }

    private void cycleColor(int direction) throws IllegalAccessException {
        final EnumChatFormatting[] values = EnumChatFormatting.values();
        EnumChatFormatting nextColor = this.chatColor;
        do {
            nextColor = values[(nextColor.ordinal() + direction) % values.length];
        } while (!nextColor.isColor());
        this.setColor(nextColor);
        this.field.set(null, nextColor);
        this.invokeConfigEvent();
    }

}
