package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;

public class HunterStrengthGui extends MyCachedGui {

    public static HunterStrengthGui instance;

    private static final String DUMMY_TEXT = "\u00a77(\u00a7l\u00a7c\u00a7lStrength\u00a77) \u00a7e\u00a7l in 10";
    private static final String PRE_STRENGTH_TEXT = "\u00a77(\u00a7l\u00a7c\u00a7lStrength\u00a77) \u00a7e\u00a7l in ";
    private static final String STRENGTH_TEXT = "\u00a7l\u00a7c\u00a7lStrength \u00a7e\u00a7l";
    private static long timeStartRender;
    private static long renderDuration;
    private static boolean isStrengthRender;

    public HunterStrengthGui() {
        instance = this;
        guiPosition = ConfigHandler.hunterStrengthHUDPosition;
    }

    @Override
    public void render() {
        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        int timeLeft = (int) ((timeStartRender + renderDuration - System.currentTimeMillis()) / 1000L);
        displayText = (isStrengthRender ? STRENGTH_TEXT : PRE_STRENGTH_TEXT) + timeLeft;
        drawCenteredString(frObj, displayText, absolutePos[0], absolutePos[1], 0);
    }

    @Override
    public void renderDummy() {
        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        drawCenteredString(frObj, DUMMY_TEXT, absolutePos[0], absolutePos[1], 0);
    }

    @Override
    public boolean isEnabled() {
        return timeStartRender + renderDuration - System.currentTimeMillis() > 0;
    }

    public void setPreStrengthTime(String preStrengthTimer, long currentTime) {
        isStrengthRender = false;
        try {
            timeStartRender = currentTime;
            renderDuration = 1000L * Integer.parseInt(preStrengthTimer) + 1000L;
        } catch (Exception ignored) {
        }
    }

    public void setStrengthRenderStart() {
        isStrengthRender = true;
        timeStartRender = System.currentTimeMillis();
        renderDuration = 5000L;
    }

}
