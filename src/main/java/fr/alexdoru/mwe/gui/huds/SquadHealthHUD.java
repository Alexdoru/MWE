package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.api.ISquadInfoRenderer;
import fr.alexdoru.mwe.asm.interfaces.NetworkPlayerInfoAccessor;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.config.TablistSorting;
import fr.alexdoru.mwe.data.NameFormatter;
import fr.alexdoru.mwe.data.PlayerDataManager;
import fr.alexdoru.mwe.features.SquadHandler;
import fr.alexdoru.mwe.utils.ColorUtil;
import fr.alexdoru.mwe.utils.NetInfoOrdering;
import fr.alexdoru.mwe.utils.RenderHelper;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SquadHealthHUD extends AbstractRenderer {

    private static final int PADDING = 4;
    private final List<NetworkPlayerInfo> netInfoList = new ArrayList<>();
    private final List<EntityPlayer> playerList = new ArrayList<>();
    private final List<String> playerNamesList = new ArrayList<>();
    private final List<ISquadInfoRenderer> extraInfoRenderers = new ArrayList<>();
    private int[] maxExtraWidths = new int[0];

    public SquadHealthHUD() {
        super(MWEConfig.squadHUDPosition);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void registerExtraRenderer(@NotNull ISquadInfoRenderer renderer) {
        Objects.requireNonNull(renderer);
        extraInfoRenderers.add(renderer);
        maxExtraWidths = new int[extraInfoRenderers.size()];
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            netInfoList.clear();
            playerList.clear();
            playerNamesList.clear();
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null && mc.thePlayer != null && this.isEnabled(0)) {
                final Scoreboard scoreboard = mc.theWorld.getScoreboard();
                final ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(0);
                if (!mc.isIntegratedServerRunning() || scoreobjective != null) {
                    this.populateRenderList(mc, scoreboard, scoreobjective);
                }
            }
        }
    }

    @Override
    public void render(ScaledResolution resolution) {
        final Minecraft mc = Minecraft.getMinecraft();
        final Scoreboard scoreboard = mc.theWorld.getScoreboard();
        final ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(0);
        if (!netInfoList.isEmpty() && (!mc.isIntegratedServerRunning() || scoreobjective != null)) {
            this.render(resolution, mc, scoreboard, scoreobjective);
        }
    }

    private void render(ScaledResolution resolution, Minecraft mc, Scoreboard scoreboard, ScoreObjective scoreobjective) {
        final int listSize = netInfoList.size();
        final boolean showScores = scoreobjective != null && scoreobjective.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS;
        final boolean showFinals = this.parser.isInMwGame();
        int maxNameWidth = 0;
        int maxScoreWidth = 0;
        int maxFinalWidth = 0;
        Arrays.fill(maxExtraWidths, 0);
        for (int i = 0; i < listSize; i++) {
            final NetworkPlayerInfo netInfo = netInfoList.get(i);
            final EntityPlayer entityPlayer = playerList.get(i);
            final String playerName = playerNamesList.get(i);
            maxNameWidth = Math.max(maxNameWidth, mc.fontRendererObj.getStringWidth(playerName));
            if (showScores) {
                maxScoreWidth = Math.max(maxScoreWidth, mc.fontRendererObj.getStringWidth(String.valueOf(scoreboard.getValueFromObjective(netInfo.getGameProfile().getName(), scoreobjective).getScorePoints())));
            }
            if (showFinals) {
                final int playerFinalkills = ((NetworkPlayerInfoAccessor) netInfo).getFinalKills();
                if (playerFinalkills != 0) {
                    maxFinalWidth = Math.max(maxFinalWidth, mc.fontRendererObj.getStringWidth(String.valueOf(playerFinalkills)));
                }
            }
            for (int r = 0; r < this.extraInfoRenderers.size(); r++) {
                final int width = this.extraInfoRenderers.get(r).getWidth(netInfo, entityPlayer);
                maxExtraWidths[r] = Math.max(maxExtraWidths[r], width);
            }
        }
        if (maxNameWidth > 0) maxNameWidth += PADDING;
        if (maxFinalWidth > 0) maxFinalWidth += PADDING;
        if (maxScoreWidth > 0) maxScoreWidth += PADDING;
        int totalExtraWidth = 0;
        for (final int w : maxExtraWidths) {
            if (w > 0) totalExtraWidth += w + PADDING;
        }
        final int maxLineWidth = 9 + maxNameWidth + maxFinalWidth + maxScoreWidth + totalExtraWidth - PADDING;
        final int hudWidth = maxLineWidth + 2;
        final int hudHeight = listSize * 9 + 1;
        this.rendererPosition.updateAdjustedAbsolutePosition(resolution, hudWidth, hudHeight);
        final int hudXpos = this.rendererPosition.getAbsoluteRenderX();
        final int hudYpos = this.rendererPosition.getAbsoluteRenderY();
        Gui.drawRect(hudXpos, hudYpos, hudXpos + hudWidth, hudYpos + hudHeight, Integer.MIN_VALUE);
        for (int i = 0; i < listSize; i++) {
            final NetworkPlayerInfo netInfo = netInfoList.get(i);
            final EntityPlayer entityPlayer = playerList.get(i);
            final String playername = playerNamesList.get(i);
            int xDrawingPos = hudXpos + 1;
            final int yDrawingPos = hudYpos + 1 + i * 9;
            // draw background
            final int backGroundColor = 0xFFFFFF | (MWEConfig.squadHUDBackgroundAlpha << 24);
            Gui.drawRect(xDrawingPos, yDrawingPos, hudXpos + maxLineWidth + 1, yDrawingPos + 8, backGroundColor);
            // draw head
            final boolean renderHatLayer = entityPlayer == null || entityPlayer.isWearing(EnumPlayerModelParts.HAT);
            RenderHelper.renderSkinHead(netInfo.getLocationSkin(), xDrawingPos, yDrawingPos, renderHatLayer, 8);
            xDrawingPos += 9;
            // draw name
            mc.fontRendererObj.drawStringWithShadow(playername, (float) xDrawingPos, (float) yDrawingPos, 0xFFFFFF);
            xDrawingPos += maxNameWidth;
            // draw finals
            if (showFinals && maxFinalWidth > 0) {
                final int playersFinals = ((NetworkPlayerInfoAccessor) netInfo).getFinalKills();
                if (playersFinals != 0) {
                    final String finalsString = EnumChatFormatting.GOLD.toString() + playersFinals;
                    mc.fontRendererObj.drawStringWithShadow(finalsString, xDrawingPos, yDrawingPos, 0xFFFFFF);
                }
            }
            xDrawingPos += maxFinalWidth;
            // draw score
            if (showScores && maxScoreWidth > 0 && netInfo.getGameType() != WorldSettings.GameType.SPECTATOR) {
                final int scorePoints = scoreobjective.getScoreboard().getValueFromObjective(netInfo.getGameProfile().getName(), scoreobjective).getScorePoints();
                final String scoreString = ColorUtil.getColoredHP(EnumChatFormatting.YELLOW, scorePoints).toString() + scorePoints;
                mc.fontRendererObj.drawStringWithShadow(scoreString, xDrawingPos, yDrawingPos, 0xFFFFFF);
            }
            xDrawingPos += maxScoreWidth;
            // draw extras
            if (totalExtraWidth > 0) {
                for (int r = 0; r < this.extraInfoRenderers.size(); r++) {
                    final int reservedWidth = maxExtraWidths[r];
                    if (reservedWidth > 0) {
                        this.extraInfoRenderers.get(r).render(netInfo, entityPlayer, xDrawingPos, yDrawingPos, reservedWidth);
                        xDrawingPos += reservedWidth + PADDING;
                    }
                }
            }
        }
    }

    @Override
    public void renderDummy() {
        final Minecraft mc = Minecraft.getMinecraft();
        final int hudXpos = this.rendererPosition.getAbsoluteRenderX();
        final int hudYpos = this.rendererPosition.getAbsoluteRenderY();
        final int listSize = 4;
        final int maxNameWidth = mc.fontRendererObj.getStringWidth(mc.thePlayer.getName());
        final int maxScoreWidth = mc.fontRendererObj.getStringWidth(" 00");
        final int maxFinalWidth = mc.fontRendererObj.getStringWidth(" 0");
        final int maxLineWidth = maxNameWidth + maxFinalWidth + maxScoreWidth + 9;
        Gui.drawRect(hudXpos, hudYpos, hudXpos + maxLineWidth + 2, hudYpos + listSize * 9 + 1, Integer.MIN_VALUE);
        for (int i = 0; i < listSize; i++) {
            int xDrawingPos = hudXpos + 1;
            final int yDrawingPos = hudYpos + 1 + i * 9;
            final int backGroundColor = 0xFFFFFF | (MWEConfig.squadHUDBackgroundAlpha << 24);
            Gui.drawRect(xDrawingPos, yDrawingPos, hudXpos + maxLineWidth + 1, yDrawingPos + 8, backGroundColor);
            RenderHelper.renderSkinHead(DefaultPlayerSkin.getDefaultSkinLegacy(), xDrawingPos, yDrawingPos, true, 8);
            xDrawingPos += 9;
            final String formattedName = EnumChatFormatting.GREEN + mc.thePlayer.getName();
            mc.fontRendererObj.drawStringWithShadow(formattedName, (float) xDrawingPos, (float) yDrawingPos, -1);
            final int xStartFinalDrawingPos = xDrawingPos + maxNameWidth + 1;
            final int xStartScoreDrawingPos = xStartFinalDrawingPos + maxFinalWidth;
            if (maxScoreWidth + maxFinalWidth > 5) {
                final int scorePoints = 12 + (i * 18 + 22) % 8;
                final String scoreString = ColorUtil.getColoredHP(EnumChatFormatting.YELLOW, scorePoints) + " " + scorePoints;
                mc.fontRendererObj.drawStringWithShadow(scoreString, xStartScoreDrawingPos, yDrawingPos, 0xFFFFFF);
                final String finalsString = EnumChatFormatting.GOLD + " " + (3 + (i * 28 + 15) % 5);
                mc.fontRendererObj.drawStringWithShadow(finalsString, xStartFinalDrawingPos, yDrawingPos, 0xFFFFFF);
            }
        }
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.rendererPosition.isEnabled() && (!SquadHandler.getSquad().isEmpty() || MWEConfig.squadHUDAutoShowTeamates);
    }

    private void populateRenderList(Minecraft mc, Scoreboard scoreboard, ScoreObjective scoreobjective) {

        final String ownName = mc.thePlayer.getGameProfile().getName();
        final char ownTeam = this.getPlayersTeam(scoreboard, ownName);
        final boolean addTeamates = ownTeam != 0 && MWEConfig.squadHUDAutoShowTeamates && MWEConfig.squadHUDAutoShowTeamatesCount > 0;
        final int distSqLimit = MWEConfig.squadHUDAutoShowTeamatesDistanceLimit * MWEConfig.squadHUDAutoShowTeamatesDistanceLimit;

        final List<NetworkPlayerInfo> candidates = new ArrayList<>();
        for (final NetworkPlayerInfo netInfo : mc.getNetHandler().getPlayerInfoMap()) {
            final String name = netInfo.getGameProfile().getName();
            if (ownName.equals(name)) continue;
            if (SquadHandler.isSquadmate(name)) {
                netInfoList.add(netInfo);
                continue;
            }
            if (addTeamates && ownTeam == getPlayersTeam(scoreboard, name)) {
                if (MWEConfig.squadHUDAutoShowTeamatesLimitDistance) {
                    final EntityPlayer entity = PlayerDataManager.getPlayerEntityByUUID(netInfo.getGameProfile().getId());
                    if (entity == null || mc.thePlayer.getDistanceSqToEntity(entity) > distSqLimit) {
                        continue;
                    }
                }
                candidates.add(netInfo);
            }
        }

        if (!candidates.isEmpty()) {
            if (candidates.size() > MWEConfig.squadHUDAutoShowTeamatesCount) {
                this.sortRenderList(candidates, MWEConfig.squadHUDAutoShowTeamatesCritera, mc, scoreboard, scoreobjective);
            }
            final int limit = Math.min(candidates.size(), MWEConfig.squadHUDAutoShowTeamatesCount);
            for (int i = 0; i < limit; i++) {
                netInfoList.add(candidates.get(i));
            }
        }

        if (!netInfoList.isEmpty()) {
            if (MWEConfig.squadHUDShowSelf && !MWEConfig.squadHUDShowSelfFirst) {
                netInfoList.add(mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()));
            }
            this.sortRenderList(netInfoList, MWEConfig.squadHUDDisplaySorting, mc, scoreboard, scoreobjective);
            if (MWEConfig.squadHUDShowSelf && MWEConfig.squadHUDShowSelfFirst) {
                netInfoList.add(0, mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()));
            }
            for (final NetworkPlayerInfo netInfo : netInfoList) {
                playerList.add(PlayerDataManager.getPlayerEntityByUUID(netInfo.getGameProfile().getId()));
                playerNamesList.add(this.getPlayerName(netInfo));
            }
        }

    }

    private char getPlayersTeam(Scoreboard scoreboard, String name) {
        final ScorePlayerTeam team = scoreboard.getPlayersTeam(name);
        if (team == null) {
            return 0;
        }
        return StringUtil.getLastColorCharOf(team.getColorPrefix());
    }

    private void sortRenderList(List<NetworkPlayerInfo> list, TablistSorting sorting, Minecraft mc, Scoreboard scoreboard, ScoreObjective scoreobjective) {
        switch (sorting) {
            case LOWEST_SCORE:
                if (scoreobjective != null && scoreobjective.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                    list.sort(Comparator.comparingInt((NetworkPlayerInfo info) -> scoreboard.getValueFromObjective(info.getGameProfile().getName(), scoreobjective).getScorePoints()));
                } else {
                    NetInfoOrdering.vanillaSorting(list);
                }
                break;
            case HIGHEST_SCORE:
                if (scoreobjective != null && scoreobjective.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                    list.sort(Comparator.comparingInt((NetworkPlayerInfo info) -> scoreboard.getValueFromObjective(info.getGameProfile().getName(), scoreobjective).getScorePoints()).reversed());
                } else {
                    NetInfoOrdering.vanillaSorting(list);
                }
                break;
            case LOWEST_DISTANCE:
                list.sort(Comparator.comparingDouble((NetworkPlayerInfo info) -> this.getDistanceSqToLocalPlayer(mc, info)));
                break;
            case HIGHEST_DISTANCE:
                list.sort(Comparator.comparingDouble((NetworkPlayerInfo info) -> this.getDistanceSqToLocalPlayer(mc, info)).reversed());
                break;
            case VANILLA:
            default:
                NetInfoOrdering.vanillaSorting(list);
                break;
        }
    }

    private double getDistanceSqToLocalPlayer(Minecraft mc, NetworkPlayerInfo info) {
        final EntityPlayer entity = PlayerDataManager.getPlayerEntityByUUID(info.getGameProfile().getId());
        if (entity == null) {
            return Double.MAX_VALUE;
        }
        return mc.thePlayer.getDistanceSqToEntity(entity);
    }

    private String getPlayerName(NetworkPlayerInfo netInfo) {
        return NameFormatter.getFormattedName(netInfo, MWEConfig.squadHUDshowPrefix, MWEConfig.squadHUDshowSuffix, MWEConfig.squadHUDshowAlias);
    }

}