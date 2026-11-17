package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import fr.alexdoru.configlib.lib.utils.FormattingCodeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClickGuiButton extends Gui {

    private final List<String> hoveringTextLines = new ArrayList<>();
    private ResourceLocation texture;
    /** Button width in pixels */
    public int width;
    /** Button height in pixels */
    public int height;
    /** The x position of this control. */
    public int xPosition;
    /** The y position of this control. */
    public int yPosition;
    /** The string displayed on this control. */
    public String displayString;
    protected boolean hovered;

    public ClickGuiButton(int widthIn, int heightIn) {
        this(widthIn, heightIn, "");
    }

    public ClickGuiButton(int widthIn, int heightIn, String buttonText) {
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
    }

    public void setHoveringText(String... hoverText) {
        this.hoveringTextLines.clear();
        this.hoveringTextLines.addAll(Arrays.asList(hoverText));
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    public void drawButton(ColorPalette colorPalette, Minecraft mc, int mouseX, int mouseY) {
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
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

    public boolean mousePressed(int mouseX, int mouseY) {
        final boolean isPressed = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        if (isPressed) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
        }
        return isPressed;
    }

    public boolean isMouseOver() {
        return this.hovered;
    }

    public List<String> getHoveringTextLines() {
        return hoveringTextLines;
    }

    public boolean hasHoveringText() {
        return !hoveringTextLines.isEmpty();
    }

    public static class FormattingCodeButton extends ClickGuiButton {

        private final EnumChatFormatting formatting;

        public FormattingCodeButton(int size, EnumChatFormatting formatting) {
            super(size, size, formatting + String.valueOf(Character.toUpperCase(FormattingCodeUtil.getCode(formatting))));
            this.formatting = formatting;
        }

        @Override
        public void drawButton(ColorPalette colorPalette, Minecraft mc, int mouseX, int mouseY) {
            if (!formatting.isColor()) {
                super.drawButton(colorPalette, mc, mouseX, mouseY);
                return;
            }
            final int argb = 255 << 24 | mc.fontRendererObj.getColorCode(FormattingCodeUtil.getCode(formatting));
            hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
            GuiUtil.drawBoxWithOutline(
                    xPosition, yPosition,
                    xPosition + width, yPosition + height,
                    argb,
                    hovered ? Color.WHITE.getRGB() : Color.BLACK.getRGB()
            );
        }

        public EnumChatFormatting getFormatting() {
            return formatting;
        }

    }

}
