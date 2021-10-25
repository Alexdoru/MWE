package fr.alexdoru.megawallsenhancementsmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class ArrowHitGui extends Gui {
	
	/**
	 * GUI for normal arrows hits and leaps
	 * @param mc
	 * @param HPvalue
	 * @param Color
	 */
	public ArrowHitGui(Minecraft mc, String HPvalue, String Color) {
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		int height = scaled.getScaledHeight();
		drawCenteredString(mc.fontRendererObj, HPvalue, width / 2, (height/20)*9 - 4, Integer.parseInt(Color, 16));
	}
	
	/**
	 * GUI for renegade arrow hits
	 * @param mc
	 * @param HPvalue
	 * @param Color - of the HP
	 * @param i - arrows pinned 
	 */
	public ArrowHitGui(Minecraft mc, String HPvalue, String Color, int i) {
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		int height = scaled.getScaledHeight();
		
		String arrowspinned = String.valueOf(i);
		
		String full_msg = HPvalue + " (" + arrowspinned + ")";
		
		int x = width / 2 - mc.fontRendererObj.getStringWidth(full_msg) / 2;
		int y = (height/20)*9 - 4;
		
		drawString(mc.fontRendererObj, HPvalue, x, y, Integer.parseInt(Color, 16));
		x += mc.fontRendererObj.getStringWidth(HPvalue);
		
		drawString(mc.fontRendererObj, " (", x, y, Integer.parseInt("AAAAAA", 16));
		x += mc.fontRendererObj.getStringWidth(" (");
		
		float floatHP = Float.parseFloat(HPvalue);
		if(floatHP > ((float)i) * 2.0f) {
			drawString(mc.fontRendererObj, arrowspinned, x, y, Integer.parseInt("55FF55", 16));
		} else {
			drawString(mc.fontRendererObj, arrowspinned, x, y, Integer.parseInt("FFAA00", 16));
		}
		x += mc.fontRendererObj.getStringWidth(arrowspinned);

		drawString(mc.fontRendererObj, ")", x, y, Integer.parseInt("AAAAAA", 16));
			
	}
	
}
