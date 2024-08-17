package fr.alexdoru.mwe.gui.guiapi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public final class GuiPosition {

    private boolean enabled;
    /** The default relative x and y coordinates for this HUD. Ranging from 0 to 1 */
    private final double defaultRelativeX, defaultRelativeY;
    /** The relative x and y coordinates. Ranging from 0 to 1 */
    private double relativeX, relativeY;
    /** The absolute position to render this hud, needs to be updated before rendering */
    private int absoluteRenderX, absoluteRenderY;

    public GuiPosition(boolean enabled, double defaultX, double defaultY) {
        this.enabled = enabled;
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

    public void setAbsolutePositionForRender(int x, int y) {
        this.absoluteRenderX = x;
        this.absoluteRenderY = y;
    }

    public void saveAbsoluteToRelative() {
        final ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        this.relativeX = ((double) this.absoluteRenderX) / ((double) res.getScaledWidth());
        this.relativeY = ((double) this.absoluteRenderY) / ((double) res.getScaledHeight());
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
        updateAbsolutePosition(new ScaledResolution(Minecraft.getMinecraft()));
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
    public void updateAdjustedAbsolutePosition(ScaledResolution res, int hudWidth, int hudHeight) {
        updateAbsolutePosition(res);
        if (this.absoluteRenderX + hudWidth > res.getScaledWidth()) {
            this.absoluteRenderX = res.getScaledWidth() - hudWidth;
        }
        if (this.absoluteRenderY + hudHeight > res.getScaledHeight()) {
            this.absoluteRenderY = res.getScaledHeight() - hudHeight;
        }
    }

    /**
     * Updates the position of the render : absoluteRenderX and absoluteRenderY
     * Needs to be called before getAbsoluteRenderX() and getAbsoluteRenderY()
     * So that the whole HUD fits inside the screen
     */
    public void updateAdjustedAbsolutePosition(ScaledResolution res, int hudWidth, int hudHeight, int renderOffsetX, int renderOffsetY) {
        updateAbsolutePosition(res);
        this.absoluteRenderX = this.absoluteRenderX + renderOffsetX;
        this.absoluteRenderY = this.absoluteRenderY + renderOffsetY;
        if (this.absoluteRenderX < 0) {
            this.absoluteRenderX = 0;
        }
        if (this.absoluteRenderY < 0) {
            this.absoluteRenderY = 0;
        }
        if (this.absoluteRenderX + hudWidth > res.getScaledWidth()) {
            this.absoluteRenderX = res.getScaledWidth() - hudWidth;
        }
        if (this.absoluteRenderY + hudHeight > res.getScaledHeight()) {
            this.absoluteRenderY = res.getScaledHeight() - hudHeight;
        }
    }

    public void setEnabled(boolean b) {
        enabled = b;
    }

    public boolean isEnabled() {
        return enabled;
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

    public int getAbsoluteRenderX() {
        return absoluteRenderX;
    }

    public int getAbsoluteRenderY() {
        return absoluteRenderY;
    }

}