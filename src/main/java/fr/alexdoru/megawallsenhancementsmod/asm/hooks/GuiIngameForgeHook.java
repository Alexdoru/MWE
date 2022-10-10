package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

@SuppressWarnings("unused")
public class GuiIngameForgeHook {
    public static int adjustActionBarHeight(int y, int leftHeigth) {
        return 68 < leftHeigth ? y + 68 - leftHeigth : y;
    }
}
