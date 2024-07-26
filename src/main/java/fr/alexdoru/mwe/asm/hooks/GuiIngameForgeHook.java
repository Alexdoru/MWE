package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;

@SuppressWarnings("unused")
public class GuiIngameForgeHook {
    public static int adjustActionBarHeight(int y, int leftHeigth) {
        return MWEConfig.fixActionbarTextOverlap && 68 < leftHeigth ? y + 68 - leftHeigth : y;
    }
}
