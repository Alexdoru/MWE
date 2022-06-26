package fr.alexdoru.fkcountermod.gui;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.SquadEvent;
import fr.alexdoru.megawallsenhancementsmod.gui.MyCachedGui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.HashMap;
import java.util.Map.Entry;

import static fr.alexdoru.fkcountermod.events.KillCounter.*;

public class FKCounterGui extends MyCachedGui {

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
    public static FKCounterGui instance;
    private final int BACKGROUND_COLOR = new Color(0, 0, 0, 96).getRGB();
    private final int DUMMY_BACKGROUND_COLOR = new Color(255, 255, 255, 127).getRGB();

    public FKCounterGui() {
        instance = this;
        guiPosition = ConfigHandler.fkcounterPosition;
    }

    @Override
    public int getHeight() {
        if (ConfigHandler.compact_hud) {
            return (int) (frObj.FONT_HEIGHT * ConfigHandler.fkc_hud_size);
        } else {
            return (int) (frObj.FONT_HEIGHT * 4 * ConfigHandler.fkc_hud_size);
        }
    }

    @Override
    public int getWidth() {
        return (int) (getMultilineWidth(getDisplayText()) * ConfigHandler.fkc_hud_size);
    }

    @Override
    public void render() {
        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        int x = absolutePos[0];
        int y = absolutePos[1];
        if (ConfigHandler.draw_background) {
            drawRect(x - 2, y - 2, x + getWidth() + 1, y + getHeight(), BACKGROUND_COLOR);
        }
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, 0);
            GlStateManager.scale(ConfigHandler.fkc_hud_size, ConfigHandler.fkc_hud_size, 0d);
            if (ConfigHandler.compact_hud) {
                frObj.drawString(getDisplayText(), 0, 0, 16777215, ConfigHandler.text_shadow);
            } else {
                drawMultilineString(getDisplayText(), 0, 0, ConfigHandler.text_shadow);
            }
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void renderDummy() {

        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        int x = absolutePos[0];
        int y = absolutePos[1];

        int width;
        if (ConfigHandler.compact_hud) {
            width = (int) (frObj.getStringWidth(DUMMY_TEXT_COMPACT) * ConfigHandler.fkc_hud_size);
        } else if (ConfigHandler.show_players) {
            width = (int) (getMultilineWidth(DUMMY_TEXT_PLAYERS) * ConfigHandler.fkc_hud_size);
        } else {
            width = (int) (getMultilineWidth(DUMMY_TEXT) * ConfigHandler.fkc_hud_size);
        }

        int left = x - 2;
        int top = y - 2;
        int right = x + width + 1;
        int bottom = y + getHeight();

        drawRect(left, top, right, bottom, DUMMY_BACKGROUND_COLOR);
        drawHorizontalLine(left, right, top, Color.RED.getRGB());
        drawHorizontalLine(left, right, bottom, Color.RED.getRGB());
        drawVerticalLine(left, top, bottom, Color.RED.getRGB());
        drawVerticalLine(right, top, bottom, Color.RED.getRGB());

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, 0);
            GlStateManager.scale(ConfigHandler.fkc_hud_size, ConfigHandler.fkc_hud_size, 0d);
            if (ConfigHandler.compact_hud) {
                frObj.drawString(DUMMY_TEXT_COMPACT, 0, 0, 16777215, ConfigHandler.text_shadow);
            } else if (ConfigHandler.show_players) {
                drawMultilineString(DUMMY_TEXT_PLAYERS, 0, 0, ConfigHandler.text_shadow);
            } else {
                drawMultilineString(DUMMY_TEXT, 0, 0, ConfigHandler.text_shadow);
            }
        }
        GlStateManager.popMatrix();

    }

    @Override
    public boolean isEnabled() {
        return (!ConfigHandler.FKHUDinSidebar && ConfigHandler.show_fkcHUD && FKCounterMod.isInMwGame && getGameId() != null);
    }

    @Override
    public void updateDisplayText() {

        if (getGameId() != null) {

            HashMap<Integer, Integer> sortedmap = getSortedTeamKillsMap();
            StringBuilder strBuilder = new StringBuilder();
            int i = 0;

            if (ConfigHandler.compact_hud) {

                for (Entry<Integer, Integer> entry : sortedmap.entrySet()) {
                    if (i != 0) {
                        strBuilder.append(EnumChatFormatting.DARK_GRAY).append(" / ");
                    }
                    strBuilder.append(getColorPrefixFromTeam(entry.getKey()))
                            .append(entry.getValue());
                    i++;
                }

            } else if (ConfigHandler.show_players) {

                for (Entry<Integer, Integer> teamEntry : sortedmap.entrySet()) {
                    int team = teamEntry.getKey();
                    if (i != 0) {
                        strBuilder.append("\n");
                    }
                    strBuilder.append(getColorPrefixFromTeam(team)).append(getTeamNameFromTeam(team).charAt(0)).append(EnumChatFormatting.WHITE).append(" ").append(getKills(team));
                    HashMap<String, Integer> teamkillsmap = KillCounter.sortByDecreasingValue1(KillCounter.getPlayers(team));
                    if (!teamkillsmap.isEmpty()) {
                        int playerAmount = 0;
                        boolean isFirst = true;
                        for (Entry<String, Integer> playerEntry : teamkillsmap.entrySet()) {
                            if (isFirst) {
                                strBuilder.append(" : ");
                            } else {
                                strBuilder.append(" - ");
                            }
                            String squadname = SquadEvent.getSquad().get(playerEntry.getKey());
                            if (squadname != null) {
                                strBuilder.append(squadname).append(" (").append(playerEntry.getValue()).append(")");
                            } else {
                                strBuilder.append(playerEntry.getKey()).append(" (").append(playerEntry.getValue()).append(")");
                            }
                            playerAmount++;
                            if (playerAmount == ConfigHandler.playerAmount) {
                                break;
                            }
                            isFirst = false;
                        }
                    }
                    i++;
                }

            } else {

                for (Entry<Integer, Integer> entry : sortedmap.entrySet()) {
                    int team = entry.getKey();
                    if (i != 0) {
                        strBuilder.append("\n");
                    }
                    strBuilder.append(getColorPrefixFromTeam(team))
                            .append(getTeamNameFromTeam(team))
                            .append(EnumChatFormatting.WHITE).append(": ")
                            .append(getKills(team));
                    i++;
                }

            }

            displayText = strBuilder.toString();

        }

    }

}
