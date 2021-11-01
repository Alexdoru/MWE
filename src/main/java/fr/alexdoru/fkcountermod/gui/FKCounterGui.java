package fr.alexdoru.fkcountermod.gui;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.config.EnumFKConfigSetting;
import fr.alexdoru.fkcountermod.gui.hudapi.HUDPosition;
import fr.alexdoru.fkcountermod.gui.hudapi.IRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Tuple;

import java.awt.*;
import java.util.HashMap;
import java.util.Map.Entry;

import static fr.alexdoru.fkcountermod.events.KillCounter.*;

public class FKCounterGui extends Gui implements IRenderer {

    public static FKCounterGui instance;
    private final HUDPosition hudPosition;
    private boolean dummy = false;
    private static String displayText = "";
    private final FontRenderer frObj = Minecraft.getMinecraft().fontRendererObj;

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

    public FKCounterGui() {
        instance = this;
        hudPosition = EnumFKConfigSetting.FKCOUNTER_HUD.getHUDPosition();
    }

    @Override
    public void save(HUDPosition pos) {
        int[] absolutePos = pos.getAbsolutePosition();
        EnumFKConfigSetting.FKCOUNTER_HUD.getHUDPosition().setAbsolute(absolutePos[0], absolutePos[1]);
        FKCounterMod.getConfigHandler().saveConfig();
    }

    @Override
    public HUDPosition getHUDPosition() {
        return this.hudPosition;
    }

    @Override
    public int getHeight() {
        if (EnumFKConfigSetting.COMPACT_HUD.getValue()) {
            return frObj.FONT_HEIGHT;
        } else {
            return frObj.FONT_HEIGHT * 4;
        }
    }

    @Override
    public int getWidth() {
        if (dummy) {
            if (EnumFKConfigSetting.COMPACT_HUD.getValue()) {
                return frObj.getStringWidth(DUMMY_TEXT_COMPACT);
            } else if (EnumFKConfigSetting.SHOW_PLAYERS.getValue()) {
                return getMultilineWidth(DUMMY_TEXT_PLAYERS);
            } else {
                return getMultilineWidth(DUMMY_TEXT);
            }
        }
        return getMultilineWidth(getDisplayText());
    }

    private int getMultilineWidth(String string) {
        int maxwidth = 0;
        for (String line : string.split("\n")) {
            int width = frObj.getStringWidth(line);
            if (width > maxwidth) {
                maxwidth = width;
            }
        }
        return maxwidth;
    }

    @Override
    public void render() {
        // TODO ca se décale pendant les games
        dummy = false;

        int[] absolutePos = this.hudPosition.getAbsolutePosition();
        int x = absolutePos[0];
        int y = absolutePos[1];

        if (EnumFKConfigSetting.DRAW_BACKGROUND.getValue()) {
            drawRect(x - 1, y - 1, x + getWidth(), y + getHeight(), new Color(0, 0, 0, 64).getRGB());
        }

        drawMultilineString(getDisplayText(), x, y);

    }

    @Override
    public void renderDummy() {

        dummy = true;
        int[] absolutePos = this.hudPosition.getAbsolutePosition();
        int x = absolutePos[0];
        int y = absolutePos[1];

        int width = getWidth();
        int height = getHeight();

        int XtopLeft = x - 2;
        int YtopLeft = y - 2;

        int XtopRight = x + width + 1;
        int YbotLeft = y + height;

        drawRect(XtopLeft, YtopLeft, XtopRight, YbotLeft, new Color(255, 255, 255, 127).getRGB());
        drawHorizontalLine(XtopLeft, XtopRight, YtopLeft, Color.RED.getRGB());
        drawHorizontalLine(XtopLeft, XtopRight, YbotLeft, Color.RED.getRGB());
        drawVerticalLine(XtopLeft, YtopLeft, YbotLeft, Color.RED.getRGB());
        drawVerticalLine(XtopRight, YtopLeft, YbotLeft, Color.RED.getRGB());

        if (EnumFKConfigSetting.COMPACT_HUD.getValue()) {
            drawMultilineString(DUMMY_TEXT_COMPACT, x, y);
        } else if (EnumFKConfigSetting.SHOW_PLAYERS.getValue()) {
            drawMultilineString(DUMMY_TEXT_PLAYERS, x, y);
        } else {
            drawMultilineString(DUMMY_TEXT, x, y);
        }
    }

    @Override
    public boolean isEnabled() {
        return (EnumFKConfigSetting.FKCOUNTER_HUD.getValue() && FKCounterMod.isInMwGame() && getGameId() != null);
    }

    private void drawMultilineString(String msg, int x, int y) {

        for (String line : msg.split("\n")) {
            if (EnumFKConfigSetting.TEXT_SHADOW.getValue()) {
                frObj.drawStringWithShadow(line, x, y, 0);
            } else {
                frObj.drawString(line, x, y, 0);
            }
            y += frObj.FONT_HEIGHT;
        }
    }

    private String getDisplayText() {
        return displayText;
    }

    public static void updateDisplayText() {

        if (getGameId() != null) {

            HashMap<Integer, Integer> sortedmap = getSortedTeamKillsMap();
            StringBuilder strBuilder = new StringBuilder();
            int i = 0;

            if (EnumFKConfigSetting.COMPACT_HUD.getValue()) {

                for (Entry<Integer, Integer> entry : sortedmap.entrySet()) {
                    strBuilder.append(i == 0 ? "" : EnumChatFormatting.DARK_GRAY + " / ")
                            .append(getColorPrefixFromTeam(entry.getKey()))
                            .append(entry.getValue());
                    i++;
                }

            } else if (EnumFKConfigSetting.SHOW_PLAYERS.getValue()) {

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
