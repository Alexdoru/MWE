package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

public class GuiIngameHook {
    public static String cancelHungerTitle(String subtitle) {
        if (subtitle.contains("Get to the middle to stop the hunger!")) {
            return "";
        }
        return subtitle;
    }
}
