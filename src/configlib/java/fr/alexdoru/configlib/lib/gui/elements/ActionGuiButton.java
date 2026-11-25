package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigGuiAction;
import fr.alexdoru.configlib.lib.gui.GuiUtil;
import fr.alexdoru.configlib.lib.gui.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActionGuiButton implements SizedElement {

    private static final int TEXT_PADDING = 2;

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Method action;
    private final String displayString;
    private String visibleString;
    private int posX, posY;

    public ActionGuiButton(Method action) {
        this.action = action;
        this.displayString = action.getAnnotation(ConfigGuiAction.class).displayString();
        this.visibleString = this.displayString;
    }

    public void draw(ColorPalette colorPalette, int mouseX, int mouseY) {
        final int color = isMouseHovering(mouseX, mouseY) ? GuiUtil.brightenColor(colorPalette.ACTION_BUTTON_TEXT, 0.12f) : colorPalette.ACTION_BUTTON_TEXT;
        mc.fontRendererObj.drawStringWithShadow(this.visibleString, posX + TEXT_PADDING, posY + TEXT_PADDING, color);
    }

    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) {
        if (mouseButton.isLeft() && isMouseHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            runAction();
            return true;
        }
        return false;
    }

    private void runAction() {
        try {
            action.invoke(null);
        } catch (IllegalAccessException | InvocationTargetException e) {
            new IllegalStateException("Failed to run config gui action " + action.getName(), e).printStackTrace();
        }
    }

    private boolean isMouseHovering(int mouseX, int mouseY) {
        return mouseX >= posX && mouseY >= posY && mouseX < posX + getWidth() && mouseY < posY + getHeight();
    }

    public void setLayout(int posX, int posY, int maxWidth) {
        this.posX = posX;
        this.posY = posY;
        final int maxTextWidth = maxWidth - 2 * TEXT_PADDING;
        if (mc.fontRendererObj.getStringWidth(displayString) <= maxTextWidth) {
            this.visibleString = displayString;
        } else {
            this.visibleString = mc.fontRendererObj.trimStringToWidth(displayString, maxTextWidth - mc.fontRendererObj.getStringWidth("...")) + EnumChatFormatting.RESET + "...";
        }
    }

    public int getWidth() {
        return mc.fontRendererObj.getStringWidth(visibleString) + 2 * TEXT_PADDING;
    }

    @Override
    public int getHeight() {
        return mc.fontRendererObj.FONT_HEIGHT + 2 * TEXT_PADDING;
    }

}
