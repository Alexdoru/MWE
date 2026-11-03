package fr.alexdoru.mwe.asm.hooks.mc.gui;

import fr.alexdoru.mwe.api.utils.ColorUtil;
import net.minecraft.util.EnumChatFormatting;

public class GuiPlayerTabOverlayHook_ColoredScores {

    public static EnumChatFormatting getColoredHP(EnumChatFormatting original, int hp) {
        return ColorUtil.getHPColorForTablist(original, hp);
    }

}
