package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.configlib.api.RendererPositionV2;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

public abstract class SimpleTextRenderer extends AbstractRenderer {

    private String dummyText = "";
    private String text = "";
    private int color = 0xFFFFFF;

    public SimpleTextRenderer(RendererPositionV2 rendererPosition) {
        super(rendererPosition);
    }

    protected void updateText() {}

    @Override
    public final void render(ScaledResolution resolution) {
        this.updateText();
        this.renderText(resolution, this.text, this.color);
    }

    @Override
    public final void renderDummy(ScaledResolution resolution) {
        this.renderText(resolution, this.dummyText, 0xFFFFFF);
    }

    private void renderText(ScaledResolution resolution, String text, int color) {
        final FontRenderer fr = mc.fontRendererObj;
        this.rendererPosition.applyTransform(resolution, fr.getStringWidth(text), fr.FONT_HEIGHT);
        fr.drawStringWithShadow(text, 0, 0, color);
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
