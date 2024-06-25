package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class GuiPlayerTabOverlayHook {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static int finalsScoreWidth = 0;

    public static void resetFinalsScoreWidth() {
        finalsScoreWidth = 0;
    }

    public static void computeFKScoreWidth(int playerFinalkills) {
        if (ScoreboardTracker.isInMwGame() && ConfigHandler.fkcounterHUDTablist) {
            if (playerFinalkills != 0) {
                finalsScoreWidth = Math.max(finalsScoreWidth, mc.fontRendererObj.getStringWidth(" " + playerFinalkills));
            }
        }
    }

    public static int getRenderScoreWidth() {
        return finalsScoreWidth;
    }

    public static void renderFinals(int playerFinalkills, int j2, int i, int k2) {
        if (!ConfigHandler.fkcounterHUDTablist || playerFinalkills == 0 || !ScoreboardTracker.isInMwGame()) {
            return;
        }
        final String s1 = EnumChatFormatting.GOLD + " " + playerFinalkills;
        mc.fontRendererObj.drawStringWithShadow(s1, j2 + i + 1, k2, 0xFFFFFF);
    }

    public static boolean shouldRenderHeader() {
        return ConfigHandler.showPlayercountTablist || !shouldHideFooter();
    }

    public static boolean shouldHideFooter() {
        if (ConfigHandler.showHeaderFooterOutsideMW && !ScoreboardTracker.isMWEnvironement()) {
            return false;
        }
        return ConfigHandler.hideTablistHeaderFooter;
    }

    public static int getTablistHeight() {
        return 25;
    }

    public static int getTotalPlayerAmount() {
        return ConfigHandler.tablistSize;
    }

    @Nonnull
    public static List<String> addPlayerCountInHeader(@Nonnull List<String> listIn) {
        if (!ConfigHandler.showPlayercountTablist) {
            return listIn;
        }
        final int i = mc.thePlayer.sendQueue.getPlayerInfoMap().size();
        if (i < 2) {
            return shouldHideFooter() ? new ArrayList<>() : listIn;
        }
        final List<String> list;
        if (shouldHideFooter()) {
            list = new ArrayList<>();
        } else {
            list = new ArrayList<>(listIn);
        }
        list.add(0, EnumChatFormatting.GREEN + "Players: " + EnumChatFormatting.GOLD + i);
        return list;
    }

    private static boolean drawPing = true;

    public static int getPingWidth(List<NetworkPlayerInfo> list) { // called once per frame
        if (ConfigHandler.hidePingTablist) {
            if (ScoreboardTracker.isInMwGame()) {
                drawPing = false;
                return 0;
            }
            drawPing = !list.stream().allMatch(it -> it.getResponseTime() <= 1);
            return drawPing ? 13 : 0;
        }
        drawPing = true;
        return 13;
    }

    public static boolean shouldDrawPing() { //called n times per frame
        return drawPing;
    }

    public static int fixMissplacedDrawRect(int l1) {
        return l1 % 2;
    }

    public static EnumChatFormatting getColoredHP(int healthPoints) {
        if (ConfigHandler.useColoredScores) {
            final float maxHealthPoints;
            if (ScoreboardTracker.isInMwGame()) {
                maxHealthPoints = 44f;
            } else {
                maxHealthPoints = mc.thePlayer.getMaxHealth();
            }
            return ColorUtil.getHPColor(maxHealthPoints, healthPoints);
        } else {
            return EnumChatFormatting.YELLOW;
        }
    }

}
