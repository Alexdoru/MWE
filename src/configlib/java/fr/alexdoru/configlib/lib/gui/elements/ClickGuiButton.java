package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClickGuiButton extends GuiButton {

    private List<String> hoveringTextLines = Collections.emptyList();

    public ClickGuiButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    public ClickGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    public ClickGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, List<String> hoveringTextLines) {
        this(buttonId, x, y, widthIn, heightIn, buttonText);
        setHoveringTextLines(hoveringTextLines);
    }

    public ClickGuiButton(int buttonId, int x, int y, String buttonText, List<String> hoveringTextLines) {
        this(buttonId, x, y, buttonText);
        setHoveringTextLines(hoveringTextLines);
    }

    public ClickGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, String hoveringText) {
        this(buttonId, x, y, widthIn, heightIn, buttonText, Collections.singletonList(hoveringText));
    }

    public ClickGuiButton(int buttonId, int x, int y, String buttonText, String hoveringText) {
        this(buttonId, x, y, buttonText, Collections.singletonList(hoveringText));
    }

    public void drawButton(ColorPalette colorPalette, Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            GuiUtil.drawBoxWithOutline(
                    this.xPosition,
                    this.yPosition,
                    this.xPosition + this.width,
                    this.yPosition + this.height,
                    this.hovered ? GuiUtil.brightenColor(colorPalette.BUTTON_BACKGROUND, 0.12f) : colorPalette.BUTTON_BACKGROUND,
                    this.hovered ? GuiUtil.brightenColor(colorPalette.BUTTON_BACKGROUND_BORDER, 0.12f) : colorPalette.BUTTON_BACKGROUND_BORDER
            );
            this.mouseDragged(mc, mouseX, mouseY);
            this.drawCenteredString(
                    mc.fontRendererObj,
                    this.displayString,
                    this.xPosition + this.width / 2,
                    this.yPosition + (this.height - 8) / 2,
                    colorPalette.BUTTON_TEXT
            );
        }
        else {
            this.hovered = false;
        }
    }

    public void setHoveringTextLines(List<String> hoveringTextLines) {
        this.hoveringTextLines = (hoveringTextLines == null || hoveringTextLines.isEmpty()) ? Collections.emptyList()
            : Collections.unmodifiableList(new ArrayList<>(hoveringTextLines));
    }

    public List<String> getHoveringTextLines() {
        return hoveringTextLines;
    }

    public boolean hasHoveringText() { return !hoveringTextLines.isEmpty(); }


    public static class TexturedButton extends ClickGuiButton {

        private ResourceLocation texture;

        public TexturedButton(int buttonId, int x, int y, int widthIn, int heightIn, ResourceLocation texture) {
            super(buttonId, x, y, widthIn, heightIn, "");
            this.texture = texture;
        }

        public TexturedButton(int buttonId, int x, int y, int widthIn, int heightIn, ResourceLocation texture, List<String> hoveringTextLines) {
            super(buttonId, x, y, widthIn, heightIn, "", hoveringTextLines);
            this.texture = texture;
        }

        public TexturedButton(int buttonId, int x, int y, int widthIn, int heightIn, ResourceLocation texture, String hoveringText) {
            this(buttonId, x, y, widthIn, heightIn, texture,Collections.singletonList(hoveringText));
        }

        @Override
        public void drawButton(ColorPalette colorPalette, Minecraft mc, int mouseX, int mouseY) {
            if (visible) {
                super.drawButton(colorPalette, mc, mouseX, mouseY);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                GlStateManager.color(1f, 1f, 1f, 1f);
                mc.getTextureManager().bindTexture(texture);
                final int texturePadding = 2;
                GuiUtil.drawFullTextureWithCustomSize(xPosition + texturePadding, yPosition + texturePadding, width - texturePadding * 2, height - texturePadding * 2);
                GlStateManager.disableBlend();
            }
        }

        public void setTexture(ResourceLocation texture) { this.texture = texture; }
        public ResourceLocation getTexture() { return this.texture; }
    }
}
