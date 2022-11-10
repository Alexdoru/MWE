package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;


public class CreeperPrimedTNTHUD extends MyCachedHUD {

    public static CreeperPrimedTNTHUD instance;

    private static final String DUMMY_TEXT = "\u00a7a3.0s";
    private long timeStartRender;
    private long renderDuration;        //in reality the timer ingame counts from 3-0 including the full second of 0 time so its 4s
    private String lastCountdownNum = "balls";
    private EnumChatFormatting colorPrefix = EnumChatFormatting.GREEN;

    public CreeperPrimedTNTHUD() {
        super(ConfigHandler.creeperTNTHUDPosition);
        instance = this;
    }

    @Override
    public void render(ScaledResolution resolution) {
        final int[] absolutePos = this.guiPosition.getAbsolutePosition(resolution);
        final long temp = (timeStartRender + renderDuration - System.currentTimeMillis());
        final String timeLeft = String.format("%.1f", (float) temp / 1000);
        displayText = (colorPrefix + timeLeft + "s");
        drawCenteredString(frObj, displayText, absolutePos[0], absolutePos[1], 0);
    }

    @Override
    public void renderDummy() {
        final int[] absolutePos = this.guiPosition.getAbsolutePosition();
        drawCenteredString(frObj, DUMMY_TEXT, absolutePos[0], absolutePos[1], 0);
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
        if (cooldownTimer.equals("3")) {
            colorPrefix = EnumChatFormatting.GREEN;
        } else if (cooldownTimer.equals("2")) {
            colorPrefix = EnumChatFormatting.YELLOW;
        } else if (cooldownTimer.equals("1")) {
            colorPrefix = EnumChatFormatting.GOLD;
        } else if (cooldownTimer.equals("0")) {
            colorPrefix = EnumChatFormatting.RED;
        }
        lastCountdownNum = cooldownTimer;
    }


}
