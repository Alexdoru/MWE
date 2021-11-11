package fr.alexdoru.fkcountermod.gui;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.MyCachedGui;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Tuple;

import java.awt.*;
import java.util.HashMap;
import java.util.Map.Entry;

import static fr.alexdoru.fkcountermod.events.KillCounter.*;

public class FKCounterGui extends MyCachedGui {

    public static FKCounterGui instance;

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
    private static final String DUMMY_TEXT_PLAYERS = EnumChatFormatting.RED + "Red" + EnumChatFormatting.WHITE + ": 5 - RedPlayer 1\n"
            + EnumChatFormatting.GREEN + "Green" + EnumChatFormatting.WHITE + ": 12 - GreenPlayer 2\n"
            + EnumChatFormatting.YELLOW + "Yellow" + EnumChatFormatting.WHITE + ": 6 - YellowPlayer 3\n"
            + EnumChatFormatting.BLUE + "Blue" + EnumChatFormatting.WHITE + ": 9 - BluePlayer 4";

    private final int BACKGROUND_COLOR = new Color(0, 0, 0, 64).getRGB();
    private final int DUMMY_BACKGROUND_COLOR = new Color(255, 255, 255, 127).getRGB();

    public FKCounterGui() {
        instance = this;
        guiPosition = ConfigHandler.fkcounterPosition;
    }

    @Override
    public int getHeight() {
        if (ConfigHandler.compact_hud) {
            return frObj.FONT_HEIGHT;
        } else {
            return frObj.FONT_HEIGHT * 4;
        }
    }

    @Override
    public int getWidth() {
        if (isRenderingDummy) {
            if (ConfigHandler.compact_hud) {
                return frObj.getStringWidth(DUMMY_TEXT_COMPACT);
            } else if (ConfigHandler.show_players) {
                return getMultilineWidth(DUMMY_TEXT_PLAYERS);
            } else {
                return getMultilineWidth(DUMMY_TEXT);
            }
        }
        return getMultilineWidth(getDisplayText());
    }

    @Override
    public void render() {
        // FIXME ca se décale pendant les games
        super.render();

        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        int x = absolutePos[0];
        int y = absolutePos[1];

        if (ConfigHandler.draw_background) {
            drawRect(x - 1, y - 1, x + getWidth(), y + getHeight(), BACKGROUND_COLOR);
        }

        if (ConfigHandler.compact_hud) {
            frObj.drawString(getDisplayText(), x, y, 0, ConfigHandler.text_shadow);
        } else {
            drawMultilineString(getDisplayText(), x, y, ConfigHandler.text_shadow);
        }

    }

    @Override
    public void renderDummy() {

        super.renderDummy();

        int[] absolutePos = this.guiPosition.getAbsolutePosition();
        int x = absolutePos[0];
        int y = absolutePos[1];

        int width = getWidth();
        int height = getHeight();

        int XtopLeft = x - 2;
        int YtopLeft = y - 2;

        int XtopRight = x + width + 1;
        int YbotLeft = y + height;

        drawRect(XtopLeft, YtopLeft, XtopRight, YbotLeft, DUMMY_BACKGROUND_COLOR);
        drawHorizontalLine(XtopLeft, XtopRight, YtopLeft, Color.RED.getRGB());
        drawHorizontalLine(XtopLeft, XtopRight, YbotLeft, Color.RED.getRGB());
        drawVerticalLine(XtopLeft, YtopLeft, YbotLeft, Color.RED.getRGB());
        drawVerticalLine(XtopRight, YtopLeft, YbotLeft, Color.RED.getRGB());

        if (ConfigHandler.compact_hud) {
            frObj.drawString(DUMMY_TEXT_COMPACT, x, y, 0, ConfigHandler.text_shadow);
        } else if (ConfigHandler.show_players) {
            drawMultilineString(DUMMY_TEXT_PLAYERS, x, y, ConfigHandler.text_shadow);
        } else {
            drawMultilineString(DUMMY_TEXT, x, y, ConfigHandler.text_shadow);
        }

    }

    @Override
    public boolean isEnabled() {
        return (ConfigHandler.show_fkcHUD && FKCounterMod.isInMwGame() && getGameId() != null);
    }

    @Override
    public void updateDisplayText() {

        if (getGameId() != null) {

            HashMap<Integer, Integer> sortedmap = getSortedTeamKillsMap();
            StringBuilder strBuilder = new StringBuilder();
            int i = 0;

            if (ConfigHandler.compact_hud) {

                for (Entry<Integer, Integer> entry : sortedmap.entrySet()) {
                    strBuilder.append(i == 0 ? "" : EnumChatFormatting.DARK_GRAY + " / ")
                            .append(getColorPrefixFromTeam(entry.getKey()))
                            .append(entry.getValue());
                    i++;
                }

            } else if (ConfigHandler.show_players) {// TODO add support for nick hider

                for (Entry<Integer, Integer> entry : sortedmap.entrySet()) {
                    int team = entry.getKey();
                    Tuple<String, Integer> tuple = getHighestFinalsPlayerOfTeam(team);
                    strBuilder.append(i == 0 ? "" : "\n")
                            .append(getColorPrefixFromTeam(team))
                            .append(getTeamNameFromTeam(team)).append(EnumChatFormatting.WHITE)
                            .append(": ").append(getKills(team))
                            .append(tuple == null ? "" : " - " + tuple.getFirst() + " " + tuple.getSecond());
                    i++;
                }

            } else {

                for (Entry<Integer, Integer> entry : sortedmap.entrySet()) {
                    int team = entry.getKey();
                    strBuilder.append(i == 0 ? "" : "\n")
                            .append(getColorPrefixFromTeam(team))
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
