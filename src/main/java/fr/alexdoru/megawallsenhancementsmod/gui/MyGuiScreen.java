package fr.alexdoru.megawallsenhancementsmod.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Method;

public class MyGuiScreen extends GuiScreen {

    private static final ResourceLocation SHADER = new ResourceLocation("fkcounter", "shaders/blur.json");

    @Override
    public void initGui() {

        Method loadShaderMethod = null;
        try {
            loadShaderMethod = EntityRenderer.class.getDeclaredMethod("loadShader", ResourceLocation.class);
        } catch (NoSuchMethodException e) {
            try {
                loadShaderMethod = EntityRenderer.class.getDeclaredMethod("func_175069_a", ResourceLocation.class);
            } catch (NoSuchMethodException ignored) {
            }
        }

        if (loadShaderMethod != null) {
            loadShaderMethod.setAccessible(true);
            try {
                loadShaderMethod.invoke(mc.entityRenderer, SHADER);
            } catch (Exception ignored) {
            }
        }
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        mc.entityRenderer.stopUseShader();
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public int getxCenter() {
        return this.width / 2;
    }

    public int getyCenter() {
        return this.height / 2;
    }

}
