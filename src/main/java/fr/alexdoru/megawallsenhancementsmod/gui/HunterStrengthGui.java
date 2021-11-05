package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.megawallsenhancementsmod.config.MWEnConfigHandler;
import net.minecraft.client.gui.ScaledResolution;

public class HunterStrengthGui extends MyCachedGui {

    public static HunterStrengthGui instance;

    private static final String DUMMY_TEXT = "\u00a77(\u00a7l\u00a7c\u00a7lStrength\u00a77) \u00a7e\u00a7l10";
    private static long timeStartRender;
    private static final double xd = 0.5d;
    private static final double yd = 8d / 20d;

    public HunterStrengthGui() {
        instance = this;
        guiPosition = MWEnConfigHandler.hunterStrengthHUDPosition;
    }

    @Override
    public void render() {
        ScaledResolution res = new ScaledResolution(mc);
        int x = (int) (xd * res.getScaledWidth());
        int y = (int) (yd * res.getScaledHeight());
        drawCenteredString(frObj, displayText, x, y, 0);
    }

    @Override
    public void renderDummy() {
        super.renderDummy();
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

    public void setRenderStart(){
        timeStartRender = System.currentTimeMillis();
    }

}
