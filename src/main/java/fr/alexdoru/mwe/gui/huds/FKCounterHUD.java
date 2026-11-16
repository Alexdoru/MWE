package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.api.enums.MWTeam;
import fr.alexdoru.mwe.api.utils.MapUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.features.SquadHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FKCounterHUD extends AbstractRenderer {

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
    private static final int PADDING_LEFT = 2;
    private static final int PADDING_RIGHT = 1;
    private static final int PADDING_TOP = 2;

    private String displayText = "";
    private final List<String> textToRender = new ArrayList<>();

    public FKCounterHUD() {
        super(MWEConfig.fkcounterHUDPosition);
    }

    private int getHeight() {
        if (MWEConfig.fkcounterHUDCompact || MWEConfig.fkcounterHUDinSidebar) {
            return mc.fontRendererObj.FONT_HEIGHT;
        }
        return mc.fontRendererObj.FONT_HEIGHT * 4;
    }

    private int getWidth() {
        if (MWEConfig.fkcounterHUDCompact || MWEConfig.fkcounterHUDinSidebar) {
            return mc.fontRendererObj.getStringWidth(this.displayText);
        }
        return getMultilineWidth(this.textToRender);
    }

    @Override
    public void render(ScaledResolution resolution) {
        final int width = getWidth() + PADDING_LEFT + PADDING_RIGHT;
        final int height = getHeight() + PADDING_TOP;
        this.rendererPosition.applyTransform(resolution, width, height);
        if (MWEConfig.fkcounterHUDDrawBackground) {
            drawRect(0, 0, width, height, BACKGROUND_COLOR);
        }
        if (MWEConfig.fkcounterHUDCompact) {
            mc.fontRendererObj.drawStringWithShadow(this.displayText, PADDING_LEFT, PADDING_TOP, 0xFFFFFF);
        } else {
            drawStringList(this.textToRender, PADDING_LEFT, PADDING_TOP);
        }
    }

    @Override
    public void renderDummy(ScaledResolution resolution) {
        final int textWidth;
        if (MWEConfig.fkcounterHUDCompact || MWEConfig.fkcounterHUDinSidebar) {
            textWidth = mc.fontRendererObj.getStringWidth(DUMMY_TEXT_COMPACT);
        } else if (MWEConfig.fkcounterHUDShowPlayers) {
            textWidth = getMultilineWidth(DUMMY_TEXT_PLAYERS);
        } else {
            textWidth = getMultilineWidth(DUMMY_TEXT);
        }
        final int width = textWidth + PADDING_LEFT + PADDING_RIGHT;
        final int height = getHeight() + PADDING_TOP;
        this.rendererPosition.applyTransform(resolution, width, height);
        drawRect(0, 0, width, height, DUMMY_BACKGROUND_COLOR);
        if (MWEConfig.fkcounterHUDCompact || MWEConfig.fkcounterHUDinSidebar) {
            mc.fontRendererObj.drawStringWithShadow(DUMMY_TEXT_COMPACT, PADDING_LEFT, PADDING_TOP, 0xFFFFFF);
        } else if (MWEConfig.fkcounterHUDShowPlayers) {
            drawStringList(DUMMY_TEXT_PLAYERS, PADDING_LEFT, PADDING_TOP);
        } else {
            drawStringList(DUMMY_TEXT, PADDING_LEFT, PADDING_TOP);
        }
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return !MWEConfig.fkcounterHUDinSidebar
                && this.rendererPosition.isEnabled()
                && this.parser.isInMwGame()
                && MWE.INSTANCE().getFinalKillCounter() != null;
    }

    public void updateDisplayText() {

        final FinalKillCounter fkCounter = MWE.INSTANCE().getFinalKillCounter();
        if (fkCounter == null) {
            return;
        }

        this.textToRender.clear();
        final List<Map.Entry<MWTeam, Integer>> sortedKillList = fkCounter.getSortedTeamKillsList();

        if (MWEConfig.fkcounterHUDCompact || MWEConfig.fkcounterHUDinSidebar) {

            boolean first = true;
            final StringBuilder strBuilder = new StringBuilder();
            for (final Entry<MWTeam, Integer> entry : sortedKillList) {
                if (first) {
                    first = false;
                } else {
                    strBuilder.append(EnumChatFormatting.DARK_GRAY).append(" / ");
                }
                strBuilder.append(fkCounter.getColorPrefixOfTeam(entry.getKey())).append(entry.getValue());
            }
            this.displayText = strBuilder.toString();

        } else if (MWEConfig.fkcounterHUDShowPlayers) {

            for (final Entry<MWTeam, Integer> teamEntry : sortedKillList) {
                final StringBuilder strBuilder = new StringBuilder();
                final MWTeam team = teamEntry.getKey();
                strBuilder.append(fkCounter.getColorPrefixOfTeam(team)).append(team.getName().charAt(0)).append(EnumChatFormatting.WHITE).append(" ").append(fkCounter.getKillsOfTeam(team));
                final List<Map.Entry<String, Integer>> teamKillList = MapUtil.sortByValueReversed(fkCounter.getKillMapOfTeam(team));
                if (!teamKillList.isEmpty()) {
                    int playerAmount = 0;
                    boolean first = true;
                    for (final Entry<String, Integer> playerEntry : teamKillList) {
                        if (first) {
                            strBuilder.append(" : ");
                        } else {
                            strBuilder.append(" - ");
                        }
                        strBuilder.append(SquadHandler.getSquadname(playerEntry.getKey())).append(" (").append(playerEntry.getValue()).append(")");
                        playerAmount++;
                        if (playerAmount == MWEConfig.fkcounterHUDPlayerAmount) {
                            break;
                        }
                        first = false;
                    }
                }
                this.textToRender.add(strBuilder.toString());
            }

        } else {

            for (final Entry<MWTeam, Integer> entry : sortedKillList) {
                final StringBuilder strBuilder = new StringBuilder();
                final MWTeam team = entry.getKey();
                strBuilder.append(fkCounter.getColorPrefixOfTeam(team))
                        .append(team.getName())
                        .append(EnumChatFormatting.WHITE).append(": ")
                        .append(fkCounter.getKillsOfTeam(team));
                this.textToRender.add(strBuilder.toString());
            }

        }

    }

    public String getDisplayText() {
        return displayText;
    }

    private void drawStringList(List<String> list, int x, int y) {
        final FontRenderer fr = mc.fontRendererObj;
        for (final String line : list) {
            fr.drawStringWithShadow(line, x, y, 0xFFFFFF);
            y += fr.FONT_HEIGHT;
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
