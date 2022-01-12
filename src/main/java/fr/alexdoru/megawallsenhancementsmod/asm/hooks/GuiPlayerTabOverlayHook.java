package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

public class GuiPlayerTabOverlayHook {
    public static EnumChatFormatting getColoredHP(int healthPoints) {
        if (ConfigHandler.useColoredScores) {

            float maxhealth;
            float playerhealth = Minecraft.getMinecraft().thePlayer.getMaxHealth();

            if (FKCounterMod.isInMwGame() && playerhealth == 20f) {
                maxhealth = 40f;
            } else {
                maxhealth = playerhealth;
            }

            float ifloat = (float) healthPoints;

            if (ifloat > maxhealth) {
                return EnumChatFormatting.DARK_GREEN;
            } else if (ifloat > maxhealth * 3 / 4) {
                return EnumChatFormatting.GREEN;
            } else if (ifloat > maxhealth / 2) {
                return EnumChatFormatting.YELLOW;
            } else if (ifloat > maxhealth / 4) {
                return EnumChatFormatting.RED;
            } else {
                return EnumChatFormatting.DARK_RED;
            }

        } else {
            return EnumChatFormatting.YELLOW;
        }
    }
}
