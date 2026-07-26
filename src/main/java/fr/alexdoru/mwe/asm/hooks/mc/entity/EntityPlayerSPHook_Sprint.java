package fr.alexdoru.mwe.asm.hooks.mc.entity;

import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.Minecraft;

public class EntityPlayerSPHook_Sprint {

    public static boolean shouldSprint(boolean original, Minecraft mc) {
        return original || MWEConfig.toggleSprint && mc.gameSettings.keyBindForward.isKeyDown();
    }

}
