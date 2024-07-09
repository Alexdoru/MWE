package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;

@SuppressWarnings("unused")
public class GuiPlayerTabOverlayHook_LongerTab {

    public static int getTablistHeight(int original) {
        return 25;
    }

    public static int getTotalPlayerAmount(int original) {
        return ConfigHandler.tablistSize;
    }

}
