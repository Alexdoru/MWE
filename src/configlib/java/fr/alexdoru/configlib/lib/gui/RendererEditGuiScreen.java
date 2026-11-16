package fr.alexdoru.configlib.lib.gui;

import fr.alexdoru.configlib.api.RendererPosition;
import fr.alexdoru.configlib.lib.RendererManager;

import java.lang.reflect.Field;

public class RendererEditGuiScreen extends AbstractRendererEditGuiScreen<RendererPosition> {

    public RendererEditGuiScreen(RendererManager rendererManager, RendererPosition rendererPosition, ConfigGuiScreen parent, Field field) {
        super(rendererManager, rendererPosition, parent, field);
    }

    @Override
    public void initGui() {
        if (this.resolution != null) {
            this.rendererPosition.setRelativePosition(
                    (double) this.rendererPosition.getAbsoluteRenderX() / this.resolution.getScaledWidth(),
                    (double) this.rendererPosition.getAbsoluteRenderY() / this.resolution.getScaledHeight()
            );
        }
        super.initGui();
        this.rendererPosition.updateAbsolutePosition(resolution);
        this.adjustBounds();
    }

    @Override
    protected void drawEditedHud(int mouseX, int mouseY) {
        if (this.dragging) {
            this.rendererPosition.setAbsolutePositionForRender(
                    this.rendererPosition.getAbsoluteRenderX() + mouseX - this.prevX,
                    this.rendererPosition.getAbsoluteRenderY() + mouseY - this.prevY
            );
        }
        this.renderer.renderDummy();
    }

    @Override
    protected void refreshFromRelative() {
        this.rendererPosition.updateAbsolutePosition(resolution);
        this.adjustBounds();
    }

    @Override
    public void onGuiClosed() {
        this.rendererPosition.saveAbsoluteToRelative();
    }

    /**
     * Makes sure the HUD can't get out of the screen
     */
    private void adjustBounds() {
        final int screenWidth = resolution.getScaledWidth();
        final int screenHeight = resolution.getScaledHeight();
        final int absoluteX = Math.max(0, Math.min(rendererPosition.getAbsoluteRenderX(), screenWidth));
        final int absoluteY = Math.max(0, Math.min(rendererPosition.getAbsoluteRenderY(), screenHeight));
        this.rendererPosition.setAbsolutePositionForRender(absoluteX, absoluteY);
    }

}
