package fr.alexdoru.mwe.gui.guiapi;

import fr.alexdoru.mwe.utils.DelayedTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;

public class PositionEditGuiScreen extends GuiScreen {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final IRenderer renderer;
    private final GuiPosition guiPosition;
    private final GuiScreen parent;
    private boolean dragging;
    private int prevX, prevY;

    public PositionEditGuiScreen(IRenderer renderer, GuiScreen parent) {
        this.renderer = renderer;
        this.guiPosition = renderer.getGuiPosition();
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.guiPosition.updateAbsolutePosition();
        this.adjustBounds();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.renderCrosshair();
        super.drawDefaultBackground();
        renderer.renderDummy();
        if (this.dragging) {
            this.guiPosition.setAbsolutePositionForRender(
                    this.guiPosition.getAbsoluteRenderX() + mouseX - this.prevX,
                    this.guiPosition.getAbsoluteRenderY() + mouseY - this.prevY
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
    public void onGuiClosed() {
        this.guiPosition.saveAbsoluteToRelative();
        new DelayedTask(() -> mc.displayGuiScreen(parent));
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
        final int absoluteX = Math.max(0, Math.min(guiPosition.getAbsoluteRenderX(), screenWidth));
        final int absoluteY = Math.max(0, Math.min(guiPosition.getAbsoluteRenderY(), screenHeight));
        this.guiPosition.setAbsolutePositionForRender(absoluteX, absoluteY);
    }

    private void renderCrosshair() {
        mc.getTextureManager().bindTexture(Gui.icons);
        drawTexturedModalRect(this.width / 2 - 7, this.height / 2 - 7, 0, 0, 16, 16);
    }

}
