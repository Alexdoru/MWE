package fr.alexdoru.mwe.config;

import fr.alexdoru.configlib.api.ColorPalette;
import fr.alexdoru.configlib.api.IConfigTitleRenderer;
import fr.alexdoru.mwe.MWE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public final class MWEConfigTitle implements IConfigTitleRenderer {

    private static final ResourceLocation DORU_SKIN = new ResourceLocation("mwe", "doru_skin.png");

    @Override
    public void renderTitle(FontRenderer fontRenderer, ColorPalette colorPalette, int x, int y) {
        final String modnameText = MWE.modName + " - " + MWE.version + EnumChatFormatting.GRAY + " created by ";
        fontRenderer.drawStringWithShadow(modnameText, x, y, 0xFFFFFFFF);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        Minecraft.getMinecraft().getTextureManager().bindTexture(DORU_SKIN);
        final int headPosX = x + fontRenderer.getStringWidth(modnameText);
        Gui.drawScaledCustomSizeModalRect(headPosX, y, 8, 8, 8, 8, 8, 8, 64.0F, 32.0F);
        Gui.drawScaledCustomSizeModalRect(headPosX, y, 40, 8, 8, 8, 8, 8, 64.0F, 32.0F);
        fontRenderer.drawStringWithShadow(EnumChatFormatting.GOLD + "Alexdoru", headPosX + 10, y, 0xFFFFFFFF);
    }

}
