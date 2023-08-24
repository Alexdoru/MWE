package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.IRenderer;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerRenderHUD implements IRenderer {

    private final Minecraft mc = Minecraft.getMinecraft();
    private float partialTicks;

    public void setPartialTickTime(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    @Override
    public void render(ScaledResolution resolution) {
        GlStateManager.pushMatrix();
        {
            this.getGuiPosition().updateAbsolutePosition(resolution);
            int x = this.getGuiPosition().getAbsoluteRenderX();
            final int y = this.getGuiPosition().getAbsoluteRenderY();
            for (final String playername : HackerDetector.INSTANCE.playersToLog) {
                final EntityPlayer player = this.mc.theWorld.getPlayerEntityByName(playername);
                if (player == null) continue;
                x = renderPlayerHUD(x, y, player);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void renderDummy() {
        final int x = this.getGuiPosition().getAbsoluteRenderX();
        final int y = this.getGuiPosition().getAbsoluteRenderY();
        this.renderPlayerHUD(x, y, mc.thePlayer);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return ConfigHandler.debugLogging && !HackerDetector.INSTANCE.playersToLog.isEmpty();
    }

    @Override
    public GuiPosition getGuiPosition() {
        return ConfigHandler.playerRendererGuiPosition;
    }

    private int renderPlayerHUD(int x, int y, EntityPlayer player) {
        final int scale = 50;
        final int extraBorder = (int) (50 / 1.8F);
        final int rectwidth = scale + extraBorder;
        final int rectHight = scale * 2 + extraBorder;
        Gui.drawRect(x, y, x + rectwidth, y + rectHight, Integer.MIN_VALUE);
        GlStateManager.color(1F, 1F, 1F, 1F);
        drawEntityOnScreen(x + rectwidth / 2, y + rectHight - extraBorder / 2, scale, player);
        x += rectwidth + scale / 2;
        return x;
    }

    private void drawEntityOnScreen(int Xcenter, int Ycenter, int scale, EntityLivingBase entity) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) Xcenter, (float) Ycenter, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        float f = mc.thePlayer.prevRotationYawHead + (mc.thePlayer.rotationYawHead - mc.thePlayer.prevRotationYawHead) * this.partialTicks;
        if (mc.thePlayer != entity) f -= 180F;
        GlStateManager.rotate(-135.0F + f, 0.0F, 1.0F, 0.0F);
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, this.partialTicks);
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

}
