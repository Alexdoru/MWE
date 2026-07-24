package fr.alexdoru.mwe.asm.hooks.mc.render;

import fr.alexdoru.mwe.config.MWEConfig;

public class LayerArmorBaseHook_HitColor {

    public static boolean shouldCombineTextures(boolean original) {
        return original || MWEConfig.colorArmorWhenHurt;
    }

}
