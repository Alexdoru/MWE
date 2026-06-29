package fr.alexdoru.mwe.asm.hooks.mc.render;

import fr.alexdoru.mwe.config.MWEConfig;

public class RenderHook_ModernNametags {

    public static int modifyAlpha(int original) {
        return MWEConfig.modernNametags ? 0x80FFFFFF : original;
    }

}
