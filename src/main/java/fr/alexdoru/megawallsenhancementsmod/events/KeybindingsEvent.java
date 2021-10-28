package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.misc.LogGlitching;
import fr.alexdoru.nocheatersmod.NoCheatersMod;
import fr.alexdoru.nocheatersmod.commands.CommandWDR;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

public class KeybindingsEvent {

    private final LogGlitching logGlitching = new LogGlitching();

    @SubscribeEvent
    public void key(KeyInputEvent e) {

        if (Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null) {
            return;
        }

        try {
            if (Keyboard.isCreated()) {
                if (Keyboard.getEventKeyState()) {
                    int keycode = Keyboard.getEventKey();

                    if (keycode == MegaWallsEnhancementsMod.log_key_fast.getKeyCode()) {
                        logGlitching.fastlogglitch();
                    } else if (keycode == MegaWallsEnhancementsMod.log_key_normal.getKeyCode()) {
                        logGlitching.logglitch();
                    } else if (keycode == MegaWallsEnhancementsMod.killkey.getKeyCode()) {
                        ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().thePlayer, "/kill");
                    } else if (keycode == NoCheatersMod.addtimemark_key.getKeyCode()) {
                        CommandWDR.addTimeMark();
                    }

                }
            }
        } catch (Exception q) {
            q.printStackTrace();
        }
    }

}
