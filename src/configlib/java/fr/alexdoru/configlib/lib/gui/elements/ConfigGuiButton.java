package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.lib.ConfigFieldContainer;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class ConfigGuiButton implements ConfigUIElement {

    protected final Minecraft mc = Minecraft.getMinecraft();
    private final ConfigFieldContainer fieldContainer;
    protected final Field field;
    protected final ConfigProperty annotation;
    private final List<String> commentToRender = new ArrayList<>();
    protected int boxWidth;
    protected int posX, posY;

    protected ConfigGuiButton(ConfigFieldContainer fieldContainer) {
        this.fieldContainer = fieldContainer;
        this.field = fieldContainer.getField();
        this.annotation = fieldContainer.getAnnotation();
    }

    @Override
    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
        if (this.hasComment()) {
            final int wrapWidth = boxWidth - mc.fontRendererObj.getStringWidth(" Disabled ") - 20 - 20;
            this.commentToRender.clear();
            this.commentToRender.addAll(resizeCommentLines(annotation.comment(), wrapWidth));
        }
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        this.posX = drawX;
        this.posY = drawY;
        GuiUtil.drawBoxWithOutline(drawX, drawY, drawX + boxWidth, drawY + getHeight(), colorPalette.SETTING_BACKGROUND, colorPalette.SETTING_BACKGROUND_BORDER);
        mc.fontRendererObj.drawStringWithShadow(annotation.name(), drawX + 8, drawY + 8, colorPalette.SETTING_NAME_TEXT);
        if (this.hasComment()) {
            int commentY = drawY + 8 + mc.fontRendererObj.FONT_HEIGHT + 8;
            for (final String line : commentToRender) {
                mc.fontRendererObj.drawStringWithShadow(line, drawX + 8, commentY, colorPalette.SETTING_COMMENT_TEXT);
                commentY += mc.fontRendererObj.FONT_HEIGHT;
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.hasComment()) {
            return 8 + mc.fontRendererObj.FONT_HEIGHT + 8 + mc.fontRendererObj.FONT_HEIGHT * commentToRender.size() + 8 - 1;
        }
        return 8 + mc.fontRendererObj.FONT_HEIGHT + 8 - 1;
    }

    @Override
    public final String getCategory() {
        return annotation.category();
    }

    @Override
    public final String getSubCategory() {
        return annotation.subCategory();
    }

    @Override
    public boolean matchSearch(String search) {
        return annotation.category().toLowerCase().contains(search)
                || annotation.subCategory().toLowerCase().contains(search)
                || annotation.name().toLowerCase().contains(search);
    }

    @Override
    public final boolean isVisible() {
        return fieldContainer.isVisible();
    }

    protected final void toggleDependencies(ConfigGuiScreen configGuiScreen) {
        if (fieldContainer.hasDependent()) {
            configGuiScreen.rebuildRenderedElementList();
        }
    }

    protected final boolean hasComment() {
        return !this.annotation.comment().isEmpty();
    }

    protected final void invokeConfigEvent() {
        if (fieldContainer.getEvent() != null) {
            try {
                fieldContainer.getEvent().invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected final void playPressSound() {
        this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

}
