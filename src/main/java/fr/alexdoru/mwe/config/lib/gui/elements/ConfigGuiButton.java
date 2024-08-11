package fr.alexdoru.mwe.config.lib.gui.elements;

import fr.alexdoru.mwe.config.lib.ConfigProperty;
import fr.alexdoru.mwe.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class ConfigGuiButton implements ConfigUIElement {

    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final Field field;
    private final Method event;
    private final ConfigProperty annotation;
    private final List<String> commentToRender = new ArrayList<>();
    protected final boolean hasComment;
    protected int boxWidth;
    protected int posX, posY;

    protected ConfigGuiButton(Field field, Method event, ConfigProperty annotation) {
        this.field = field;
        this.event = event;
        this.annotation = annotation;
        this.hasComment = !this.annotation.comment().isEmpty();
    }

    @Override
    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
        if (hasComment) {
            final int wrapWidth = boxWidth - mc.fontRendererObj.getStringWidth(" Disabled ") - 20 - 20;
            resizeCommentLines(commentToRender, annotation.comment(), wrapWidth, mc);
        }
    }

    @Override
    public void draw(int drawX, int drawY, int mouseX, int mouseY) {
        this.posX = drawX;
        this.posY = drawY;
        GuiUtil.drawBoxWithOutline(drawX, drawY, drawX + boxWidth, drawY + getHeight(), 0xFF505050, 0xFF8D8D8D);
        mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GREEN + annotation.name(), drawX + 8, drawY + 8, 0xFFFFFFFF);
        if (hasComment) {
            int commentY = drawY + 8 + mc.fontRendererObj.FONT_HEIGHT + 8;
            for (final String line : commentToRender) {
                mc.fontRendererObj.drawStringWithShadow(line, drawX + 8, commentY, 0xFFFFFFFF);
                commentY += mc.fontRendererObj.FONT_HEIGHT;
            }
        }
    }

    @Override
    public int getHeight() {
        if (hasComment) {
            return 8 + mc.fontRendererObj.FONT_HEIGHT + 8 + mc.fontRendererObj.FONT_HEIGHT * commentToRender.size() + 8 - 1;
        }
        return 8 + mc.fontRendererObj.FONT_HEIGHT + 8 - 1;
    }

    @Override
    public String getCategory() {
        return annotation.category();
    }

    @Override
    public String getSubCategory() {
        return annotation.subCategory();
    }

    @Override
    public boolean matchSearch(String search) {
        return annotation.category().toLowerCase().contains(search)
                || annotation.subCategory().toLowerCase().contains(search)
                || annotation.name().toLowerCase().contains(search);
    }

    protected void invokeConfigEvent() {
        if (event != null) {
            try {
                event.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
