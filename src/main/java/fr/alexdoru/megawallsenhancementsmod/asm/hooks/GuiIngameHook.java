package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.fkcountermod.gui.FKCounterGui;

public class GuiIngameHook {

    public static String cancelHungerTitle(String subtitle) {
        if (subtitle.contains("Get to the middle to stop the hunger!")) {
            return "";
        }
        return subtitle;
    }

    public static void renderSiderbarGui(int x, int y, boolean textShadow, int lineNumber) {
        if (lineNumber == 6) {
            //TODO call render wither hud
            return;
        }
        if (lineNumber == 11) {
            FKCounterGui.instance.renderinSidebar(x, y, textShadow);
        }
    }

}
