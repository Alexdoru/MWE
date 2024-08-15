package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public class EntityPlayerSPHook_Sprint {

    public static boolean shouldSprint(boolean original, Minecraft mc) {
        return original || MWEConfig.toggleSprint && mc.gameSettings.keyBindForward.isKeyDown();
    }

}
