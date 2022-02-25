package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.asm.accessor.GameProfileAccessor;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.data.MWPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;

public class GuiPlayerTabOverlayHook {

    private static final FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
    private static final int FK_SCORE_WIDTH = fontRendererObj.getStringWidth("00  ");

    public static int getFKScoreWidth() {
        return ConfigHandler.finalsInTablist ? (FKCounterMod.isInMwGame ? FK_SCORE_WIDTH : 0) : 0;
    }

    public static void renderFinals(NetworkPlayerInfo networkPlayerInfo, int x, int y) {
        GameProfile gameProfile = networkPlayerInfo.getGameProfile();
        MWPlayerData mwPlayerData = ((GameProfileAccessor) gameProfile).getMWPlayerData();
        if (mwPlayerData == null) {
            return;
        }
        int playerFinalkills = mwPlayerData.playerFinalkills;
        if (!ConfigHandler.finalsInTablist || playerFinalkills == 0 || !FKCounterMod.isInMwGame) {
            return;
        }
        String s1 = EnumChatFormatting.GOLD + "" + playerFinalkills;
        fontRendererObj.drawStringWithShadow(s1, (float) (x - fontRendererObj.getStringWidth(s1) - FK_SCORE_WIDTH), (float) y, 16777215);
    }

    public static EnumChatFormatting getColoredHP(int healthPoints) {
        if (ConfigHandler.useColoredScores) {

            float maxhealth;
            float playerhealth = Minecraft.getMinecraft().thePlayer.getMaxHealth();

            if (FKCounterMod.isInMwGame && playerhealth == 20f) {
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
