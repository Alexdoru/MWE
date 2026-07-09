package fr.alexdoru.configlib.lib.gui;

import fr.alexdoru.configlib.api.IRenderer;
import fr.alexdoru.configlib.api.RendererPosition;
import fr.alexdoru.configlib.lib.RendererManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Collections;

public class RendererEditGuiScreen extends GuiScreen {

    private static final int BUTTON_SIZE = 10;

    private final RendererManager rendererManager;
    private final IRenderer renderer;
    private final RendererPosition rendererPosition;
    private final GuiScreen parent;
    private final CustomButton[] customButtons;
    private final double originalRelativeX, originalRelativeY;
    private boolean dragging;
    private int prevX, prevY;

    public RendererEditGuiScreen(RendererManager rendererManager, IRenderer renderer, GuiScreen parent) {
        this.rendererManager = rendererManager;
        this.renderer = renderer;
        this.rendererPosition = renderer.getPosition();
        this.parent = parent;
        this.originalRelativeX = rendererPosition.getRelativeX();
        this.originalRelativeY = rendererPosition.getRelativeY();
        this.customButtons = new CustomButton[]{
                new CustomButton(new ResourceLocation("configlib", "reload.png"), "Reset to Default Position"),
                new CustomButton(new ResourceLocation("configlib", "undo.png"), "Undo Changes")
        };
    }

    @Override
    public void initGui() {
        final int buttonX = this.width - BUTTON_SIZE - 2;
        int buttonY = 2;
        for (final CustomButton button : customButtons) {
            button.onResize(buttonX, buttonY);
            buttonY += BUTTON_SIZE + 2;
        }
        final ScaledResolution res = new ScaledResolution(mc);
        this.rendererPosition.updateAbsolutePosition(res);
        this.adjustBounds(res);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.renderCrosshair();
        final boolean prevEnabled = this.rendererPosition.isEnabled();
        this.rendererPosition.setEnabled(false);
        this.rendererManager.renderEditScreenBackground(this.renderer);
        this.rendererPosition.setEnabled(prevEnabled);
        super.drawDefaultBackground();
        if (this.dragging) {
            this.rendererPosition.setAbsolutePositionForRender(
                    this.rendererPosition.getAbsoluteRenderX() + mouseX - this.prevX,
                    this.rendererPosition.getAbsoluteRenderY() + mouseY - this.prevY
            );
        }
        this.renderer.renderDummy();
        if (!this.dragging) {
            String hoveringText = null;
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            for (final CustomButton button : customButtons) {
                final String textIfHovered = button.draw(mc, mouseX, mouseY);
                if (hoveringText == null || hoveringText.isEmpty()) hoveringText = textIfHovered;
            }
            GlStateManager.disableBlend();
            GlStateManager.color(1f, 1f, 1f, 1f);
            if (hoveringText != null && !hoveringText.isEmpty()) {
                drawHoveringText(Collections.singletonList(hoveringText), mouseX, mouseY + fontRendererObj.FONT_HEIGHT + 6);
            }
        }
        this.prevX = mouseX;
        this.prevY = mouseY;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == GuiUtil.MOUSE_LEFT) {
            if (!this.dragging) {
                int clickedButtonIdx = -1;
                for (int i = 0; i < customButtons.length; ++i) {
                    if (customButtons[i].isMouseOver()) {
                        clickedButtonIdx = i;
                        break;
                    }
                }
                if (clickedButtonIdx != -1) {
                    if (clickedButtonIdx == 0) rendererPosition.resetToDefault();
                    else rendererPosition.setRelativePosition(originalRelativeX, originalRelativeY);
                    final ScaledResolution res = new ScaledResolution(mc);
                    this.rendererPosition.updateAbsolutePosition(res);
                    this.adjustBounds(res);
                    return;
                }
            }
            this.dragging = true;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (state == GuiUtil.MOUSE_LEFT) {
            this.dragging = false;
        }
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
    private void adjustBounds(ScaledResolution res) {
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

    private static class CustomButton {
        private final ResourceLocation icon;
        private final String hoveringText;
        private final GuiButton button;

        public CustomButton(ResourceLocation icon, String hoveringText) {
            this.icon = icon;
            this.hoveringText = hoveringText;
            this.button = new GuiButton(-1, 0, 0, BUTTON_SIZE, BUTTON_SIZE, "");
        }

        public void onResize(int x, int y) {
            this.button.xPosition = x;
            this.button.yPosition = y;
        }

        public String draw(Minecraft mc, int mouseX, int mouseY) {
            button.drawButton(mc, mouseX, mouseY);
            GlStateManager.color(1f, 1f, 1f, 1f);
            mc.getTextureManager().bindTexture(icon);
            final int iconSize = 6;
            final int iconOffset = (BUTTON_SIZE - iconSize) / 2;
            GuiUtil.drawFullTextureWithCustomSize(button.xPosition + iconOffset, button.yPosition + iconOffset, iconSize, iconSize);
            return button.isMouseOver() ? hoveringText : null;
        }

        public boolean isMouseOver() {
            return button.isMouseOver();
        }
    }
}
