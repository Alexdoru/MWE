package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import net.minecraft.client.Minecraft;

public class TextLabel implements ConfigUIElement {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final String text;
    private int boxWidth;

    public TextLabel(String text) {
        this.text = text;
    }

    @Override
    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        final int textX = drawX + (boxWidth - mc.fontRendererObj.getStringWidth(this.text)) / 2;
        mc.fontRendererObj.drawStringWithShadow(this.text, textX, drawY, colorPalette.LABEL_TEXT);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) {
        return false;
    }

    @Override
    public int getHeight() {
        return mc.fontRendererObj.FONT_HEIGHT;
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getSubCategory() {
        return null;
    }

    @Override
    public boolean matchSearch(String search) {
        return false;
    }

}
