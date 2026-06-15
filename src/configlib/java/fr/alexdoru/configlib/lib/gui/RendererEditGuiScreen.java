package fr.alexdoru.configlib.lib.gui;

import fr.alexdoru.configlib.api.IRenderer;
import fr.alexdoru.configlib.api.IRendererManager;
import fr.alexdoru.configlib.api.RendererPosition;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;

public class RendererEditGuiScreen extends GuiScreen {

    private final IRendererManager rendererManager;
    private final IRenderer renderer;
    private final RendererPosition rendererPosition;
    private final GuiScreen parent;
    private boolean dragging;
    private int prevX, prevY;

    public RendererEditGuiScreen(IRendererManager rendererManager, IRenderer renderer, GuiScreen parent) {
        this.rendererManager = rendererManager;
        this.renderer = renderer;
        this.rendererPosition = renderer.getPosition();
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.rendererPosition.updateAbsolutePosition();
        this.adjustBounds();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.renderCrosshair();
        final boolean prevEnabled = this.rendererPosition.isEnabled();
        this.rendererPosition.setEnabled(false);
        this.rendererManager.renderEditScreenBackground(this.renderer);
        this.rendererPosition.setEnabled(prevEnabled);
        super.drawDefaultBackground();
        this.renderer.renderDummy();
        if (this.dragging) {
            this.rendererPosition.setAbsolutePositionForRender(
                    this.rendererPosition.getAbsoluteRenderX() + mouseX - this.prevX,
                    this.rendererPosition.getAbsoluteRenderY() + mouseY - this.prevY
            );
        }
        this.prevX = mouseX;
        this.prevY = mouseY;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.dragging = true;
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.dragging = false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void onGuiClosed() {
        this.rendererPosition.saveAbsoluteToRelative();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * Makes sure the HUD can't get out of the screen
     */
    private void adjustBounds() {
        final ScaledResolution res = new ScaledResolution(mc);
        final int screenWidth = res.getScaledWidth();
        final int screenHeight = res.getScaledHeight();
        final int absoluteX = Math.max(0, Math.min(rendererPosition.getAbsoluteRenderX(), screenWidth));
        final int absoluteY = Math.max(0, Math.min(rendererPosition.getAbsoluteRenderY(), screenHeight));
        this.rendererPosition.setAbsolutePositionForRender(absoluteX, absoluteY);
    }

    private void renderCrosshair() {
        mc.getTextureManager().bindTexture(Gui.icons);
        drawTexturedModalRect(this.width / 2 - 7, this.height / 2 - 7, 0, 0, 16, 16);
    }

}
