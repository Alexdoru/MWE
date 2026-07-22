package fr.alexdoru.configlib.lib.gui.elements;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.ConfigProperty;
import fr.alexdoru.configlib.api.IRenderer;
import fr.alexdoru.configlib.api.RendererPosition;
import fr.alexdoru.configlib.lib.RendererManager;
import fr.alexdoru.configlib.lib.gui.ConfigGuiScreen;
import fr.alexdoru.configlib.lib.gui.RendererEditGuiScreen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RendererGuiButton extends ConfigGuiButton {

    private static final ResourceLocation MOVE_ICON = new ResourceLocation("configlib", "move_icon_64x64.png");

    private final ConfigGuiScreen parentScreen;
    private final RendererManager rendererManager;
    private final RendererPosition rendererPosition;
    private boolean toggled;
    private final ClickGuiButton buttonEnabled;
    private final ClickGuiButton buttonMoveHud;

    public RendererGuiButton(
            ConfigGuiScreen configGuiScreen,
            RendererManager rendererManager,
            Field field,
            Method event,
            ConfigProperty annotation) throws IllegalAccessException {
        super(field, event, annotation);
        this.parentScreen = configGuiScreen;
        this.rendererManager = rendererManager;
        this.rendererPosition = ((RendererPosition) field.get(null));
        this.toggled = this.rendererPosition.isEnabled();
        final int btnWidth = mc.fontRendererObj.getStringWidth(" Disabled ");
        this.buttonEnabled = new ClickGuiButton(0, 0, 0, btnWidth, 20, getButtonText());
        this.buttonMoveHud = new ClickGuiButton(0, 0, 0, btnWidth, 20, "Position");
    }

    @Override
    public void setBoxWidth(int boxWidth) {
        // super.setBoxWidth(boxWidth);
        super.setBoxWidth(boxWidth - mc.fontRendererObj.getStringWidth("Reset Position") - 10);
        this.boxWidth = boxWidth;
    }

    @Override
    public void draw(ColorPalette colorPalette, int drawX, int drawY, int mouseX, int mouseY) {
        super.draw(colorPalette, drawX, drawY, mouseX, mouseY);
        buttonEnabled.xPosition = drawX + boxWidth - buttonEnabled.width - 20;
        buttonEnabled.yPosition = drawY + 8;
        buttonEnabled.drawButton(colorPalette, mc, mouseX, mouseY);
        buttonMoveHud.xPosition = buttonEnabled.xPosition;
        buttonMoveHud.yPosition = buttonEnabled.yPosition + buttonEnabled.height + 1;
        buttonMoveHud.drawButton(colorPalette, mc, mouseX, mouseY);
        drawIcon(MOVE_ICON, buttonMoveHud.xPosition, buttonMoveHud.yPosition);
        if (buttonMoveHud.isMouseOver()) {
            final int textX = buttonEnabled.xPosition - 4 - mc.fontRendererObj.getStringWidth("Move HUD");
            final int textY = buttonMoveHud.yPosition + mc.fontRendererObj.FONT_HEIGHT / 2 + 1;
            mc.fontRendererObj.drawStringWithShadow("Move HUD", textX, textY, colorPalette.HUD_BUTTON_HINT_TEXT);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, MouseButton mouseButton) {
        if (mouseButton.isLeft()) {
            if (buttonEnabled.mousePressed(mc, mouseX, mouseY)) {
                flipBooleanConfig();
                buttonEnabled.displayString = getButtonText();
                buttonEnabled.playPressSound(mc.getSoundHandler());
                return true;
            } else if (buttonMoveHud.mousePressed(mc, mouseX, mouseY)) {
                buttonEnabled.playPressSound(mc.getSoundHandler());
                final IRenderer renderer = this.rendererManager.getRendererFromPosition(rendererPosition);
                if (renderer != null) {
                    mc.displayGuiScreen(new RendererEditGuiScreen(this.rendererManager, renderer, parentScreen));
                } else {
                    throw new RuntimeException("No registered renderer associated to " + field.getName());
                }
                return true;
            }
        }
//        else if (buttonMoveHud.mousePressed(mc, mouseX, mouseY)) {
//            buttonEnabled.playPressSound(mc.getSoundHandler());
//            mc.displayGuiScreen(new RendererEditGuiScreen(rendererManager, rendererPosition, parentScreen, field));
//            return true;
//        }
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
        rendererPosition.setEnabled(!rendererPosition.isEnabled());
        toggled = rendererPosition.isEnabled();
        invokeConfigEvent();
    }

    private String getButtonText() {
        return toggled ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled";
    }

}
