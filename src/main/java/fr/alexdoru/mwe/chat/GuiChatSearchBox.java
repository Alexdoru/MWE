package fr.alexdoru.mwe.chat;

import fr.alexdoru.mwe.asm.interfaces.GuiScreenInvoker;
import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.EnumChatFormatting.*;

public class GuiChatSearchBox extends GuiTextField {

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
                guiscreen.mc.fontRendererObj.drawStringWithShadow(AQUA + "?", this.searchIconX + 1, this.yPosition, 0xFFFFFF);
            } else {
                guiscreen.mc.fontRendererObj.drawStringWithShadow(GREEN + "?", this.searchIconX + 1, this.yPosition, 0xFFFFFF);
            }
            drawRect(this.xPosition - 1, this.yPosition - 1 - 1, this.xPosition + this.width, this.yPosition + this.height, Integer.MIN_VALUE);
            drawRect(this.xPosition - 1, this.yPosition - 1, this.xPosition + this.width - 1, this.yPosition + this.height - 1, 0x20FFFFFF);
        } else {
            guiscreen.mc.fontRendererObj.drawStringWithShadow("?", this.searchIconX + 1, this.yPosition, 0xFFFFFF);
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
                if (MWEConfig.searchBoxChatShortcuts) {
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
