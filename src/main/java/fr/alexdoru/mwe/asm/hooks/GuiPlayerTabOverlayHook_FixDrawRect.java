package fr.alexdoru.mwe.asm.hooks;

@SuppressWarnings("unused")
public class GuiPlayerTabOverlayHook_FixDrawRect {

    public static int fixDrawRect(int l1) {
        return l1 % 2;
    }

}
