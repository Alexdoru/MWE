package fr.alexdoru.mwe.asm.hooks;

public class GuiPlayerTabOverlayHook_FixDrawRect {

    public static int fixDrawRectHeight(int i) {
        return i - 1;
    }

    public static int fixDrawRectWidth(int l1) {
        return l1 % 2;
    }

}
