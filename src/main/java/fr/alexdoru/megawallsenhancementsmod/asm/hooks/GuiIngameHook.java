package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.fkcountermod.gui.FKCounterGui;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.LastWitherHPGui;

public class GuiIngameHook {

    public static String cancelHungerTitle(String subtitle) {
        if (subtitle.contains("Get to the middle to stop the hunger!")) {
            return "";
        }
        return subtitle;
    }

    public static void renderSiderbarGui(int x, int y, boolean textShadow, int lineNumber) {
        if (ConfigHandler.witherHUDinSiderbar && lineNumber == 6) {
            LastWitherHPGui.instance.renderinSidebar(x, y, textShadow);
            return;
        }
        if (ConfigHandler.FKHUDinSidebar && lineNumber == 11) {
            FKCounterGui.instance.renderinSidebar(x, y, textShadow);
        }
    }

}
