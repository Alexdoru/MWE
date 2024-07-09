package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.utils.ColorUtil;
import net.minecraft.util.EnumChatFormatting;

@SuppressWarnings("unused")
public class GuiPlayerTabOverlayHook_ColoredScores {

    public static EnumChatFormatting getColoredHP(EnumChatFormatting original, int hp) {
        return ColorUtil.getColoredHP(original, hp);
    }

}
