package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;
import net.minecraft.util.EnumChatFormatting;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnumGuiButton extends ConfigGuiButton {

    protected final ClickGuiButton button;
    protected final Enum<?>[] values;
    protected Enum<?> value;

    public EnumGuiButton(Field field, Method event, ConfigProperty annotation) throws IllegalAccessException {
        super(field, event, annotation);
        try {
            values = (Enum<?>[]) field.getType().getDeclaredMethod("values").invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException();
        }
        int width = 0;
        for (final Enum<?> e : values) {
            width = Math.max(width, mc.fontRendererObj.getStringWidth("  " + e.name()));
        }
        this.button = new ClickGuiButton(0, 0, 0, width, 20, "");
        this.setValue((Enum<?>) this.field.get(null));
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY);
        button.xPosition = drawX + boxWidth - button.width - 20;
        button.yPosition = drawY + (hasComment ? 8 + mc.fontRendererObj.FONT_HEIGHT / 2 : (getHeight() - button.height) / 2);
        button.drawButton(colorPalette, mc, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IllegalAccessException {
        if ((mouseButton == 0 || mouseButton == 1) && button.mousePressed(mc, mouseX, mouseY)) {
            button.playPressSound(mc.getSoundHandler());
            this.cycleEnum(mouseButton == 0 ? 1 : -1);
            return true;
        }
        return false;
    }

    protected void setValue(Enum<?> v) {
        this.value = v;
        this.button.displayString = EnumChatFormatting.GOLD + v.name();
    }

    protected void cycleEnum(int direction) throws IllegalAccessException {
        int nextOrdinal = (this.value.ordinal() + direction) % values.length;
        if (nextOrdinal < 0) nextOrdinal += values.length;
        this.setValue(this.values[nextOrdinal]);
        this.field.set(null, this.value);
        this.invokeConfigEvent();
    }

}