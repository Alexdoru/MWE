package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;

public class HunterStrengthGui extends MyCachedGui {

    public static HunterStrengthGui instance;

    private static final String DUMMY_TEXT = "\u00a77(\u00a7l\u00a7c\u00a7lStrength\u00a77) \u00a7e\u00a7l10";
    private static long timeStartRender;

    public HunterStrengthGui() {
        instance = this;
        guiPosition = ConfigHandler.hunterStrengthHUDPosition;
    }

    @Override
    public void render() {
        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        int x = absolutePos[0];
        int y = absolutePos[1];
        drawCenteredString(frObj, displayText, x, y, 0);
    }

    @Override
    public void renderDummy() {
        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        int x = absolutePos[0];
        int y = absolutePos[1];
        drawCenteredString(frObj, DUMMY_TEXT, x, y, 0);
    }

    @Override
    public boolean isEnabled() {
        return timeStartRender + 9500L - System.currentTimeMillis() > 0;
    }

    public void setDisplayText(String text) {
        displayText = text;
    }

    public void setRenderStart() {
        timeStartRender = System.currentTimeMillis();
    }

}
