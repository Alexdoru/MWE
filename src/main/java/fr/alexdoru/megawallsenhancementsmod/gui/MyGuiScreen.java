package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public abstract class MyGuiScreen extends GuiScreen {

    private static final ResourceLocation SHADER = new ResourceLocation("fkcounter", "shaders/blur.json");
    public final int ButtonsHeight = 20;
    public GuiScreen parent = null;

    @Override
    public void initGui() {
        mc.entityRenderer.loadShader(SHADER);
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        ConfigHandler.saveConfig();
        mc.entityRenderer.stopUseShader();
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public String getSuffix(boolean enabled) {
        return enabled ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled";
    }

    public void drawCenteredTitle(String title, int dilatation, float xPos, float yPos, int color) {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(xPos - (mc.fontRendererObj.getStringWidth(title) * dilatation) / 2.0f, yPos, 0);
            GlStateManager.scale(dilatation, dilatation, dilatation);
            mc.fontRendererObj.drawStringWithShadow(title, 0, 0, color);
        }
        GlStateManager.popMatrix();
    }

    /**
     * Call this at the end of the drawScreen method to draw the tooltips defined in getTooltopText
     */
    public void drawTooltips(int mouseX, int mouseY) {
        for (GuiButton button : this.buttonList) {
            if (button.isMouseOver()) {
                drawHoveringText(getTooltipText(button.id), mouseX, mouseY);
                return;
            }
        }
    }

    public int getYposForButton(int relativePosition) {
        return getyCenter() - ButtonsHeight / 2 + (ButtonsHeight + 4) * relativePosition;
    }

    /**
     * Override this method and make a switch with all the buttons tooltips
     */
    public List<String> getTooltipText(int id) {
        return new ArrayList<>();
    }

    public int getxCenter() {
        return this.width / 2;
    }

    public int getyCenter() {
        return this.height / 2;
    }

}
