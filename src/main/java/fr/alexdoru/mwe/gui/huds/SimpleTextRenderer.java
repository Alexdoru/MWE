package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.configlib.api.RendererPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

public abstract class SimpleTextRenderer extends AbstractRenderer {

    private boolean centered;
    private String dummyText = "";
    private String text = "";
    private int color = 0xFFFFFF;

    public SimpleTextRenderer(RendererPosition rendererPosition) {
        super(rendererPosition);
    }

    protected void updateText() {}

    @Override
    public final void render(ScaledResolution resolution) {
        this.updateText();
        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        final int width = fr.getStringWidth(this.text);
        final int offsetX = this.centered ? -width / 2 : 0;
        this.rendererPosition.updateAdjustedAbsolutePosition(resolution, width, fr.FONT_HEIGHT, offsetX, 0);
        final int x = this.rendererPosition.getAbsoluteRenderX();
        final int y = this.rendererPosition.getAbsoluteRenderY();
        fr.drawStringWithShadow(this.text, x, y, this.color);
    }

    @Override
    public final void renderDummy() {
        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        final int x = this.rendererPosition.getAbsoluteRenderX();
        final int y = this.rendererPosition.getAbsoluteRenderY();
        if (this.centered) {
            this.drawCenteredString(fr, this.dummyText, x, y, 0xFFFFFF);
        } else {
            fr.drawStringWithShadow(this.dummyText, x, y, 0xFFFFFF);
        }
    }

    protected final void setCentered() {
        this.centered = true;
    }

    protected final void setDummyText(String text) {
        this.dummyText = text;
    }

    public final String getText() {
        return this.text;
    }

    protected final void setText(String text) {
        this.text = text;
    }

    protected final void setColor(int color) {
        this.color = color;
    }

}
