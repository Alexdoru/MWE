package fr.alexdoru.mwe.config.lib.gui.elements;

import fr.alexdoru.mwe.config.lib.ConfigProperty;
import fr.alexdoru.mwe.config.lib.gui.ConfigGuiScreen;
import fr.alexdoru.mwe.gui.guiapi.GuiManager;
import fr.alexdoru.mwe.gui.guiapi.GuiPosition;
import fr.alexdoru.mwe.gui.guiapi.IRenderer;
import fr.alexdoru.mwe.gui.guiapi.PositionEditGuiScreen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HUDGuiButton extends ConfigGuiButton {

    private static final ResourceLocation moveIcon = new ResourceLocation("mwe", "move_icon_64x64.png");
    private static final ResourceLocation resetIcon = new ResourceLocation("mwe", "reset_icon_64x64.png");

    private final ConfigGuiScreen parentScreen;
    private final GuiPosition guiPosition;
    private boolean toggled;
    private final GuiButton buttonEnabled;
    private final GuiButton buttonMoveHud;
    private final GuiButton buttonResetPos;

    public HUDGuiButton(ConfigGuiScreen configGuiScreen, Field field, Method event, ConfigProperty annotation) throws IllegalAccessException {
        super(field, event, annotation);
        this.parentScreen = configGuiScreen;
        this.guiPosition = ((GuiPosition) field.get(null));
        this.toggled = this.guiPosition.isEnabled();
        this.buttonEnabled = new GuiButton(0, 0, 0, mc.fontRendererObj.getStringWidth(" Disabled "), 20, getButtonText());
        this.buttonMoveHud = new GuiButton(0, 0, 0, 20, 20, "");
        this.buttonResetPos = new GuiButton(0, 0, 0, 20, 20, "");
    }

    @Override
    public void setBoxWidth(int boxWidth) {
        super.setBoxWidth(boxWidth - mc.fontRendererObj.getStringWidth("Reset Position") - 10);
        this.boxWidth = boxWidth;
    }

    @Override
    public void draw(int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(drawX, drawY, mouseX, mouseY);
        buttonEnabled.xPosition = drawX + boxWidth - buttonEnabled.width - 20;
        buttonEnabled.yPosition = drawY + 8;
        buttonEnabled.drawButton(mc, mouseX, mouseY);
        buttonMoveHud.xPosition = buttonEnabled.xPosition;
        buttonMoveHud.yPosition = buttonEnabled.yPosition + buttonEnabled.height + 1;
        buttonMoveHud.drawButton(mc, mouseX, mouseY);
        buttonResetPos.xPosition = buttonEnabled.xPosition + buttonEnabled.width - buttonResetPos.width - 1;
        buttonResetPos.yPosition = buttonMoveHud.yPosition;
        buttonResetPos.drawButton(mc, mouseX, mouseY);
        drawIcon(moveIcon, buttonMoveHud.xPosition, buttonMoveHud.yPosition);
        drawIcon(resetIcon, buttonResetPos.xPosition, buttonResetPos.yPosition);
        if (buttonMoveHud.isMouseOver()) {
            final int textX = buttonEnabled.xPosition - 4 - mc.fontRendererObj.getStringWidth("Move HUD");
            final int textY = buttonMoveHud.yPosition + mc.fontRendererObj.FONT_HEIGHT / 2 + 1;
            mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.YELLOW + "Move HUD", textX, textY, 0xFFFFFFFF);
        }
        if (buttonResetPos.isMouseOver()) {
            final int textX = buttonEnabled.xPosition - 4 - mc.fontRendererObj.getStringWidth("Reset Position");
            final int textY = buttonResetPos.yPosition + mc.fontRendererObj.FONT_HEIGHT / 2 + 1;
            mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.YELLOW + "Reset Position", textX, textY, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            if (buttonEnabled.mousePressed(mc, mouseX, mouseY)) {
                flipBooleanConfig();
                buttonEnabled.displayString = getButtonText();
                buttonEnabled.playPressSound(mc.getSoundHandler());
                return true;
            } else if (buttonMoveHud.mousePressed(mc, mouseX, mouseY)) {
                buttonEnabled.playPressSound(mc.getSoundHandler());
                final IRenderer renderer = GuiManager.getRendererFromPosition(guiPosition);
                if (renderer != null) {
                    mc.displayGuiScreen(new PositionEditGuiScreen(renderer, parentScreen));
                } else throw new RuntimeException("No registered HUD associated to " + field.getName());
                return true;
            } else if (buttonResetPos.mousePressed(mc, mouseX, mouseY)) {
                guiPosition.resetToDefault();
                buttonEnabled.playPressSound(mc.getSoundHandler());
                return true;
            }
        }
        return false;
    }

    @Override
    public int getHeight() {
        return Math.max(super.getHeight(), 8 + buttonEnabled.height + 1 + buttonMoveHud.height + 8 - 1);
    }

    private void drawIcon(ResourceLocation icon, int drawX, int drawY) {
        drawX += 3;
        drawY += 3;
        GlStateManager.pushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        parentScreen.mc.getTextureManager().bindTexture(icon);
        GlStateManager.color(1, 1, 1);
        Gui.drawModalRectWithCustomSizedTexture(drawX, drawY, 0f, 0f, 14, 14, 14f, 14f);
        GlStateManager.popMatrix();
    }

    private void flipBooleanConfig() {
        guiPosition.setEnabled(!guiPosition.isEnabled());
        toggled = guiPosition.isEnabled();
        invokeConfigEvent();
    }

    private String getButtonText() {
        return toggled ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled";
    }

}
