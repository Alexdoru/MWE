package fr.alexdoru.mwe.api.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public final class RenderUtil {

    private RenderUtil() {}

    public static void renderSkinHead(ResourceLocation locationSkin, int x, int y, boolean renderHatLayer, int skinSize) {
        renderSkinHead(locationSkin, x, y, renderHatLayer, skinSize, 1.0F);
    }

    public static void renderSkinHead(ResourceLocation locationSkin, int x, int y, boolean renderHatLayer, int skinSize, float alpha) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        Minecraft.getMinecraft().getTextureManager().bindTexture(locationSkin);
        Gui.drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, skinSize, skinSize, 64.0F, 64.0F);
        if (renderHatLayer) {
            Gui.drawScaledCustomSizeModalRect(x, y, 40, 8, 8, 8, skinSize, skinSize, 64.0F, 64.0F);
        }
    }

}
