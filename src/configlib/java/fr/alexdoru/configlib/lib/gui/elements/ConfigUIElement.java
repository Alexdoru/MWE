package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ConfigUIElement extends SizedElement {

    void setBoxWidth(int boxWidth);

    void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY);

    boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) throws IllegalAccessException;

    default boolean mouseReleased(int mouseX, int mouseY, MouseButton mouseButton) {return false;}

    String getCategory();

    String getSubCategory();

    boolean matchSearch(String search);

    boolean isVisible();

    default List<String> resizeCommentLines(String comment, int wrapWidth) {
        final String[] split = comment.split("\n");
        if (wrapWidth <= 0) {
            return Arrays.asList(split);
        } else {
            final List<String> resizedLines = new ArrayList<>();
            for (final String line : split) {
                resizedLines.addAll(Minecraft.getMinecraft().fontRendererObj.listFormattedStringToWidth(line, wrapWidth));
            }
            return resizedLines;
        }
    }

}
