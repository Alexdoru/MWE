package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.gui.ScaledResolution;

import static net.minecraft.util.EnumChatFormatting.*;

public class HunterStrengthHUD extends AbstractRenderer {

    public static HunterStrengthHUD instance;

    private static final String DUMMY_TEXT = GRAY + "(" + RED + BOLD + "Strength" + GRAY + ")" + YELLOW + BOLD + " in 10";
    private static final String PRE_STRENGTH_TEXT = GRAY + "(" + RED + BOLD + "Strength" + GRAY + ")" + YELLOW + BOLD + " in ";
    private static final String STRENGTH_TEXT = RED.toString() + BOLD + "Strength " + YELLOW + BOLD;
    private long timeStartRender;
    private long renderDuration;
    private boolean isStrengthRender;

    public HunterStrengthHUD() {
        super(ConfigHandler.hunterStrengthHUDPosition);
        instance = this;
    }

    @Override
    public void render(ScaledResolution resolution) {
        this.guiPosition.updateAbsolutePosition(resolution);
        final int timeLeft = (int) ((timeStartRender + renderDuration - System.currentTimeMillis()) / 1000L);
        final String displayText = (isStrengthRender ? STRENGTH_TEXT : PRE_STRENGTH_TEXT) + timeLeft;
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

    public void setPreStrengthTime(String preStrengthTimer) {
        isStrengthRender = false;
        timeStartRender = System.currentTimeMillis();
        renderDuration = 1000L * Integer.parseInt(preStrengthTimer) + 1000L;
    }

    public void setStrengthRenderStart(long duration) {
        isStrengthRender = true;
        timeStartRender = System.currentTimeMillis();
        renderDuration = duration;
    }

}
