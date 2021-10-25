package fr.alexdoru.megawallsenhancementsmod.events;

import org.lwjgl.input.Keyboard;

import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.misc.LogGlitching;
import fr.alexdoru.nocheatersmod.NoCheatersMod;
import fr.alexdoru.nocheatersmod.commands.CommandWDR;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeybindingsEvent {
	
	private final LogGlitching logGlitching = new LogGlitching();

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
					} else if (keycode == MegaWallsEnhancementsMod.log_key_fast.getKeyCode()) {
						logGlitching.fastlogglitch();
						return;
					} else if (keycode == MegaWallsEnhancementsMod.log_key_normal.getKeyCode()) {
						logGlitching.logglitch();
						return;
					}else if (keycode == MegaWallsEnhancementsMod.killkey.getKeyCode()) {
						ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().thePlayer, "/kill");
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
