package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.features.FinalKillCounter;
import fr.alexdoru.megawallsenhancementsmod.features.SquadHandler;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.MapUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FKCounterHUD extends AbstractRenderer {

    public static FKCounterHUD instance;

    private static final List<String> DUMMY_TEXT = Arrays.asList(
            EnumChatFormatting.RED + "Red" + EnumChatFormatting.WHITE + ": 1",
            EnumChatFormatting.GREEN + "Green" + EnumChatFormatting.WHITE + ": 2",
            EnumChatFormatting.YELLOW + "Yellow" + EnumChatFormatting.WHITE + ": 3",
            EnumChatFormatting.BLUE + "Blue" + EnumChatFormatting.WHITE + ": 4");
    private static final String DUMMY_TEXT_COMPACT = EnumChatFormatting.RED + "1" + EnumChatFormatting.DARK_GRAY + " / "
            + EnumChatFormatting.GREEN + "2" + EnumChatFormatting.DARK_GRAY + " / "
            + EnumChatFormatting.YELLOW + "3" + EnumChatFormatting.DARK_GRAY + " / "
            + EnumChatFormatting.BLUE + "4";
    private static final List<String> DUMMY_TEXT_PLAYERS = Arrays.asList(
            EnumChatFormatting.RED + "R" + EnumChatFormatting.WHITE + " 12 :" + EnumChatFormatting.WHITE + " RedPlayer (5)",
            EnumChatFormatting.GREEN + "G" + EnumChatFormatting.WHITE + " 9 :" + EnumChatFormatting.WHITE + " GreenPlayer (4)",
            EnumChatFormatting.YELLOW + "Y" + EnumChatFormatting.WHITE + " 5 :" + EnumChatFormatting.WHITE + " YellowPlayer (3)",
            EnumChatFormatting.BLUE + "B" + EnumChatFormatting.WHITE + " 4 :" + EnumChatFormatting.WHITE + " BluePlayer (2)");
    private static final int BACKGROUND_COLOR = new Color(0, 0, 0, 96).getRGB();
    private static final int DUMMY_BACKGROUND_COLOR = new Color(255, 255, 255, 127).getRGB();

    public String displayText = "";
    private final List<String> textToRender = new ArrayList<>();

    public FKCounterHUD() {
        super(ConfigHandler.fkcounterHUDPosition);
        instance = this;
    }

    private int getHeight() {
        if (ConfigHandler.fkcounterHUDCompact) {
            return (int) (mc.fontRendererObj.FONT_HEIGHT * ConfigHandler.fkcounterHUDSize);
        } else {
            return (int) (mc.fontRendererObj.FONT_HEIGHT * 4 * ConfigHandler.fkcounterHUDSize);
        }
    }

    private int getWidth() {
        if (ConfigHandler.fkcounterHUDCompact) {
            return (int) (mc.fontRendererObj.getStringWidth(this.displayText) * ConfigHandler.fkcounterHUDSize);
        } else {
            return (int) (getMultilineWidth(this.textToRender) * ConfigHandler.fkcounterHUDSize);
        }
    }

    @Override
    public void render(ScaledResolution resolution) {
        this.guiPosition.updateAdjustedAbsolutePosition(resolution, getWidth(), getHeight());
        final int x = this.guiPosition.getAbsoluteRenderX();
        final int y = this.guiPosition.getAbsoluteRenderY();
        GlStateManager.pushMatrix();
        {
            if (ConfigHandler.fkcounterHUDDrawBackground) {
                drawRect(x - 2, y - 2, x + getWidth() + 1, y + getHeight(), BACKGROUND_COLOR);
            }
            GlStateManager.translate(x, y, 0);
            GlStateManager.scale(ConfigHandler.fkcounterHUDSize, ConfigHandler.fkcounterHUDSize, 1d);
            if (ConfigHandler.fkcounterHUDCompact) {
                mc.fontRendererObj.drawStringWithShadow(this.displayText, 0, 0, 0xFFFFFF);
            } else {
                drawStringList(this.textToRender);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void renderDummy() {

        final int x = this.guiPosition.getAbsoluteRenderX();
        final int y = this.guiPosition.getAbsoluteRenderY();

        final int width;
        if (ConfigHandler.fkcounterHUDCompact) {
            width = (int) (mc.fontRendererObj.getStringWidth(DUMMY_TEXT_COMPACT) * ConfigHandler.fkcounterHUDSize);
        } else if (ConfigHandler.fkcounterHUDShowPlayers) {
            width = (int) (getMultilineWidth(DUMMY_TEXT_PLAYERS) * ConfigHandler.fkcounterHUDSize);
        } else {
            width = (int) (getMultilineWidth(DUMMY_TEXT) * ConfigHandler.fkcounterHUDSize);
        }

        final int left = x - 2;
        final int top = y - 2;
        final int right = x + width + 1;
        final int bottom = y + getHeight();

        GlStateManager.pushMatrix();
        {
            drawRect(left, top, right, bottom, DUMMY_BACKGROUND_COLOR);
            drawHorizontalLine(left, right, top, Color.RED.getRGB());
            drawHorizontalLine(left, right, bottom, Color.RED.getRGB());
            drawVerticalLine(left, top, bottom, Color.RED.getRGB());
            drawVerticalLine(right, top, bottom, Color.RED.getRGB());
            GlStateManager.translate(x, y, 0);
            GlStateManager.scale(ConfigHandler.fkcounterHUDSize, ConfigHandler.fkcounterHUDSize, 1d);
            if (ConfigHandler.fkcounterHUDCompact) {
                mc.fontRendererObj.drawStringWithShadow(DUMMY_TEXT_COMPACT, 0, 0, 0xFFFFFF);
            } else if (ConfigHandler.fkcounterHUDShowPlayers) {
                drawStringList(DUMMY_TEXT_PLAYERS);
            } else {
                drawStringList(DUMMY_TEXT);
            }
        }
        GlStateManager.popMatrix();

    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return !ConfigHandler.fkcounterHUDinSidebar && ConfigHandler.showfkcounterHUD && ScoreboardTracker.isInMwGame && FinalKillCounter.getGameId() != null;
    }

    public void updateDisplayText() {

        if (FinalKillCounter.getGameId() == null) {
            return;
        }

        this.textToRender.clear();
        final Map<Integer, Integer> sortedmap = FinalKillCounter.getSortedTeamKillsMap();

        if (ConfigHandler.fkcounterHUDCompact) {

            boolean first = true;
            final StringBuilder strBuilder = new StringBuilder();
            for (final Entry<Integer, Integer> entry : sortedmap.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    strBuilder.append(EnumChatFormatting.DARK_GRAY).append(" / ");
                }
                strBuilder.append(FinalKillCounter.getColorPrefixFromTeam(entry.getKey())).append(entry.getValue());
            }
            this.displayText = strBuilder.toString();

        } else if (ConfigHandler.fkcounterHUDShowPlayers) {

            for (final Entry<Integer, Integer> teamEntry : sortedmap.entrySet()) {
                final StringBuilder strBuilder = new StringBuilder();
                final int team = teamEntry.getKey();
                strBuilder.append(FinalKillCounter.getColorPrefixFromTeam(team)).append(FinalKillCounter.getTeamNameFromTeam(team).charAt(0)).append(EnumChatFormatting.WHITE).append(" ").append(FinalKillCounter.getKills(team));
                final Map<String, Integer> teamKillMap = MapUtil.sortByDecreasingValue(FinalKillCounter.getPlayers(team));
                if (!teamKillMap.isEmpty()) {
                    int playerAmount = 0;
                    boolean first = true;
                    for (final Entry<String, Integer> playerEntry : teamKillMap.entrySet()) {
                        if (first) {
                            strBuilder.append(" : ");
                        } else {
                            strBuilder.append(" - ");
                        }
                        strBuilder.append(SquadHandler.getSquadname(playerEntry.getKey())).append(" (").append(playerEntry.getValue()).append(")");
                        playerAmount++;
                        if (playerAmount == ConfigHandler.fkcounterHUDPlayerAmount) {
                            break;
                        }
                        first = false;
                    }
                }
                this.textToRender.add(strBuilder.toString());
            }

        } else {

            for (final Entry<Integer, Integer> entry : sortedmap.entrySet()) {
                final StringBuilder strBuilder = new StringBuilder();
                final int team = entry.getKey();
                strBuilder.append(FinalKillCounter.getColorPrefixFromTeam(team))
                        .append(FinalKillCounter.getTeamNameFromTeam(team))
                        .append(EnumChatFormatting.WHITE).append(": ")
                        .append(FinalKillCounter.getKills(team));
                this.textToRender.add(strBuilder.toString());
            }

        }

    }

    private void drawStringList(List<String> list) {
        int y = 0;
        for (final String line : list) {
            mc.fontRendererObj.drawStringWithShadow(line, (float) 0, (float) y, 0xFFFFFF);
            y += mc.fontRendererObj.FONT_HEIGHT;
        }
    }

    private int getMultilineWidth(List<String> list) {
        int maxwidth = 0;
        for (final String line : list) {
            final int width = mc.fontRendererObj.getStringWidth(line);
            if (width > maxwidth) {
                maxwidth = width;
            }
        }
        return maxwidth;
    }

}
