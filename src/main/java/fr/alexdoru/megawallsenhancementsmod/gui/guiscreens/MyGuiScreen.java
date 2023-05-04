package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.SimpleGuiButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeVersion;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class MyGuiScreen extends GuiScreen {

    private static final ResourceLocation SHADER = new ResourceLocation("fkcounter", "shaders/blur.json");
    protected final int buttonsHeight = 20;
    protected GuiScreen parent = null;
    private int usersGuiScale = -1;
    protected int maxWidth;
    protected int maxHeight;

    @Override
    public void initGui() {
        if (usersGuiScale != -1) {
            mc.gameSettings.guiScale = usersGuiScale;
        }
        usersGuiScale = mc.gameSettings.guiScale;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        this.width = scaledresolution.getScaledWidth();
        this.height = scaledresolution.getScaledHeight();
        while (this.maxHeight + this.height / 6 > this.height || this.maxWidth > this.width) {
            if (mc.gameSettings.guiScale == 1) {
                break;
            }
            if (mc.gameSettings.guiScale == 0) {
                mc.gameSettings.guiScale = 3;
            } else {
                mc.gameSettings.guiScale--;
            }
            scaledresolution = new ScaledResolution(this.mc);
            this.width = scaledresolution.getScaledWidth();
            this.height = scaledresolution.getScaledHeight();
        }
        if (ForgeVersion.getVersion().contains("2318")) {
            mc.entityRenderer.loadShader(SHADER);
        } else {
            try {
                final Method m = mc.entityRenderer.getClass().getDeclaredMethod(ASMLoadingPlugin.isObf ? "func_175069_a" : "loadShader", ResourceLocation.class);
                m.setAccessible(true);
                m.invoke(mc.entityRenderer, SHADER);
            } catch (Exception ignored) {}
        }
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        mc.gameSettings.guiScale = usersGuiScale;
        ConfigHandler.saveConfig();
        try {
            mc.entityRenderer.stopUseShader();
        } catch (Exception ignored) {}
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof SimpleGuiButton) {
            ((SimpleGuiButton) button).onButtonPressed();
        }
    }

    protected String getSuffix(boolean enabled) {
        return enabled ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled";
    }

    protected void drawCenteredTitle(String title, int dilatation, float xPos, float yPos) {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(xPos - (mc.fontRendererObj.getStringWidth(title) * dilatation) / 2.0f, yPos, 0);
            GlStateManager.scale(dilatation, dilatation, dilatation);
            mc.fontRendererObj.drawStringWithShadow(title, 0, 0, 0);
        }
        GlStateManager.popMatrix();
    }

    protected void drawColoredRectWithOutline(int top, int bottom, int left, int right, int rgbColor) {
        drawHorizontalLine(left, right, top, Color.BLACK.getRGB());
        drawHorizontalLine(left, right, bottom, Color.BLACK.getRGB());
        drawVerticalLine(left, top, bottom, Color.BLACK.getRGB());
        drawVerticalLine(right, top, bottom, Color.BLACK.getRGB());
        drawRect(left + 1, top + 1, right, bottom, 255 << 24 | rgbColor);
    }

    /**
     * Call this at the end of the drawScreen method to draw the tooltips defined in getTooltopText
     */
    protected void drawTooltips(int mouseX, int mouseY) {
        for (final GuiButton button : this.buttonList) {
            if (button.isMouseOver()) {
                drawHoveringText(getTooltipText(button.id), mouseX, mouseY);
                return;
            }
        }
    }

    protected int getButtonYPos(int i) {
        return this.height / 8 + (buttonsHeight + 4) * (i + 1);
    }

    /**
     * Override this method and make a switch with all the buttons tooltips
     */
    protected List<String> getTooltipText(int id) {
        return new ArrayList<>();
    }

    protected int getxCenter() {
        return this.width / 2;
    }

}
