package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public final class GuiPosition {

    private final Minecraft mc = Minecraft.getMinecraft();

    /*The relative x and y coordinates. Ranging from 0 to 1.*/
    private double x, y;

    public GuiPosition(double x, double y) {
        setRelative(x, y);
    }

    public GuiPosition(int x, int y) {
        setAbsolute(x, y);
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
        ScaledResolution res = new ScaledResolution(mc);
        this.x = ((double) x) / ((double) res.getScaledWidth());
        this.y = ((double) y) / ((double) res.getScaledHeight());
    }

    public int[] getAbsolutePosition() {
        ScaledResolution res = new ScaledResolution(mc);
        return new int[]{(int) (x * res.getScaledWidth()), (int) (y * res.getScaledHeight())};
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

}
	
