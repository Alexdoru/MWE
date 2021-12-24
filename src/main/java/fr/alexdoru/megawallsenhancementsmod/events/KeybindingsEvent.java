package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.nocheatersmod.NoCheatersMod;
import fr.alexdoru.nocheatersmod.commands.CommandWDR;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

public class KeybindingsEvent {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void key(KeyInputEvent e) {

        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        try {
            if (Keyboard.isCreated()) {
                if (Keyboard.getEventKeyState()) {
                    int keycode = Keyboard.getEventKey();

                    if (keycode == NoCheatersMod.addtimemark_key.getKeyCode()) {
                        CommandWDR.addTimeMark();
                    }

                }
            }
        } catch (Exception q) {
            q.printStackTrace();
        }
    }

}
