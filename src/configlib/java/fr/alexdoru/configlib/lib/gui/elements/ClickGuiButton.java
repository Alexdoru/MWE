package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClickGuiButton extends GuiButton {

    private final List<String> hoveringTextLines = new ArrayList<>();
    private ResourceLocation texture;

    public ClickGuiButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    public ClickGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    public void setHoveringText(String... hoverText) {
        this.hoveringTextLines.clear();
        this.hoveringTextLines.addAll(Arrays.asList(hoverText));
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    public void drawButton(ColorPalette colorPalette, Minecraft mc, int mouseX, int mouseY) {
        this.hovered = this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        if (this.visible) {
            this.mouseDragged(mc, mouseX, mouseY);
            GuiUtil.drawBoxWithOutline(
                    this.xPosition,
                    this.yPosition,
                    this.xPosition + this.width,
                    this.yPosition + this.height,
                    this.hovered ? GuiUtil.brightenColor(colorPalette.BUTTON_BACKGROUND, 0.12f) : colorPalette.BUTTON_BACKGROUND,
                    this.hovered ? GuiUtil.brightenColor(colorPalette.BUTTON_BACKGROUND_BORDER, 0.12f) : colorPalette.BUTTON_BACKGROUND_BORDER
            );
            if (this.texture != null) {
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                GlStateManager.color(1f, 1f, 1f, 1f);
                mc.getTextureManager().bindTexture(texture);
                final int PADDING = 2;
                GuiUtil.drawFullTextureWithCustomSize(xPosition + PADDING, yPosition + PADDING, width - PADDING * 2, height - PADDING * 2);
                GlStateManager.disableBlend();
            }
            this.drawCenteredString(
                    mc.fontRendererObj,
                    this.displayString,
                    this.xPosition + this.width / 2,
                    this.yPosition + (this.height - 8) / 2,
                    colorPalette.BUTTON_TEXT
            );
        }
    }

    public List<String> getHoveringTextLines() {
        return hoveringTextLines;
    }

    public boolean hasHoveringText() {
        return !hoveringTextLines.isEmpty();
    }

}
