package fr.alexdoru.nocheatersmod.events;

import org.lwjgl.input.Keyboard;

import fr.alexdoru.nocheatersmod.NoCheatersMod;
import fr.alexdoru.nocheatersmod.commands.CommandWDR;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeybindingsEvent {

	@SubscribeEvent
	public void key(KeyInputEvent e) {

		if(Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null) {
			return;
		}

		try {
			if (Keyboard.isCreated()) {
				if (Keyboard.getEventKeyState()) {					
					int keycode = Keyboard.getEventKey();

					if (keycode <=0) {
						return;
					} else if (keycode == NoCheatersMod.addtimemark_key.getKeyCode()) {
						CommandWDR.addTimeMark();
						return;
					}

				}
			}
		} catch (Exception q) {
			q.printStackTrace();
		}
	}

}
