package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ColorEnumGuiButton extends EnumGuiButton {

    private int color;

    public ColorEnumGuiButton(Field field, Method event, ConfigProperty annotation) throws IllegalAccessException {
        super(field, event, annotation);
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY);
        final int colorBoxSize = button.height;
        final int left = this.button.xPosition - colorBoxSize - 1;
        final int top = this.button.yPosition;
        GuiUtil.drawBoxWithOutline(left, top, left + colorBoxSize, top + colorBoxSize, 255 << 24 | color, colorPalette.COLOR_BUTTON_INDICATOR_BORDER);
    }

    @Override
    protected void setValue(Enum<?> v) {
        this.value = v;
        this.color = Minecraft.getMinecraft().fontRendererObj.getColorCode(v.toString().charAt(1));
        this.button.displayString = v + v.name();
    }

    @Override
    protected void cycleEnum(int direction) throws IllegalAccessException {
        EnumChatFormatting nextColor = (EnumChatFormatting) this.value;
        do {
            int nextOrdinal = (nextColor.ordinal() + direction) % values.length;
            if (nextOrdinal < 0) nextOrdinal += values.length;
            nextColor = (EnumChatFormatting) values[nextOrdinal];
        } while (!nextColor.isColor());
        this.setValue(nextColor);
        this.field.set(null, nextColor);
        this.invokeConfigEvent();
    }

}
