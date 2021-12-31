package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.nocheatersmod.NoCheatersMod;
import fr.alexdoru.nocheatersmod.commands.CommandWDR;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeybindingsEvent {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void key(KeyInputEvent e) {

        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (NoCheatersMod.addtimemark_key.isPressed()) {
            CommandWDR.addTimeMark();
        }

    }

}
