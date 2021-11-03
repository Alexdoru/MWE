package fr.alexdoru.megawallsenhancementsmod.gui;

import net.minecraft.client.gui.ScaledResolution;

public class HunterStrengthGui extends MyCachedGui {

    public static HunterStrengthGui instance;

    private static long timeStartRender;
    private static final double xd = 0.5d;
    private static final double yd = 11d / 20d;

    public HunterStrengthGui() {
        instance = this;
    }

    @Override
    public void render() {
        ScaledResolution res = new ScaledResolution(mc);
        int x = (int) (xd * res.getScaledWidth());
        int y = (int) (yd * res.getScaledHeight());
        drawCenteredString(frObj, displayText, x, y, 0);
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
