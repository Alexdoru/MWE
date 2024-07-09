package fr.alexdoru.mwe.utils;

import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

public class ColorUtil {

    public static EnumChatFormatting getPrestige4Color(int classpoints) {
        if (classpoints >= 40_000) {
            return EnumChatFormatting.BLACK;
        } else if (classpoints >= 30_000) {
            return EnumChatFormatting.DARK_RED;
        } else if (classpoints >= 25_000) {
            return EnumChatFormatting.DARK_PURPLE;
        } else if (classpoints >= 20_000) {
            return EnumChatFormatting.DARK_BLUE;
        } else if (classpoints >= 18_000) {
            return EnumChatFormatting.DARK_AQUA;
        } else if (classpoints >= 16_000) {
            return EnumChatFormatting.DARK_GREEN;
        } else if (classpoints >= 14_000) {
            return EnumChatFormatting.YELLOW;
        } else if (classpoints >= 12_000) {
            return EnumChatFormatting.BLUE;
        } else if (classpoints >= 10_000) {
            return EnumChatFormatting.RED;
        } else if (classpoints >= 8_000) {
            return EnumChatFormatting.LIGHT_PURPLE;
        } else if (classpoints >= 6_000) {
            return EnumChatFormatting.AQUA;
        } else if (classpoints >= 4_000) {
            return EnumChatFormatting.GREEN;
        } else if (classpoints >= 2_000) {
            return EnumChatFormatting.GOLD;
        } else {
            return EnumChatFormatting.GRAY;
        }
    }

    public static EnumChatFormatting getColoredHP(EnumChatFormatting original, int hp) {
        if (ConfigHandler.useColoredScores) {
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

    public static int getColorInt(char colorChar) {
        if (colorChar == '\0') {
            return 0xFFFFFF;
        }
        return Minecraft.getMinecraft().fontRendererObj.getColorCode(colorChar);
    }

}
