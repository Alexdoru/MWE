package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.features.SquadHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.KillCounter;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.HashMap;
import java.util.Map.Entry;

public class FKCounterHUD extends MyCachedHUD {

    public static FKCounterHUD instance;

    /*used as an example when in the settings*/
    private static final String DUMMY_TEXT = EnumChatFormatting.RED + "Red" + EnumChatFormatting.WHITE + ": 1\n"
            + EnumChatFormatting.GREEN + "Green" + EnumChatFormatting.WHITE + ": 2\n"
            + EnumChatFormatting.YELLOW + "Yellow" + EnumChatFormatting.WHITE + ": 3\n"
            + EnumChatFormatting.BLUE + "Blue" + EnumChatFormatting.WHITE + ": 4";
    /*used as an example when in the settings*/
    private static final String DUMMY_TEXT_COMPACT = EnumChatFormatting.RED + "1" + EnumChatFormatting.DARK_GRAY + " / "
            + EnumChatFormatting.GREEN + "2" + EnumChatFormatting.DARK_GRAY + " / "
            + EnumChatFormatting.YELLOW + "3" + EnumChatFormatting.DARK_GRAY + " / "
            + EnumChatFormatting.BLUE + "4";
    /*used as an example when in the settings*/
    private static final String DUMMY_TEXT_PLAYERS = EnumChatFormatting.RED + "R" + EnumChatFormatting.WHITE + " 12 :" + EnumChatFormatting.WHITE + " RedPlayer (5)\n"
            + EnumChatFormatting.GREEN + "G" + EnumChatFormatting.WHITE + " 9 :" + EnumChatFormatting.WHITE + " GreenPlayer (4)\n"
            + EnumChatFormatting.YELLOW + "Y" + EnumChatFormatting.WHITE + " 5 :" + EnumChatFormatting.WHITE + " YellowPlayer (3)\n"
            + EnumChatFormatting.BLUE + "B" + EnumChatFormatting.WHITE + " 4 :" + EnumChatFormatting.WHITE + " BluePlayer (2)";
    private static final int BACKGROUND_COLOR = new Color(0, 0, 0, 96).getRGB();
    private static final int DUMMY_BACKGROUND_COLOR = new Color(255, 255, 255, 127).getRGB();

    public FKCounterHUD() {
        super(ConfigHandler.fkcounterHUDPosition);
        instance = this;
    }

    @Override
    public int getHeight() {
        if (ConfigHandler.fkcounterHUDCompact) {
            return (int) (frObj.FONT_HEIGHT * ConfigHandler.fkcounterHUDSize);
        } else {
            return (int) (frObj.FONT_HEIGHT * 4 * ConfigHandler.fkcounterHUDSize);
        }
    }

    @Override
    public int getWidth() {
        return (int) (getMultilineWidth(getDisplayText()) * ConfigHandler.fkcounterHUDSize);
    }

    @Override
    public void render(ScaledResolution resolution) {
        final int[] absolutePos = this.guiPosition.getAbsolutePosition(resolution);
        final int x = absolutePos[0];
        final int y = absolutePos[1];
        GlStateManager.pushMatrix();
        {
            if (ConfigHandler.fkcounterHUDDrawBackground) {
                drawRect(x - 2, y - 2, x + getWidth() + 1, y + getHeight(), BACKGROUND_COLOR);
            }
            GlStateManager.translate(x, y, 0);
            GlStateManager.scale(ConfigHandler.fkcounterHUDSize, ConfigHandler.fkcounterHUDSize, 0d);
            if (ConfigHandler.fkcounterHUDCompact) {
                frObj.drawString(getDisplayText(), 0, 0, 16777215, ConfigHandler.fkcounterHUDTextShadow);
            } else {
                drawMultilineString(getDisplayText(), 0, 0, ConfigHandler.fkcounterHUDTextShadow);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void renderDummy() {

        final int[] absolutePos = this.guiPosition.getAbsolutePosition();
        final int x = absolutePos[0];
        final int y = absolutePos[1];

        final int width;
        if (ConfigHandler.fkcounterHUDCompact) {
            width = (int) (frObj.getStringWidth(DUMMY_TEXT_COMPACT) * ConfigHandler.fkcounterHUDSize);
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
            GlStateManager.scale(ConfigHandler.fkcounterHUDSize, ConfigHandler.fkcounterHUDSize, 0d);
            if (ConfigHandler.fkcounterHUDCompact) {
                frObj.drawString(DUMMY_TEXT_COMPACT, 0, 0, 16777215, ConfigHandler.fkcounterHUDTextShadow);
            } else if (ConfigHandler.fkcounterHUDShowPlayers) {
                drawMultilineString(DUMMY_TEXT_PLAYERS, 0, 0, ConfigHandler.fkcounterHUDTextShadow);
            } else {
                drawMultilineString(DUMMY_TEXT, 0, 0, ConfigHandler.fkcounterHUDTextShadow);
            }
        }
        GlStateManager.popMatrix();

    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return !ConfigHandler.fkcounterHUDinSidebar && ConfigHandler.showfkcounterHUD && FKCounterMod.isInMwGame && KillCounter.getGameId() != null;
    }

    @Override
    public void updateDisplayText() {

        if (KillCounter.getGameId() != null) {

            final HashMap<Integer, Integer> sortedmap = KillCounter.getSortedTeamKillsMap();
            final StringBuilder strBuilder = new StringBuilder();
            int i = 0;

            if (ConfigHandler.fkcounterHUDCompact) {

                for (final Entry<Integer, Integer> entry : sortedmap.entrySet()) {
                    if (i != 0) {
                        strBuilder.append(EnumChatFormatting.DARK_GRAY).append(" / ");
                    }
                    strBuilder.append(KillCounter.getColorPrefixFromTeam(entry.getKey()))
                            .append(entry.getValue());
                    i++;
                }

            } else if (ConfigHandler.fkcounterHUDShowPlayers) {

                for (final Entry<Integer, Integer> teamEntry : sortedmap.entrySet()) {
                    final int team = teamEntry.getKey();
                    if (i != 0) {
                        strBuilder.append("\n");
                    }
                    strBuilder.append(KillCounter.getColorPrefixFromTeam(team)).append(KillCounter.getTeamNameFromTeam(team).charAt(0)).append(EnumChatFormatting.WHITE).append(" ").append(KillCounter.getKills(team));
                    final HashMap<String, Integer> teamkillsmap = KillCounter.sortByDecreasingValue1(KillCounter.getPlayers(team));
                    if (!teamkillsmap.isEmpty()) {
                        int playerAmount = 0;
                        boolean isFirst = true;
                        for (final Entry<String, Integer> playerEntry : teamkillsmap.entrySet()) {
                            if (isFirst) {
                                strBuilder.append(" : ");
                            } else {
                                strBuilder.append(" - ");
                            }
                            strBuilder.append(SquadHandler.getSquadname(playerEntry.getKey())).append(" (").append(playerEntry.getValue()).append(")");
                            playerAmount++;
                            if (playerAmount == ConfigHandler.fkcounterHUDPlayerAmount) {
                                break;
                            }
                            isFirst = false;
                        }
                    }
                    i++;
                }

            } else {

                for (final Entry<Integer, Integer> entry : sortedmap.entrySet()) {
                    final int team = entry.getKey();
                    if (i != 0) {
                        strBuilder.append("\n");
                    }
                    strBuilder.append(KillCounter.getColorPrefixFromTeam(team))
                            .append(KillCounter.getTeamNameFromTeam(team))
                            .append(EnumChatFormatting.WHITE).append(": ")
                            .append(KillCounter.getKills(team));
                    i++;
                }

            }

            displayText = strBuilder.toString();

        }

    }

}
