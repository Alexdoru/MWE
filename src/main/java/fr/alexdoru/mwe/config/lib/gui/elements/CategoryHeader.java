package fr.alexdoru.mwe.config.lib.gui.elements;

import fr.alexdoru.mwe.config.lib.ConfigCategoryContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.List;

public class CategoryHeader implements ConfigUIElement {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final String name;
    private final String displayname;
    private final String comment;
    private final List<String> commentToRender = new ArrayList<>();
    protected final boolean hasComment;
    private int boxWidth;

    public CategoryHeader(String name) {
        this.name = name;
        this.displayname = name;
        this.comment = "";
        this.hasComment = false;
    }

    public CategoryHeader(ConfigCategoryContainer categoryContainer) {
        this.name = categoryContainer.getCategoryName();
        this.displayname = categoryContainer.getAnnotation().displayname();
        this.comment = categoryContainer.getAnnotation().comment();
        this.hasComment = !this.comment.isEmpty();
    }

    @Override
    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
        if (hasComment) {
            final int wrapWidth = boxWidth * 4 / 5;
            resizeCommentLines(commentToRender, this.comment, wrapWidth, mc);
        }
    }

    @Override
    public void draw(int drawX, int drawY, int mouseX, int mouseY) {
        final int textX = drawX + (boxWidth - mc.fontRendererObj.getStringWidth(displayname) * 2) / 2;
        GlStateManager.translate(textX, drawY, 0);
        GlStateManager.scale(2, 2, 2);
        mc.fontRendererObj.drawStringWithShadow(displayname, 0, 0, 0xFFFFFFFF);
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.translate(-textX, -drawY, 0);
        if (hasComment) {
            int commentY = drawY + mc.fontRendererObj.FONT_HEIGHT * 2 + 8;
            for (final String line : commentToRender) {
                final int lineX = drawX + (boxWidth - mc.fontRendererObj.getStringWidth(line)) / 2;
                mc.fontRendererObj.drawStringWithShadow(line, lineX, commentY, 0xFFFFFFFF);
                commentY += mc.fontRendererObj.FONT_HEIGHT;
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    @Override
    public int getHeight() {
        if (hasComment) {
            return mc.fontRendererObj.FONT_HEIGHT * 2 + 8 + mc.fontRendererObj.FONT_HEIGHT * commentToRender.size() + 8;
        }
        return mc.fontRendererObj.FONT_HEIGHT * 2 + 8;
    }

    @Override
    public String getCategory() {
        return name;
    }

    @Override
    public String getSubCategory() {
        return "";
    }

    @Override
    public boolean matchSearch(String search) {
        return false;
    }

}
