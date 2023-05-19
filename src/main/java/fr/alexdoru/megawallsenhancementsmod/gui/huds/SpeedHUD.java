package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

public class SpeedHUD extends AbstractRenderer {

    public static SpeedHUD instance;
    private static final String DUMMY_TEXT = EnumChatFormatting.DARK_GREEN + "Speed: " + EnumChatFormatting.WHITE + "5.65m/s";

    public SpeedHUD() {
        super(ConfigHandler.speedHUDPosition);
        instance = this;
    }

    @Override
    public void render(ScaledResolution resolution) {
        final double vX = mc.thePlayer.motionX;
        final double vZ = mc.thePlayer.motionZ;
        final double velocity = Math.sqrt(vX * vX + vZ * vZ) * 20d;
        final String displayText = EnumChatFormatting.DARK_GREEN + "Speed: " + EnumChatFormatting.WHITE + String.format("%.2f", velocity) + "m/s";
        this.guiPosition.updateAdjustedAbsolutePosition(resolution, mc.fontRendererObj.getStringWidth(displayText), mc.fontRendererObj.FONT_HEIGHT);
        mc.fontRendererObj.drawStringWithShadow(displayText, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public void renderDummy() {
        mc.fontRendererObj.drawStringWithShadow(DUMMY_TEXT, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return ConfigHandler.showSpeedHUD;
    }

}
