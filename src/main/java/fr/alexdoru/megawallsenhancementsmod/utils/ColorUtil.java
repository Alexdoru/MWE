package fr.alexdoru.megawallsenhancementsmod.utils;

import net.minecraft.util.EnumChatFormatting;

public class ColorUtil {

    public static EnumChatFormatting getPrestigeVColor(int classpoints) {
        if (classpoints < 2000) {
            return EnumChatFormatting.GRAY;
        } else if (classpoints < 10000) {
            return EnumChatFormatting.GOLD;
        } else if (classpoints < 13000) {
            return EnumChatFormatting.DARK_PURPLE;
        } else if (classpoints < 19000) {
            return EnumChatFormatting.DARK_BLUE;
        } else if (classpoints < 28000) {
            return EnumChatFormatting.DARK_AQUA;
        } else if (classpoints < 40000) {
            return EnumChatFormatting.DARK_GREEN;
        } else {
            return EnumChatFormatting.DARK_RED;
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

}
