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
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRendererEditGuiScreen<T extends RendererPosition> extends GuiScreen {

    protected static final int BUTTON_SIZE = 16;
    protected static final int BUTTON_GAP = 2;

    protected final RendererManager rendererManager;
    protected final T rendererPosition;
    protected final ConfigGuiScreen parent;
    protected final IRenderer renderer;
    protected final double originalRelativeX, originalRelativeY;
    protected final ClickGuiButton resetButton;
    protected final ClickGuiButton undoButton;
    protected final List<ClickGuiButton> buttons = new ArrayList<>();
    protected boolean dragging;
    protected int prevX, prevY;
    protected ScaledResolution resolution;

    protected AbstractRendererEditGuiScreen(RendererManager rendererManager, T rendererPosition, ConfigGuiScreen parent, Field field) {
        this.renderer = rendererManager.getRendererFromPosition(rendererPosition);
        if (this.renderer == null) {
            throw new RuntimeException("No registered renderer associated to " + field.getName());
        }
        this.rendererManager = rendererManager;
        this.rendererPosition = rendererPosition;
        this.parent = parent;
        this.originalRelativeX = rendererPosition.getRelativeX();
        this.originalRelativeY = rendererPosition.getRelativeY();
        this.resetButton = new ClickGuiButton(BUTTON_SIZE, BUTTON_SIZE);
        this.resetButton.setTexture(new ResourceLocation("configlib", "reload.png"));
        this.resetButton.setHoveringText("Reset to Default");
        this.undoButton = new ClickGuiButton(BUTTON_SIZE, BUTTON_SIZE);
        this.undoButton.setTexture(new ResourceLocation("configlib", "undo.png"));
        this.undoButton.setHoveringText("Undo Changes");
        this.buttons.add(resetButton);
        this.buttons.add(undoButton);
    }

    @Override
    public void initGui() {
        this.resolution = new ScaledResolution(mc);
    }

    protected abstract void drawEditedHud(int mouseX, int mouseY);

    /**
     * Recomputes the on-screen position after the relative position got changed
     */
    protected void refreshFromRelative() {}

    @Override
    public final void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.renderCrosshair();
        this.rendererManager.renderEditScreenBackground(this.renderer);
        GlStateManager.translate(0, 0, 200F);
        super.drawDefaultBackground();
        this.drawEditedHud(mouseX, mouseY);
        if (!this.dragging) {
            this.drawButtons(mouseX, mouseY);
        }
        GlStateManager.translate(0, 0, -200F);
        this.prevX = mouseX;
        this.prevY = mouseY;
    }

    private void drawButtons(int mouseX, int mouseY) {
        final int buttonX = this.width - BUTTON_SIZE - BUTTON_GAP;
        int buttonY = BUTTON_GAP;
        for (final ClickGuiButton button : buttons) {
            button.xPosition = buttonX;
            button.yPosition = buttonY;
            button.drawButton(parent.getColorPalette(), mc, mouseX, mouseY);
            buttonY += button.height + BUTTON_GAP;
        }
        this.drawButtonsExtras(buttonX, buttonY);
        for (final ClickGuiButton button : buttons) {
            if (button.isMouseOver() && button.hasHoveringText()) {
                drawHoveringText(button.getHoveringTextLines(), mouseX, mouseY + fontRendererObj.FONT_HEIGHT + 6);
                break;
            }
        }
    }

    /**
     * @param nextButtonY the y coordinate below the last button
     */
    protected void drawButtonsExtras(int buttonX, int nextButtonY) {}

    /**
     * @return true if the click was consumed
     */
    protected boolean handleExtraButtonClick(MouseButton button, int mouseX, int mouseY) {
        return false;
    }

    protected void undoChanges() {
        rendererPosition.setRelativePosition(originalRelativeX, originalRelativeY);
        this.refreshFromRelative();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        final MouseButton button = MouseButton.from(mouseButton);
        if (!this.dragging) {
            if (this.handleExtraButtonClick(button, mouseX, mouseY)) {
                return;
            }
            if (button.isLeft()) {
                if (resetButton.mousePressed(mouseX, mouseY)) {
                    rendererPosition.resetToDefault();
                    this.refreshFromRelative();
                    return;
                }
                if (undoButton.mousePressed(mouseX, mouseY)) {
                    this.undoChanges();
                    return;
                }
            }
        }
        if (button.isLeft()) {
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
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void renderCrosshair() {
        mc.getTextureManager().bindTexture(Gui.icons);
        drawTexturedModalRect(this.width / 2 - 7, this.height / 2 - 7, 0, 0, 16, 16);
        GuiUtil.drawHorizontalLine(0, this.width, this.height / 2, 0xFFFFFFFF);
        GuiUtil.drawVerticalLine(this.width / 2, 0, this.height, 0xFFFFFFFF);
    }

}
