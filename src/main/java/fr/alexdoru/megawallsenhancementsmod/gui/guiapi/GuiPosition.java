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
     * @param x   The absolute x coordinate to be set.
     * @param y   The absolute y coordinate to be set.
     */
    public void setAbsolutePosition(ScaledResolution res, int x, int y) {
        this.relativeX = ((double) x) / ((double) res.getScaledWidth());
        this.relativeY = ((double) y) / ((double) res.getScaledHeight());
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

    /**
     * Updates the position of the render : absoluteRenderX and absoluteRenderY
     * Needs to be called before getAbsoluteRenderX() and getAbsoluteRenderY()
     */
    public void updateAbsolutePosition() {
        updateAbsolutePosition(new ScaledResolution(mc));
    }

    /**
     * Updates the position of the render : absoluteRenderX and absoluteRenderY
     * Needs to be called before getAbsoluteRenderX() and getAbsoluteRenderY()
     */
    public void updateAbsolutePosition(ScaledResolution res) {
        this.absoluteRenderX = (int) (relativeX * res.getScaledWidth());
        this.absoluteRenderY = (int) (relativeY * res.getScaledHeight());
    }

    /**
     * Updates the position of the render : absoluteRenderX and absoluteRenderY
     * Needs to be called before getAbsoluteRenderX() and getAbsoluteRenderY()
     * So that the whole HUD fits inside the screen
     */
    public void updateAdjustedAbsolutePosition(ScaledResolution res, int hudWidth, int hudHigth) {
        updateAbsolutePosition(res);
        if (this.absoluteRenderX + hudWidth > res.getScaledWidth()) {
            this.absoluteRenderX = res.getScaledWidth() - hudWidth;
        }
        if (this.absoluteRenderY + hudHigth > res.getScaledHeight()) {
            this.absoluteRenderY = res.getScaledHeight() - hudHigth;
        }
    }

    public int getAbsoluteRenderX() {
        return absoluteRenderX;
    }

    public int getAbsoluteRenderY() {
        return absoluteRenderY;
    }

}
	
