package fr.alexdoru.mwe.chat;

import fr.alexdoru.mwe.asm.interfaces.GuiScreenInvoker;
import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.utils.ColorUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.EnumChatFormatting.*;

public class GuiChatSearchBox extends GuiTextField {

    // thanks to Sfyri for making the search texture
    private static final ResourceLocation searchIcon = new ResourceLocation("mwe", "shearch_icon.png");
    private final int searchIconX;
    private final int defaultWidth;
    private boolean isRegexSearch;
    private String errorMessage;

    public GuiChatSearchBox(int componentId, FontRenderer fontrendererObj, int x, int y, int width, int height) {
        super(componentId, fontrendererObj, x + height + 1, y, width, height);
        this.searchIconX = x;
        this.defaultWidth = width;
        this.setMaxStringLength(256);
        this.setEnableBackgroundDrawing(false);
    }

    /**
     * Returns true if on the search icon or the search box
     */
    public boolean isMouseOnSearchBar(int mouseX, int mouseY) {
        return mouseX >= this.searchIconX - 2
                && mouseX < this.xPosition + this.width
                && mouseY >= this.yPosition - 1
                && mouseY < this.yPosition + this.height;
    }

    /**
     * Returns true only if on the search icon
     */
    public boolean isMouseOnSearchIcon(int mouseX, int mouseY) {
        return mouseX >= this.searchIconX - 2
                && mouseX < this.searchIconX + this.height
                && mouseY >= this.yPosition - 1
                && mouseY < this.yPosition + this.height + 1;
    }

    /**
     * Returns true only if on the text box
     */
    public boolean isMouseOnTextField(int mouseX, int mouseY) {
        return mouseX >= this.xPosition - 1
                && mouseX < this.xPosition + this.width
                && mouseY >= this.yPosition - 1
                && mouseY < this.yPosition + this.height;
    }

    public void drawTextBox(GuiScreen guiscreen, int mouseX, int mouseY) {
        drawRect(this.searchIconX - 1 - 1, this.yPosition - 1 - 1, this.searchIconX + this.height, this.yPosition + this.height, Integer.MIN_VALUE);
        drawRect(this.searchIconX - 1, this.yPosition - 1, this.searchIconX + this.height - 1, this.yPosition + this.height - 1, 0x20FFFFFF);
        if (isTextFieldShowing()) {
            final int textWidth = guiscreen.mc.fontRendererObj.getStringWidth(this.getText());
            if (textWidth + 9 > this.width) {
                if (this.width < 3 * this.defaultWidth && this.width < guiscreen.width / 2) this.width = textWidth + 9;
            } else {
                this.width = Math.max(textWidth + 9, defaultWidth);
            }
            if (isRegexSearch) {
                drawSearchIcon(guiscreen, this.searchIconX + 1, this.yPosition, AQUA);
            } else {
                drawSearchIcon(guiscreen, this.searchIconX + 1, this.yPosition, GREEN);
            }
            drawRect(this.xPosition - 1, this.yPosition - 1 - 1, this.xPosition + this.width, this.yPosition + this.height, Integer.MIN_VALUE);
            drawRect(this.xPosition - 1, this.yPosition - 1, this.xPosition + this.width - 1, this.yPosition + this.height - 1, 0x20FFFFFF);
        } else {
            drawSearchIcon(guiscreen, this.searchIconX + 1, this.yPosition, WHITE);
        }
        super.drawTextBox();
        if (this.isFocused() && this.isRegexSearch && this.errorMessage != null) {
            guiscreen.mc.fontRendererObj.drawStringWithShadow(RED + this.errorMessage, this.searchIconX + this.height + 1 + this.width + 4, this.yPosition, 0xFFFFFF);
        }
        if (this.isMouseOnSearchIcon(mouseX, mouseY)) {
            final List<String> tooltip = new ArrayList<>();
            if (!this.isFocused()) {
                tooltip.add(GREEN + "Click to search the chat!");
                tooltip.add(GRAY + "Shift click to use " + AQUA + "regex" + GRAY + " search");
                if (ConfigHandler.searchBoxChatShortcuts) {
                    tooltip.add("");
                    tooltip.add(GRAY + "Shortcuts :");
                    tooltip.add(GREEN + "Ctrl + F" + GRAY + " for normal search");
                    tooltip.add(AQUA + "Ctrl + Shift + F" + GRAY + " for regex search");
                }
            } else {
                tooltip.add(GRAY + "Current search mode : " + (isRegexSearch ? AQUA + "REGEX" : GREEN + "NORMAL"));
                if (isRegexSearch) {
                    tooltip.add(GRAY + "Learn about regex at :");
                    tooltip.add(UNDERLINE.toString() + GRAY + "https://regex101.com/");
                }
                tooltip.add("");
                tooltip.add(GRAY + "Click to switch to " + (!isRegexSearch ? AQUA + "REGEX" : GREEN + "NORMAL") + GRAY + " search");
            }
            ((GuiScreenInvoker) guiscreen).mwe$drawHoveringText(tooltip, mouseX, mouseY - guiscreen.mc.fontRendererObj.FONT_HEIGHT * tooltip.size() - 3);
        }
    }

    private void drawSearchIcon(GuiScreen guiScreen, int drawX, int drawY, EnumChatFormatting color) {
        drawX -= 2;
        drawY -= 1;
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        guiScreen.mc.getTextureManager().bindTexture(searchIcon);
        final int colorI = ColorUtil.getColorInt(color.toString().charAt(1));
        final int r = colorI >> 16 & 255;
        final int g = colorI >> 8 & 255;
        final int b = colorI & 255;
        GlStateManager.color(r / 255f, g / 255f, b / 255f);
        drawModalRectWithCustomSizedTexture(drawX, drawY, 0f, 0f, 10, 10, 10f, 10f);
        GlStateManager.popMatrix();
    }

    public boolean isTextFieldShowing() {
        return this.isFocused() || !this.getText().isEmpty();
    }

    public void setRegexSearch(boolean regexSearch) {
        this.isRegexSearch = regexSearch;
    }

    public void setErrorMessage(String msg) {
        this.errorMessage = msg;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
