package fr.alexdoru.megawallsenhancementsmod.utils;

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

    /**
     * Gets the HP color depending on the HP input
     */
    public static EnumChatFormatting getHPColor(float maxHealthPoints, float healthPoints) {
        if (healthPoints > maxHealthPoints) {
            return EnumChatFormatting.DARK_GREEN;
        } else if (healthPoints > maxHealthPoints * 3f / 4f) {
            return EnumChatFormatting.GREEN;
        } else if (healthPoints > maxHealthPoints / 2f) {
            return EnumChatFormatting.YELLOW;
        } else if (healthPoints > maxHealthPoints / 4f) {
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
