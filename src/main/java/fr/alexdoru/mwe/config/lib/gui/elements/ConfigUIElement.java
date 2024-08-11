package fr.alexdoru.mwe.config.lib.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

public interface ConfigUIElement {

    void setBoxWidth(int boxWidth);

    void draw(int drawX, int drawY, int mouseX, int mouseY);

    boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IllegalAccessException;

    default boolean mouseReleased(int mouseX, int mouseY, int mouseButton) {return false;}

    int getHeight();

    String getCategory();

    String getSubCategory();

    boolean matchSearch(String search);

    default boolean isMouseOnButton(int mouseX, int mouseY, int buttonX, int buttonY, int buttonWidth, int buttonHeight) {
        return mouseX >= buttonX && mouseX < buttonX + buttonWidth && mouseY >= buttonY && mouseY < buttonY + buttonHeight;
    }

    default void resizeCommentLines(List<String> commentToRender, String comment, int wrapWidth, Minecraft mc) {
        final List<String> splitLines = new ArrayList<>();
        final String[] split = comment.split("\n");
        for (final String line : split) {
            splitLines.add(EnumChatFormatting.GRAY + line);
        }
        commentToRender.clear();
        if (wrapWidth <= 0) {
            commentToRender.addAll(splitLines);
        } else {
            for (final String line : splitLines) {
                commentToRender.addAll(mc.fontRendererObj.listFormattedStringToWidth(line, wrapWidth));
            }
        }
    }

}
