package fr.alexdoru.mwe.gui.huds;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.mwe.asm.accessors.NetworkPlayerInfoAccessor;
import fr.alexdoru.mwe.asm.hooks.NetHandlerPlayClientHook_PlayerMapTracker;
import fr.alexdoru.mwe.config.ConfigHandler;
import fr.alexdoru.mwe.features.SquadHandler;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.ColorUtil;
import fr.alexdoru.mwe.utils.NameUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class SquadHealthHUD extends AbstractRenderer {

    public SquadHealthHUD() {
        super(ConfigHandler.squadHUDPosition);
    }

    @Override
    public void render(ScaledResolution resolution) {
        final Scoreboard scoreboard = mc.theWorld.getScoreboard();
        final ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(0);
        if (mc.isIntegratedServerRunning() && scoreobjective == null) {
            return;
        }
        final List<NetworkPlayerInfo> list = new ArrayList<>();
        for (final String squadmateName : SquadHandler.getSquad().keySet()) {
            final NetworkPlayerInfo netInfo = NetHandlerPlayClientHook_PlayerMapTracker.getPlayerInfo(squadmateName);
            if (netInfo != null) {
                list.add(netInfo);
            }
        }
        if (list.size() <= 1) {
            return;
        }
        final List<NetworkPlayerInfo> playerlistToRender = NameUtil.sortedCopyOf(list);
        int maxNameWidth = 0;
        int maxScoreWidth = 0;
        int maxFinalWidth = 0;
        for (final NetworkPlayerInfo networkplayerinfo : playerlistToRender) {
            maxNameWidth = Math.max(maxNameWidth, mc.fontRendererObj.getStringWidth(this.getPlayerName(networkplayerinfo)));
            if (scoreobjective != null && scoreobjective.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                maxScoreWidth = Math.max(maxScoreWidth, mc.fontRendererObj.getStringWidth(" " + scoreboard.getValueFromObjective(networkplayerinfo.getGameProfile().getName(), scoreobjective).getScorePoints()));
            }
            if (ScoreboardTracker.isInMwGame()) {
                final int playerFinalkills = ((NetworkPlayerInfoAccessor) networkplayerinfo).getFinalKills();
                if (playerFinalkills != 0) {
                    maxFinalWidth = Math.max(maxFinalWidth, mc.fontRendererObj.getStringWidth(" " + playerFinalkills));
                }
            }
        }
        GlStateManager.pushMatrix();
        {
            final boolean flag = mc.isIntegratedServerRunning() || mc.getNetHandler().getNetworkManager().getIsencrypted();
            final int maxLineWidth = (flag ? 9 : 0) + maxNameWidth + maxFinalWidth + maxScoreWidth;
            final int listSize = playerlistToRender.size();
            final int hudWidth = maxLineWidth + 2;
            final int hudHight = listSize * 9 + 1;
            this.guiPosition.updateAdjustedAbsolutePosition(resolution, hudWidth, hudHight);
            final int hudXpos = this.guiPosition.getAbsoluteRenderX();
            final int hudYpos = this.guiPosition.getAbsoluteRenderY();
            Gui.drawRect(hudXpos, hudYpos, hudXpos + hudWidth, hudYpos + hudHight, Integer.MIN_VALUE);
            for (int i = 0; i < listSize; i++) {
                int xDrawingPos = hudXpos + 1;
                final int yDrawingPos = hudYpos + 1 + i * 9;
                Gui.drawRect(xDrawingPos, yDrawingPos, hudXpos + maxLineWidth + 1, yDrawingPos + 8, 0x20FFFFFF);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                final NetworkPlayerInfo netInfo = playerlistToRender.get(i);
                final GameProfile gameprofile = netInfo.getGameProfile();
                if (flag) {
                    final EntityPlayer entityplayer = mc.theWorld.getPlayerEntityByUUID(gameprofile.getId());
                    mc.getTextureManager().bindTexture(netInfo.getLocationSkin());
                    Gui.drawScaledCustomSizeModalRect(xDrawingPos, yDrawingPos, 8, 8, 8, 8, 8, 8, 64.0F, 64.0F);
                    if (entityplayer == null || entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                        Gui.drawScaledCustomSizeModalRect(xDrawingPos, yDrawingPos, 40, 8, 8, 8, 8, 8, 64.0F, 64.0F);
                    }
                    xDrawingPos += 9;
                }
                mc.fontRendererObj.drawStringWithShadow(this.getPlayerName(netInfo), (float) xDrawingPos, (float) yDrawingPos, 0xFFFFFF);
                final int xStartFinalDrawingPos = xDrawingPos + maxNameWidth + 1;
                final int xStartScoreDrawingPos = xStartFinalDrawingPos + maxFinalWidth;
                if (maxScoreWidth + maxFinalWidth > 5) {
                    if (scoreobjective != null && netInfo.getGameType() != WorldSettings.GameType.SPECTATOR && scoreobjective.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                        final int scorePoints = scoreobjective.getScoreboard().getValueFromObjective(gameprofile.getName(), scoreobjective).getScorePoints();
                        final String scoreString = ColorUtil.getColoredHP(EnumChatFormatting.YELLOW, scorePoints) + " " + scorePoints;
                        mc.fontRendererObj.drawStringWithShadow(scoreString, xStartScoreDrawingPos, yDrawingPos, 0xFFFFFF);
                    }
                    if (ScoreboardTracker.isInMwGame()) {
                        final int playersFinals = ((NetworkPlayerInfoAccessor) netInfo).getFinalKills();
                        if (playersFinals != 0) {
                            final String finalsString = EnumChatFormatting.GOLD + " " + playersFinals;
                            mc.fontRendererObj.drawStringWithShadow(finalsString, xStartFinalDrawingPos, yDrawingPos, 0xFFFFFF);
                        }
                    }
                }
            }
        }
        GlStateManager.popMatrix();
    }

    private String getPlayerName(NetworkPlayerInfo netInfo) {
        final String name = NameUtil.getFormattedName(netInfo);
        return name.startsWith(NameUtil.SQUAD_ICON) ? name.substring(NameUtil.SQUAD_ICON.length()) : name;
    }

    @Override
    public void renderDummy() {
        GlStateManager.pushMatrix();
        {
            final int hudXpos = this.guiPosition.getAbsoluteRenderX();
            final int hudYpos = this.guiPosition.getAbsoluteRenderY();
            final int listSize = 4;
            final int maxNameWidth = mc.fontRendererObj.getStringWidth(mc.thePlayer.getName());
            final int maxScoreWidth = mc.fontRendererObj.getStringWidth(" 00");
            final int maxFinalWidth = mc.fontRendererObj.getStringWidth(" 0");
            final int maxLineWidth = maxNameWidth + maxFinalWidth + maxScoreWidth + 9;
            Gui.drawRect(hudXpos, hudYpos, hudXpos + maxLineWidth + 2, hudYpos + listSize * 9 + 1, Integer.MIN_VALUE);
            for (int i = 0; i < listSize; i++) {
                int xDrawingPos = hudXpos + 1;
                final int yDrawingPos = hudYpos + 1 + i * 9;
                Gui.drawRect(xDrawingPos, yDrawingPos, hudXpos + maxLineWidth + 1, yDrawingPos + 8, 0x20FFFFFF);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                mc.getTextureManager().bindTexture(DefaultPlayerSkin.getDefaultSkinLegacy());
                Gui.drawScaledCustomSizeModalRect(xDrawingPos, yDrawingPos, 8, 8, 8, 8, 8, 8, 64.0F, 64.0F);
                Gui.drawScaledCustomSizeModalRect(xDrawingPos, yDrawingPos, 40, 8, 8, 8, 8, 8, 64.0F, 64.0F);
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
        GlStateManager.popMatrix();
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return ConfigHandler.showSquadHUD && SquadHandler.getSquad().size() > 1;
    }

}
