package fr.alexdoru.mwe.utils;

import net.minecraft.util.EnumChatFormatting;

public final class PrestigeColorHelper {

    private PrestigeColorHelper() {}

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

}
