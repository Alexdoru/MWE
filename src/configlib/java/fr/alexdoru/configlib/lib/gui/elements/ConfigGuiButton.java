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

    protected static final int DEFAULT_PADDING = 8;
    protected static final int BUTTON_RIGHT_MARGIN = 20;
    private static final int COMMENT_TOP_MARGIN = 4;

    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final Field field;
    private final Method event;
    private final ConfigProperty annotation;
    private final List<String> commentToRender = new ArrayList<>();
    protected int boxWidth;
    protected int posX, posY;
    protected int boxHeight;
    protected int contentLeft;

    /** The distance between the left-most position of the content (button) and the right side of the rect */
    protected int rightSideContentWidth;

    /** The height of the right-side content (button[s], slider, etc...) */
    protected int rightSideContentHeight;

    protected ConfigGuiButton(Field field, Method event, ConfigProperty annotation) {
        this.field = field;
        this.event = event;
        this.annotation = annotation;
    }

    /**
     * {@link #rightSideContentWidth} must be correctly set before this method is called
     */
    @Override
    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
        if (this.hasComment()) {
            final int wrapWidth = boxWidth - getLeftPadding() - rightSideContentWidth - 16; // 20
            this.commentToRender.clear();
            this.commentToRender.addAll(resizeCommentLines(annotation.comment(), wrapWidth, mc));
        }
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY, boolean canMouseBeVisuallyOverElement) {
        this.posX = drawX;
        this.posY = drawY;
        this.boxHeight = getHeight();
        final int right = drawX + boxWidth;
        this.contentLeft = right - rightSideContentWidth;
        GuiUtil.drawBoxWithOutline(drawX, drawY, right, drawY + boxHeight, colorPalette.SETTING_BACKGROUND, colorPalette.SETTING_BACKGROUND_BORDER);
        final int textX = drawX + getLeftPadding();
        final int textY = drawY + (this.hasComment() ? DEFAULT_PADDING : getCenterYOffset(mc.fontRendererObj.FONT_HEIGHT));
        mc.fontRendererObj.drawStringWithShadow(annotation.name(), textX, textY, colorPalette.SETTING_NAME_TEXT);
        if (this.hasComment()) {
            int commentY = textY + mc.fontRendererObj.FONT_HEIGHT + COMMENT_TOP_MARGIN;
            for (final String line : commentToRender) {
                mc.fontRendererObj.drawStringWithShadow(line, textX, commentY, colorPalette.SETTING_COMMENT_TEXT);
                commentY += mc.fontRendererObj.FONT_HEIGHT;
            }
        }
    }

    @Override
    public int getHeight() {
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        if (this.hasComment()) {
            textHeight += COMMENT_TOP_MARGIN + mc.fontRendererObj.FONT_HEIGHT * commentToRender.size();
        }
        return DEFAULT_PADDING + Math.max(textHeight, rightSideContentHeight) + DEFAULT_PADDING - 1;
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

    protected boolean hasComment() {
        return !this.annotation.comment().isEmpty();
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

    // doesn't seem useful, but it's a must for future 'CustomElements'
    protected int getLeftPadding() { return DEFAULT_PADDING; }

    protected final ClickGuiButton getMainButton(String text) {
        return new ClickGuiButton(-1, 0, 0, getMainButtonWidth(), 20, text);
    }

    protected final int getMainButtonWidth() {
        return mc.fontRendererObj.getStringWidth("Disabled") + 9;
    }

    protected static String getBooleanText(boolean value) {
        return value ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled";
    }

    protected final int getCenterYOffset(int height) {
        return (this.boxHeight - height) / 2;
    }
}
