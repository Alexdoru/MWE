package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import net.minecraft.client.Minecraft;

public class SubCategoryHeader implements ConfigUIElement {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final String categoryName;
    private final String subCategoryName;
    private int reservedWidth;

    public SubCategoryHeader(String categoryName, String subCategoryName) {
        this.categoryName = categoryName;
        this.subCategoryName = subCategoryName;
    }

    @Override
    public void setReservedWidth(int width) {
        this.reservedWidth = width;
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        final int textX = drawX + (reservedWidth - mc.fontRendererObj.getStringWidth(subCategoryName)) / 2;
        mc.fontRendererObj.drawStringWithShadow(subCategoryName, textX, drawY, colorPalette.SUB_CATEGORY_HEADER_TEXT);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) {
        return false;
    }

    @Override
    public int getHeight() {
        return mc.fontRendererObj.FONT_HEIGHT;
    }

    @Override
    public String getCategory() {
        return categoryName;
    }

    @Override
    public String getSubCategory() {
        return subCategoryName;
    }

    @Override
    public boolean matchSearch(String search) {
        return false;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

}
