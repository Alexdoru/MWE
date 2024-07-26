package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;

@SuppressWarnings("unused")
public class GuiIngameHook_CancelHunger {

    public static String cancelHungerTitle(String subtitle) {
        if (MWEConfig.hideHungerTitleInMW && subtitle.contains("Get to the center to stop the hunger")) {
            return "";
        }
        return subtitle;
    }

}
