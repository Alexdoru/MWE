package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

public class CreeperPrimedTntHUD extends AbstractRenderer {

    public static CreeperPrimedTntHUD instance;

    private static final String DUMMY_TEXT = EnumChatFormatting.GREEN + "Tnt " + EnumChatFormatting.RED + "3.0s";
    private long timeStartRender;
    private long renderDuration;
    private String lastCountdownNum = "";
    private EnumChatFormatting colorPrefix = EnumChatFormatting.GREEN;

    public CreeperPrimedTntHUD() {
        super(ConfigHandler.creeperTNTHUDPosition);
        instance = this;
    }

    @Override
    public void render(ScaledResolution resolution) {
        this.guiPosition.updateAbsolutePosition(resolution);
        final long temp = (timeStartRender + renderDuration - System.currentTimeMillis());
        final String timeLeft = String.format("%.1f", (float) temp / 1000);
        final String displayText = EnumChatFormatting.GREEN + "Tnt " + (colorPrefix + timeLeft + "s");
        drawCenteredString(mc.fontRendererObj, displayText, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public void renderDummy() {
        drawCenteredString(mc.fontRendererObj, DUMMY_TEXT, this.guiPosition.getAbsoluteRenderX(), this.guiPosition.getAbsoluteRenderY(), 0xFFFFFF);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return timeStartRender + renderDuration - currentTimeMillis > 0;
    }

    public void setCooldownRenderStart(String cooldownTimer) {
        if (!lastCountdownNum.equals(cooldownTimer)) {
            timeStartRender = System.currentTimeMillis();
            renderDuration = 1000L * Integer.parseInt(cooldownTimer) + 1000L;
        }
        switch (cooldownTimer) {
            case "3":
                colorPrefix = EnumChatFormatting.GREEN;
                break;
            case "2":
                colorPrefix = EnumChatFormatting.YELLOW;
                break;
            case "1":
                colorPrefix = EnumChatFormatting.GOLD;
                break;
            case "0":
                colorPrefix = EnumChatFormatting.RED;
                break;
        }
        lastCountdownNum = cooldownTimer;
    }

}
