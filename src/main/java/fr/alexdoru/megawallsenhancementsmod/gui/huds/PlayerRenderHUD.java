package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.IRenderer;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class PlayerRenderHUD implements IRenderer {

    private final Minecraft mc = Minecraft.getMinecraft();
    private float partialTicks;
    private final GuiPosition guiPosition = new GuiPosition(0.15d, 0.15d);

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
        return this.guiPosition;
    }

    private int renderPlayerHUD(int x, int y, EntityPlayer player) {
        final int scale = 50;
        final int extraBorder = (int) (50 / 1.8F);
        final int rectwidth = scale + extraBorder;
        final int rectHight = scale * 2 + extraBorder;
        final int textHight = mc.fontRendererObj.FONT_HEIGHT * 8;
        Gui.drawRect(x, y, x + rectwidth, y + rectHight + textHight, 0xC0000000);
        GlStateManager.color(1F, 1F, 1F, 1F);
        drawEntityOnScreen(x + rectwidth / 2, y + rectHight - extraBorder / 2, scale, player);

        x += 1;
        y += rectHight;
        final PlayerDataSamples data = ((EntityPlayerAccessor) player).getPlayerDataSamples();

        final String targetText = EnumChatFormatting.BLUE + "Target " + (data.targetedPlayer == null ? "" : EnumChatFormatting.RESET + NameUtil.getFormattedNameWithoutIcons(data.targetedPlayer.getName()));
        mc.fontRendererObj.drawStringWithShadow(targetText, x, y, 0xFFFFFF);
        y += mc.fontRendererObj.FONT_HEIGHT;

        final String usingItem = EnumChatFormatting.BLUE + "UsingItem " + (player.isUsingItem() ? EnumChatFormatting.GREEN + "true" : EnumChatFormatting.RED + "false");
        mc.fontRendererObj.drawStringWithShadow(usingItem, x, y, 0xFFFFFF);
        y += mc.fontRendererObj.FONT_HEIGHT;

        final String srpintText = EnumChatFormatting.BLUE + "Sprinting " + (data.sprintTime == 0 ? EnumChatFormatting.RED + "false" : EnumChatFormatting.GREEN + "true");
        mc.fontRendererObj.drawStringWithShadow(srpintText, x, y, 0xFFFFFF);
        y += mc.fontRendererObj.FONT_HEIGHT;

        final String swingText = EnumChatFormatting.BLUE + "Swinging " + (player.isSwingInProgress ? EnumChatFormatting.GREEN + "true" : EnumChatFormatting.RED + "false");
        mc.fontRendererObj.drawStringWithShadow(swingText, x, y, 0xFFFFFF);
        y += mc.fontRendererObj.FONT_HEIGHT;

        final String swingPerSecText = EnumChatFormatting.BLUE + "Swing/s " + EnumChatFormatting.RESET + data.swingList.sum();
        mc.fontRendererObj.drawStringWithShadow(swingPerSecText, x, y, 0xFFFFFF);
        y += mc.fontRendererObj.FONT_HEIGHT;

        final String attackPerSecText = EnumChatFormatting.BLUE + "Attack/s " + EnumChatFormatting.RESET + data.attackList.sum();
        mc.fontRendererObj.drawStringWithShadow(attackPerSecText, x, y, 0xFFFFFF);
        y += mc.fontRendererObj.FONT_HEIGHT;

        if (data.speedXList.size() > 1) {
            final double currentSpeed = data.getSpeedXZ();
            final double lastSpeed = data.getSpeedXZ(1);
            final String ratioText;
            if (lastSpeed == 0D) {
                ratioText = "";
            } else {
                final double ratio = (currentSpeed / lastSpeed - 1D) * 100D;
                if (currentSpeed >= lastSpeed) {
                    ratioText = EnumChatFormatting.GREEN + String.format("%.0f", ratio) + "%";
                } else {
                    ratioText = EnumChatFormatting.RED + String.format("%.0f", ratio) + "%";
                }
            }
            final String speedText = EnumChatFormatting.BLUE + "m/s " + EnumChatFormatting.RESET + String.format("%.2f", currentSpeed);
            mc.fontRendererObj.drawStringWithShadow(speedText, x, y, 0xFFFFFF);
            final int xRatioText = x + mc.fontRendererObj.getStringWidth("m/s 00.00 +00%") - mc.fontRendererObj.getStringWidth(ratioText);
            mc.fontRendererObj.drawStringWithShadow(ratioText, xRatioText, y, 0xFFFFFF);
        }

        x -= 1;
        x += rectwidth + scale / 2;
        return x;
    }

    private void drawEntityOnScreen(int Xcenter, int Ycenter, @SuppressWarnings("SameParameterValue") int scale, EntityLivingBase entity) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) Xcenter, (float) Ycenter, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        float f = mc.thePlayer.prevRotationYaw + (mc.thePlayer.rotationYaw - mc.thePlayer.prevRotationYaw) * this.partialTicks;
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
