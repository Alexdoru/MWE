package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

public class GuiPlayerTabOverlayHook {

    private static final int FK_SCORE_WIDTH = Minecraft.getMinecraft().fontRendererObj.getStringWidth("00  ");

    public static int getFKScoreWidth() {
        return ConfigHandler.finalsInTablist ? (FKCounterMod.isInMwGame() ? FK_SCORE_WIDTH : 0) : 0;
    }

    // TODO le fks est pas aligné, injecter un deuxieme call pour draw le score ?
    public static String getScoretoRender(int playersFinals, int hpIn) {
        if(!ConfigHandler.finalsInTablist){
            return "" + getColoredHP(hpIn);
        }
        if (playersFinals != 0) {
            if (FKCounterMod.isInMwGame()) {
                return EnumChatFormatting.GOLD.toString() + playersFinals + (hpIn < 10 ? "   " : "  ") + getColoredHP(hpIn);
            }
        }
        return "" + getColoredHP(hpIn);
    }

    private static EnumChatFormatting getColoredHP(int healthPoints) {
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
