package fr.alexdoru.megawallsenhancementsmod.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ButtonToggle extends GuiButton {

    private static final ResourceLocation TOGGLE_ON = new ResourceLocation("fkcounter", "toggleon.png");
    private static final ResourceLocation TOGGLE_OFF = new ResourceLocation("fkcounter", "toggleoff.png");

    private final String buttonText;
    public boolean setting;

    public ButtonToggle(boolean settingIn, int id, int x, int y, String buttonTextIn) {
        super(id, x, y, "");
        this.setting = settingIn;
        this.width = 24;
        this.height = 24;
        this.buttonText = buttonTextIn;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        {
            if (setting) {
                mc.getTextureManager().bindTexture(TOGGLE_ON);
            } else {
                mc.getTextureManager().bindTexture(TOGGLE_OFF);
            }
            drawModalRectWithCustomSizedTexture(xPosition, yPosition, 0, 0, width, height, width, height);
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        {
            float dilatation = 0.5f;
            GlStateManager.translate(xPosition + (float) width / 2.0f, yPosition - 10f, 0);
            GlStateManager.scale(dilatation, dilatation, 1);
            this.drawCenteredString(mc.fontRendererObj, this.buttonText, 0, 0, 0xFFFFFF);
        }
        GlStateManager.popMatrix();
    }

}
