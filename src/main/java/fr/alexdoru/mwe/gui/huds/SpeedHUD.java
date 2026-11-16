package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.util.EnumChatFormatting;

public class SpeedHUD extends SimpleTextRenderer {

    public SpeedHUD() {
        super(MWEConfig.speedHUDPosition);
        this.setDummyText(EnumChatFormatting.GREEN + "Speed: " + EnumChatFormatting.WHITE + "5.65m/s");
    }

    @Override
    protected void updateText() {
        final double vX = mc.thePlayer.motionX;
        final double vZ = mc.thePlayer.motionZ;
        final double velocity = Math.sqrt(vX * vX + vZ * vZ) * 20d;
        this.setText("Speed: " + EnumChatFormatting.WHITE + String.format("%.2f", velocity) + "m/s");
        this.setColor(MWEConfig.speedHUDColor);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.rendererPosition.isEnabled();
    }

}
