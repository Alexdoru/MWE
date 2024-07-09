package fr.alexdoru.mwe.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class TextElement implements UIElement {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private final String text;
    private final int posX;
    private final int posY;
    private float size = 1F;
    private boolean centered = false;

    public TextElement(String text, int posX, int posY) {
        this.text = text;
        this.posX = posX;
        this.posY = posY;
    }

    public TextElement makeCentered() {
        this.centered = true;
        return this;
    }

    public TextElement setSize(float size) {
        this.size = size;
        return this;
    }

    @Override
    public void render() {
        GlStateManager.pushMatrix();
        {
            final int x = this.centered ? (int) (this.posX - (mc.fontRendererObj.getStringWidth(this.text) * this.size) / 2.0f) : this.posX;
            GlStateManager.translate(x, this.posY, 0);
            GlStateManager.scale(this.size, this.size, this.size);
            mc.fontRendererObj.drawStringWithShadow(this.text, 0, 0, 0xFFFFFF);
        }
        GlStateManager.popMatrix();
    }

}
