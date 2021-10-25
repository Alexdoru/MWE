package fr.alexdoru.megawallsenhancementsmod.gui;

import fr.alexdoru.megawallsenhancementsmod.config.MWEnConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class KillCooldownGui extends Gui {
	
	public KillCooldownGui(Minecraft mc, String msg) {
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		int height = scaled.getScaledHeight();
		drawString(mc.fontRendererObj, msg, (int)(((double)width)*MWEnConfigHandler.xpos_killcooldownGUI), (int)(((double)height)*MWEnConfigHandler.ypos_killcooldownGUI), Integer.parseInt("AA0000", 16));
	}
	
}
