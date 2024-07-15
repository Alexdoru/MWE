package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.ConfigHandler;

@SuppressWarnings("unused")
public class GuiPlayerTabOverlayHook_ColumnSpacing {

    public static int getColumnSpacing(int original) {
        return ConfigHandler.tablistColumnSpacing;
    }

}
