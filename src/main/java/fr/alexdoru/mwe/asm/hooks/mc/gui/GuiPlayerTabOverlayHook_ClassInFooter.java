package fr.alexdoru.mwe.asm.hooks.mc.gui;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.gui.huds.ClassInLobbyHUD;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GuiPlayerTabOverlayHook_ClassInFooter {

    private static final String PLACEHOLDER = "mwe$placeholder";

    private static int textWidth;
    private static boolean skipDraw;

    @Nullable
    public static List<String> modifyList(@Nullable List<String> listIn) {
        final ClassInLobbyHUD classyHUD = MWE.INSTANCE().getMweRenderers().classInLobbyHUD;
        if (classyHUD.isTabDisplayEnabled()) {
            classyHUD.updateData();
            final int lines = classyHUD.getLinesCount();
            if (lines != 0) {
                textWidth = classyHUD.getWidth();
                skipDraw = false;
                return addToList(listIn, lines);
            }
        }
        textWidth = 0;
        return listIn;
    }

    public static int modifyWidth(int original) {
        return Math.max(original, textWidth);
    }

    public static int drawString(FontRenderer fr, String text, float x, float y, int color, int width) {
        if (textWidth == 0 || !PLACEHOLDER.equals(text)) {
            return fr.drawStringWithShadow(text, x, y, color);
        } else {
            if (skipDraw) {
                return 0;
            }
            MWE.INSTANCE().getMweRenderers().classInLobbyHUD.renderAt((width - textWidth) / 2, MathHelper.floor_float(y));
            skipDraw = true;
            return 0;
        }
    }

    private static List<String> addToList(@Nullable List<String> listIn, int lines) {
        final List<String> list = new ArrayList<>((listIn == null ? 0 : listIn.size()) + lines);
        for (int i = 0; i < lines; i++) {
            list.add(PLACEHOLDER);
        }
        if (listIn != null) {
            list.addAll(listIn);
        }
        return list;
    }

}
