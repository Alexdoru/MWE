package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ConfigUIElement {

    void setBoxWidth(int boxWidth);

    void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY);

    boolean mouseClicked(int mouseX, int mouseY, int mouseButton) throws IllegalAccessException;

    default boolean mouseReleased(int mouseX, int mouseY, int mouseButton) {return false;}

    int getHeight();

    String getCategory();

    String getSubCategory();

    boolean matchSearch(String search);

    default boolean isMouseOnButton(int mouseX, int mouseY, int buttonX, int buttonY, int buttonWidth, int buttonHeight) {
        return mouseX >= buttonX && mouseX < buttonX + buttonWidth && mouseY >= buttonY && mouseY < buttonY + buttonHeight;
    }

    default List<String> resizeCommentLines(String comment, int wrapWidth, Minecraft mc) {
        final String[] split = comment.split("\n");
        if (wrapWidth <= 0) {
            return Arrays.asList(split);
        } else {
            final List<String> resizedLines = new ArrayList<>();
            for (final String line : split) {
                resizedLines.addAll(mc.fontRendererObj.listFormattedStringToWidth(line, wrapWidth));
            }
            return resizedLines;
        }
    }

}
