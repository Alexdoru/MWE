package fr.alexdoru.megawallsenhancementsmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class KillCooldownGui extends Gui {
	
	public KillCooldownGui(Minecraft mc, String msg) {
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		int height = scaled.getScaledHeight();
		drawString(mc.fontRendererObj, msg, 0, 0, Integer.parseInt("AA0000", 16));
	}
	
}
