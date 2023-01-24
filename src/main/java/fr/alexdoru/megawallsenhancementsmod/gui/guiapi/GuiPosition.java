package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public final class GuiPosition {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /*The default relative x and y coordinates for this HUD. Ranging from 0 to 1.*/
    private final double defaultX, defaultY;
    /*The relative x and y coordinates. Ranging from 0 to 1.*/
    private double x, y;

    public GuiPosition(double defaultX, double defaultY) {
        this.defaultX = defaultX;
        this.defaultY = defaultY;
        this.x = defaultX;
        this.y = defaultY;
    }

    /**
     * @param x The relative x coordinate to be set. Ranging from 0 to 1.
     * @param y The relative y coordinate to be set. Ranging from 0 to 1.
     */
    public void setRelative(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @param x The absolute x coordinate to be set.
     * @param y The absolute y coordinate to be set.
     */
    public void setAbsolute(int x, int y) {
        final ScaledResolution res = new ScaledResolution(mc);
        this.x = ((double) x) / ((double) res.getScaledWidth());
        this.y = ((double) y) / ((double) res.getScaledHeight());
    }

    public int[] getAbsolutePosition() {
        final ScaledResolution res = new ScaledResolution(mc);
        return new int[]{(int) (x * res.getScaledWidth()), (int) (y * res.getScaledHeight())};
    }

    public int[] getAbsolutePosition(ScaledResolution res) {
        return new int[]{(int) (x * res.getScaledWidth()), (int) (y * res.getScaledHeight())};
    }

    /**
     * Returns x and y coordinates to start the render
     * so that the whole HUD fits within the screen
     */
    public int[] getAbsolutePositionForRender(ScaledResolution res, int hudWidth, int hudHigth) {
        int xRenderPos = (int) (x * res.getScaledWidth());
        int yRenderPos = (int) (y * res.getScaledHeight());
        if (xRenderPos + hudWidth > res.getScaledWidth()) {
            xRenderPos = res.getScaledWidth() - hudWidth;
        }
        if (yRenderPos + hudHigth > res.getScaledHeight()) {
            yRenderPos = res.getScaledHeight() - hudHigth;
        }
        return new int[]{xRenderPos, yRenderPos};
    }

    public double[] getRelativePosition() {
        return new double[]{x, y};
    }

    /**
     * @return The relative x coordinate, ranging from 0 to 1.
     */
    public double getRelativeX() {
        return x;
    }

    /**
     * @return The relative y coordinate, ranging from 0 to 1.
     */
    public double getRelativeY() {
        return y;
    }

    public void resetToDefault() {
        this.x = this.defaultX;
        this.y = this.defaultY;
    }

}
	
