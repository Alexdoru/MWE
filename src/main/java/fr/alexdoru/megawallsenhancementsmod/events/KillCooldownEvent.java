package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.megawallsenhancementsmod.gui.KillCooldownGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class KillCooldownEvent {

	public static Minecraft mc = Minecraft.getMinecraft();
	private static long lastkilltime = 0;
	private static String message;

	public static void drawCooldownGui() {

		if(!(System.currentTimeMillis() - lastkilltime < 60000)) { // doesn't update the cooldown if you used /kill in the last 60 seconds
			lastkilltime = System.currentTimeMillis();
		}

	}

	@SubscribeEvent
	public void onRenderGui(RenderGameOverlayEvent.Post event) {

		if (event.type != ElementType.EXPERIENCE) { 
			return;
		} else if(System.currentTimeMillis() - lastkilltime < 60000L) {

			int timeleft = 60 - ((int)(System.currentTimeMillis() - lastkilltime))/1000;
			message = "/kill cooldown : " + timeleft + "s";					
			new KillCooldownGui(mc,message);

		}

	}
		
	public static void hideGUI() {
		lastkilltime = 0;
	}

}
