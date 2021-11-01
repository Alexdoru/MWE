package fr.alexdoru.fkcountermod.gui.elements;

import fr.alexdoru.fkcountermod.config.EnumFKConfigSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ButtonToggle extends GuiButton {

    private static final ResourceLocation TOGGLE_ON = new ResourceLocation("fkcounter", "toggleon.png");
    private static final ResourceLocation TOGGLE_OFF = new ResourceLocation("fkcounter", "toggleoff.png");

    final EnumFKConfigSetting setting;

    public ButtonToggle(int x, int y, EnumFKConfigSetting setting) {
        super(0, x, y, "");
        this.setting = setting;
        this.width = 24;
        this.height = 24;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        {
            if (setting.getValue()) {
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
            this.drawCenteredString(mc.fontRendererObj, setting.getTitle(), 0, 0, 0xFFFFFF);
        }
        GlStateManager.popMatrix();
    }

    public EnumFKConfigSetting getSetting() {
        return setting;
    }

}
