package fr.alexdoru.fkcountermod.hudproperty;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public final class ScreenPosition {

	private Minecraft mc = Minecraft.getMinecraft();
	
	private double x, y;
	
	private ScreenPosition(double x, double y) {
		setRelative(x, y);
	}
	
	private ScreenPosition(int x, int y) {
		setAbsolute(x, y);
	}
	
	/**
	 * Creates a new ScreenPosition object based on relative coordinates. 
	 * From 0 to 1. Example: 0.3 being 30% of the screen size.
	 *
	 * @param  x  Relative screen x value (horizontal) of the position. 
	 * @param  y  Relative screen y value (vertical) of the position.
	 * @return    The created ScreenPosition object.
	 */
	public static ScreenPosition fromRelativePosition(double x, double y) {
		return new ScreenPosition(x, y);
	}

	/**
	 * Creates a new ScreenPosition object based on absolute coordinates. 
	 * Example: 300 being 300 pixels.
	 *
	 * @param  x  Absolute screen x value (horizontal) of the position.
	 * @param  y  Absolute screen y value (vertical) of the position.
	 * @return    The created ScreenPosition object.
	 */
	public static ScreenPosition fromAbsolutePosition(int x, int y) {
		return new ScreenPosition(x, y);
	}


	/**
	 * @return   The absolute x coordinate in pixel.
	 */
	public int getAbsoluteX() {
		ScaledResolution res = new ScaledResolution(mc);
		return (int)(x * res.getScaledWidth());
	}

	/**
	 * @return   The absolute y coordinate in pixel.
	 */
	public int getAbsoluteY() {
		ScaledResolution res = new ScaledResolution(mc);
		return (int)(y * res.getScaledHeight());
	}

	/**
	 * @return   The relative x coordinate, ranging from 0 to 1.
	 */
	public double getRelativeX() {
		return x;
	}

	/**
	 * @return    The relative y coordinate, ranging from 0 to 1.
	 */
	public double getRelativeY() {
		return y;
	}

	/**
	 * @param  x  The relative x coordinate to be set. Ranging from 0 to 1.
	 * @param  y  The relative y coordinate to be set. Ranging from 0 to 1.
	 */
	public void setRelative(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @param  x  The absolute x coordinate to be set. 
	 * @param  y  The absolute y coordinate to be set. 
	 */
	public void setAbsolute(int x, int y) {
		ScaledResolution res = new ScaledResolution(mc);
		this.x = (double)x / res.getScaledWidth();
		this.y = (double)y / res.getScaledHeight();
	}
	
	@Override
	public String toString(){
		return String.format(getClass().getSimpleName() + "[absoluteX=%d,absoluteY=%d,relativeX=%.1f,relativeY=%.1f]", this.getAbsoluteX(), this.getAbsoluteY(), this.getRelativeX(), this.getRelativeY());
	}
	
}
	
