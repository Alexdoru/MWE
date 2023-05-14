package fr.alexdoru.megawallsenhancementsmod.gui.guiscreens;

import fr.alexdoru.megawallsenhancementsmod.asm.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.FancyGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.SimpleGuiButton;
import fr.alexdoru.megawallsenhancementsmod.gui.elements.UIElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeVersion;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class MyGuiScreen extends GuiScreen {

    private static final ResourceLocation SHADER = new ResourceLocation("fkcounter", "shaders/blur.json");
    protected final int buttonsHeight = 20;
    protected final int BUTTON_WIDTH = 200;
    protected GuiScreen parent = null;
    private int usersGuiScale = -1;
    protected int maxWidth;
    protected int maxHeight;
    protected List<UIElement> elementList = new ArrayList<>();

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
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (final UIElement element : this.elementList) {
            element.render();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        for (final GuiButton button : this.buttonList) {
            if (button instanceof FancyGuiButton && button.isMouseOver()) {
                if (((FancyGuiButton) button).getTooltip() != null) {
                    this.drawHoveringText(((FancyGuiButton) button).getTooltip(), mouseX, mouseY + 20);
                }
            }
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        this.elementList.clear();
        super.setWorldAndResolution(mc, width, height);
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
    protected void actionPerformed(GuiButton button) {
        if (button instanceof SimpleGuiButton) {
            ((SimpleGuiButton) button).onButtonPressed();
        }
    }

    protected String getSuffix(boolean enabled) {
        return enabled ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled";
    }

    protected int getButtonYPos(int i) {
        return this.height / 8 + (buttonsHeight + 4) * (i + 1);
    }

    protected int getxCenter() {
        return this.width / 2;
    }

}
