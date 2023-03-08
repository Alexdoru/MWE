package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public final class GuiPosition {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /** The default relative x and y coordinates for this HUD. Ranging from 0 to 1 */
    private final double defaultRelativeX, defaultRelativeY;
    /** The relative x and y coordinates. Ranging from 0 to 1 */
    private double relativeX, relativeY;
    /** The absolute position to render this hud, needs to be updated before rendering */
    private int absoluteRenderX, absoluteRenderY;

    public GuiPosition(double defaultX, double defaultY) {
        this.defaultRelativeX = defaultX;
        this.defaultRelativeY = defaultY;
        this.relativeX = defaultX;
        this.relativeY = defaultY;
    }

    /**
     * @param x The relative x coordinate to be set. Ranging from 0 to 1.
     * @param y The relative y coordinate to be set. Ranging from 0 to 1.
     */
    public void setRelativePosition(double x, double y) {
        this.relativeX = x;
        this.relativeY = y;
    }

    /**
     * @param res scaled resolution
     * @param x The absolute x coordinate to be set.
     * @param y The absolute y coordinate to be set.
     */
    public void setAbsolutePosition(ScaledResolution res, int x, int y) {
        this.relativeX = ((double) x) / ((double) res.getScaledWidth());
        this.relativeY = ((double) y) / ((double) res.getScaledHeight());
    }

    public int[] getAbsolutePosition() {
        final ScaledResolution res = new ScaledResolution(mc);
        return new int[]{(int) (relativeX * res.getScaledWidth()), (int) (relativeY * res.getScaledHeight())};
    }

    public int[] getAbsolutePosition(ScaledResolution res) {
        return new int[]{(int) (relativeX * res.getScaledWidth()), (int) (relativeY * res.getScaledHeight())};
    }

    /**
     * Returns x and y coordinates to start the render
     * so that the whole HUD fits within the screen
     */
    public int[] getAdjustedAbsolutePosition(ScaledResolution res, int hudWidth, int hudHigth) {
        int xRenderPos = (int) (relativeX * res.getScaledWidth());
        int yRenderPos = (int) (relativeY * res.getScaledHeight());
        if (xRenderPos + hudWidth > res.getScaledWidth()) {
            xRenderPos = res.getScaledWidth() - hudWidth;
        }
        if (yRenderPos + hudHigth > res.getScaledHeight()) {
            yRenderPos = res.getScaledHeight() - hudHigth;
        }
        return new int[]{xRenderPos, yRenderPos};
    }

    /**
     * @return The relative x coordinate, ranging from 0 to 1.
     */
    public double getRelativeX() {
        return relativeX;
    }

    /**
     * @return The relative y coordinate, ranging from 0 to 1.
     */
    public double getRelativeY() {
        return relativeY;
    }

    public void resetToDefault() {
        this.relativeX = this.defaultRelativeX;
        this.relativeY = this.defaultRelativeY;
    }

}
	
