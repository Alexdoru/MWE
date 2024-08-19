package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

public class SpeedHUD extends AbstractRenderer {

    public SpeedHUD() {
        super(MWEConfig.speedHUDPosition);
    }

    @Override
    public void render(ScaledResolution resolution) {
        final double vX = mc.thePlayer.motionX;
        final double vZ = mc.thePlayer.motionZ;
        final double velocity = Math.sqrt(vX * vX + vZ * vZ) * 20d;
        final String displayText = "Speed: " + EnumChatFormatting.WHITE + String.format("%.2f", velocity) + "m/s";
        this.guiPosition.updateAdjustedAbsolutePosition(resolution, mc.fontRendererObj.getStringWidth(displayText), mc.fontRendererObj.FONT_HEIGHT);
        mc.fontRendererObj.drawStringWithShadow(displayText, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), MWEConfig.speedHUDColor);
    }

    @Override
    public void renderDummy() {
        mc.fontRendererObj.drawStringWithShadow("Speed: " + EnumChatFormatting.WHITE + "5.65m/s", this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), MWEConfig.speedHUDColor);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.guiPosition.isEnabled();
    }

}
