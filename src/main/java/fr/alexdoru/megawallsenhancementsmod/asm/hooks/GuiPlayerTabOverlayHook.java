package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.utils.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class GuiPlayerTabOverlayHook {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final int FK_SCORE_WIDTH = mc.fontRendererObj.getStringWidth("00  ");

    public static int getFKScoreWidth() {
        return ConfigHandler.finalsInTablist ? (FKCounterMod.isInMwGame ? FK_SCORE_WIDTH : 0) : 0;
    }

    public static void renderFinals(int playersFinals, int x, int y) {
        if (!ConfigHandler.finalsInTablist || playersFinals == 0 || !FKCounterMod.isInMwGame) {
            return;
        }
        final String s1 = EnumChatFormatting.GOLD + "" + playersFinals;
        mc.fontRendererObj.drawStringWithShadow(s1, (float) (x - mc.fontRendererObj.getStringWidth(s1) - FK_SCORE_WIDTH), (float) y, 16777215);
    }

    public static List<String> addPlayerCountinHeader(List<String> listIn) {
        if (listIn != null) {
            final int i = Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap().size();
            if (i < 2) {
                return listIn;
            }
            final ArrayList<String> list = new ArrayList<>(listIn);
            list.add(0, EnumChatFormatting.GREEN + "Players: " + EnumChatFormatting.GOLD + i);
            return list;
        }
        return null;
    }

    public static EnumChatFormatting getColoredHP(int healthPoints) {
        if (ConfigHandler.useColoredScores) {
            final float maxHealthPoints;
            final float playerHealth = Minecraft.getMinecraft().thePlayer.getMaxHealth();
            if (FKCounterMod.isInMwGame && playerHealth == 20f) {
                maxHealthPoints = 40f;
            } else {
                maxHealthPoints = playerHealth;
            }
            return ColorUtil.getHPColor(maxHealthPoints, healthPoints);
        } else {
            return EnumChatFormatting.YELLOW;
        }
    }

}
