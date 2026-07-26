package fr.alexdoru.mwe.asm.hooks.mc.entity;

import fr.alexdoru.mwe.config.MWEConfig;

public class EntityPlayerHook_ItemInUseResetFix {

    public static int cancelForgeCode(int itemInUseCount) {
        return MWEConfig.fixForgeItemInUseReset ? 1 : itemInUseCount;
    }

}
