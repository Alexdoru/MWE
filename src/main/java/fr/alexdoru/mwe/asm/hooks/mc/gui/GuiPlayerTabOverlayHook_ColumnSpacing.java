package fr.alexdoru.mwe.asm.hooks.mc.gui;

import fr.alexdoru.mwe.config.MWEConfig;

public class GuiPlayerTabOverlayHook_ColumnSpacing {

    public static int getColumnSpacing(int original) {
        return MWEConfig.tablistColumnSpacing;
    }

}
