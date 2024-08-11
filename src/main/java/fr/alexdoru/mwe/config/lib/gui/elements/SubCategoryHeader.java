package fr.alexdoru.mwe.config.lib.gui.elements;

import net.minecraft.client.Minecraft;

public class SubCategoryHeader implements ConfigUIElement {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final String categoryName;
    private final String subCategoryName;
    private int boxWidth;

    public SubCategoryHeader(String categoryName, String subCategoryName) {
        this.categoryName = categoryName;
        this.subCategoryName = subCategoryName;
    }

    @Override
    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
    }

    @Override
    public void draw(int drawX, int drawY, int mouseX, int mouseY) {
        final int textX = drawX + (boxWidth - mc.fontRendererObj.getStringWidth(subCategoryName)) / 2;
        mc.fontRendererObj.drawStringWithShadow(subCategoryName, textX, drawY, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
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
        return categoryName.toLowerCase().contains(search)
                || subCategoryName.toLowerCase().contains(search);
    }

}
