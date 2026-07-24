package fr.alexdoru.configlib.lib.gui;

import fr.alexdoru.configlib.api.IRenderer;
import fr.alexdoru.configlib.api.RendererPosition;
import fr.alexdoru.configlib.lib.RendererManager;
import fr.alexdoru.configlib.lib.gui.elements.ClickGuiButton;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.lang.reflect.Field;

public class RendererEditGuiScreen extends GuiScreen {

    private static final int BUTTON_SIZE = 16;

    private final RendererManager rendererManager;
    private final RendererPosition rendererPosition;
    private final ConfigGuiScreen parent;
    private final IRenderer renderer;
    private final double originalRelativeX, originalRelativeY;
    private final ClickGuiButton resetButton;
    private final ClickGuiButton undoButton;
    private boolean dragging;
    private int prevX, prevY;
    private ScaledResolution resolution;

    public RendererEditGuiScreen(RendererManager rendererManager, RendererPosition rendererPosition, ConfigGuiScreen parent, Field field) {
        this.rendererManager = rendererManager;
        this.rendererPosition = rendererPosition;
        this.parent = parent;
        this.renderer = rendererManager.getRendererFromPosition(rendererPosition);
        if (this.renderer == null) {
            throw new RuntimeException("No registered renderer associated to " + field.getName());
        }
        this.originalRelativeX = rendererPosition.getRelativeX();
        this.originalRelativeY = rendererPosition.getRelativeY();
        this.resetButton = new ClickGuiButton(-1, 0, 0, BUTTON_SIZE, BUTTON_SIZE, "");
        this.resetButton.setTexture(new ResourceLocation("configlib", "reload.png"));
        this.resetButton.setHoveringText("Reset to Default Position");
        this.undoButton = new ClickGuiButton(-1, 0, 0, BUTTON_SIZE, BUTTON_SIZE, "");
        this.undoButton.setTexture(new ResourceLocation("configlib", "undo.png"));
        this.undoButton.setHoveringText("Undo Changes");
    }

    @Override
    public void initGui() {
        this.resolution = new ScaledResolution(mc);
        this.rendererPosition.updateAbsolutePosition(resolution);
        this.adjustBounds();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.renderCrosshair();
        this.rendererManager.renderEditScreenBackground(this.renderer);
        GlStateManager.translate(0, 0, 200F);
        super.drawDefaultBackground();
        if (this.dragging) {
            this.rendererPosition.setAbsolutePositionForRender(
                    this.rendererPosition.getAbsoluteRenderX() + mouseX - this.prevX,
                    this.rendererPosition.getAbsoluteRenderY() + mouseY - this.prevY
            );
        }
        this.renderer.renderDummy();
        if (!this.dragging) {
            final int buttonX = this.width - BUTTON_SIZE - 2;
            int buttonY = 2;
            resetButton.xPosition = buttonX;
            resetButton.yPosition = buttonY;
            resetButton.drawButton(parent.getColorPalette(), mc, mouseX, mouseY);

            buttonY += 2 + resetButton.height;
            undoButton.xPosition = buttonX;
            undoButton.yPosition = buttonY;
            undoButton.drawButton(parent.getColorPalette(), mc, mouseX, mouseY);

            if (resetButton.isMouseOver() && resetButton.hasHoveringText()) {
                drawHoveringText(resetButton.getHoveringTextLines(), mouseX, mouseY + fontRendererObj.FONT_HEIGHT + 6);
            } else if (undoButton.isMouseOver() && undoButton.hasHoveringText()) {
                drawHoveringText(undoButton.getHoveringTextLines(), mouseX, mouseY + fontRendererObj.FONT_HEIGHT + 6);
            }
        }
        GlStateManager.translate(0, 0, -200F);
        this.prevX = mouseX;
        this.prevY = mouseY;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (MouseButton.from(mouseButton).isLeft()) {
            if (!this.dragging) {
                if (resetButton.mousePressed(mc, mouseX, mouseY)) {
                    rendererPosition.resetToDefault();
                    this.rendererPosition.updateAbsolutePosition(resolution);
                    this.adjustBounds();
                    resetButton.playPressSound(mc.getSoundHandler());
                    return;
                }

                if (undoButton.mousePressed(mc, mouseX, mouseY)) {
                    rendererPosition.setRelativePosition(originalRelativeX, originalRelativeY);
                    this.rendererPosition.updateAbsolutePosition(resolution);
                    this.adjustBounds();
                    undoButton.playPressSound(mc.getSoundHandler());
                    return;
                }
            }
            this.dragging = true;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (MouseButton.from(mouseButton).isLeft()) {
            this.dragging = false;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
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
        final int screenWidth = resolution.getScaledWidth();
        final int screenHeight = resolution.getScaledHeight();
        final int absoluteX = Math.max(0, Math.min(rendererPosition.getAbsoluteRenderX(), screenWidth));
        final int absoluteY = Math.max(0, Math.min(rendererPosition.getAbsoluteRenderY(), screenHeight));
        this.rendererPosition.setAbsolutePositionForRender(absoluteX, absoluteY);
    }

    private void renderCrosshair() {
        mc.getTextureManager().bindTexture(Gui.icons);
        drawTexturedModalRect(this.width / 2 - 7, this.height / 2 - 7, 0, 0, 16, 16);
        GuiUtil.drawHorizontalLine(0, this.width, this.height / 2, 0xFFFFFFFF);
        GuiUtil.drawVerticalLine(this.width / 2, 0, this.height, 0xFFFFFFFF);
    }

}
