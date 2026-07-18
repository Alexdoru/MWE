package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class ConfigGuiButton implements ConfigUIElement {

    protected static final int PADDING = 8;
    protected static final int BUTTON_RIGHT_MARGIN = 20;

    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final Field field;
    private final Method event;
    private final ConfigProperty annotation;
    private final List<String> commentToRender = new ArrayList<>();
    protected final boolean hasComment;
    protected int boxWidth;
    protected int posX, posY;
    protected int contentLeft;

    protected ConfigGuiButton(Field field, Method event, ConfigProperty annotation) {
        this.field = field;
        this.event = event;
        this.annotation = annotation;
        this.hasComment = !this.annotation.comment().isEmpty();
    }

    /**
     * @return The distance between the left-most position of the content (button) and the right side of the rect
     */
    protected abstract int getRightSideContentWidth();

    @Override
    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
        if (hasComment) {
            final int wrapWidth = boxWidth - getLeftPadding() - getRightSideContentWidth() - 12; // 20
            this.commentToRender.clear();
            this.commentToRender.addAll(resizeCommentLines(annotation.comment(), wrapWidth, mc));
        }
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        this.posX = drawX;
        this.posY = drawY;
        final int right = drawX + boxWidth;
        this.contentLeft = right - getRightSideContentWidth();
        GuiUtil.drawBoxWithOutline(drawX, drawY, right, drawY + getHeight(), colorPalette.SETTING_BACKGROUND, colorPalette.SETTING_BACKGROUND_BORDER);
        final int textX = drawX + getLeftPadding();
        mc.fontRendererObj.drawStringWithShadow(annotation.name(), textX, drawY + PADDING, colorPalette.SETTING_NAME_TEXT);
        if (hasComment) {
            int commentY = drawY + PADDING + mc.fontRendererObj.FONT_HEIGHT + 8; // '8' here represents the vertical space between name and comment (can be different from padding)
            for (final String line : commentToRender) {
                mc.fontRendererObj.drawStringWithShadow(line, textX, commentY, colorPalette.SETTING_COMMENT_TEXT);
                commentY += mc.fontRendererObj.FONT_HEIGHT;
            }
        }
    }

    @Override
    public int getHeight() {
        if (hasComment) {
            return PADDING + mc.fontRendererObj.FONT_HEIGHT + PADDING + mc.fontRendererObj.FONT_HEIGHT * commentToRender.size() + 8 - 1; // '8' here represents the vertical space between name and comment (can be different from padding)
        }
        return PADDING + mc.fontRendererObj.FONT_HEIGHT + PADDING - 1;
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

    protected void playPressSound() {
        this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    protected int getLeftPadding() { return PADDING; }

    protected final ClickGuiButton getMainButton(String text) {
        return new ClickGuiButton(-1, 0, 0, getMainButtonWidth(), 20, text);
    }

    protected final int getMainButtonWidth() {
        return mc.fontRendererObj.getStringWidth("Disabled") + 9;
    }

    protected static String getBooleanText(boolean value) {
        return value ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled";
    }
}
