package fr.alexdoru.mwe.api.utils;

import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

public final class ColorUtil {

    private ColorUtil() {}

    public static EnumChatFormatting getHPColorForTablist(EnumChatFormatting original, int hp) {
        if (MWEConfig.coloredScoresInTablist) {
            final float maxHP;
            if (ScoreboardTracker.isInMwGame()) {
                maxHP = 44f;
            } else {
                maxHP = Minecraft.getMinecraft().thePlayer.getMaxHealth();
            }
            return ColorUtil.getHPColor(maxHP, hp);
        }
        return original;
    }

    /**
     * Gets the HP color depending on the HP input
     */
    public static EnumChatFormatting getHPColor(float maxHP, float hp) {
        if (hp > maxHP) {
            return EnumChatFormatting.DARK_GREEN;
        } else if (hp > maxHP * 3f / 4f) {
            return EnumChatFormatting.GREEN;
        } else if (hp > maxHP / 2f) {
            return EnumChatFormatting.YELLOW;
        } else if (hp > maxHP / 4f) {
            return EnumChatFormatting.RED;
        } else {
            return EnumChatFormatting.DARK_RED;
        }
    }

    public static int getColorInt(EnumChatFormatting color) {
        return getColorInt(color.toString().charAt(1));
    }

    public static int getColorInt(char colorChar) {
        if (colorChar == '\0') {
            return 0xFFFFFF;
        }
        return Minecraft.getMinecraft().fontRendererObj.getColorCode(colorChar);
    }

}
