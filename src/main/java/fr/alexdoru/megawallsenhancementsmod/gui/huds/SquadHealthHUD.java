package fr.alexdoru.megawallsenhancementsmod.gui.huds;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import fr.alexdoru.megawallsenhancementsmod.asm.accessor.NetworkPlayerInfoAccessor;
import fr.alexdoru.megawallsenhancementsmod.asm.hooks.GuiPlayerTabOverlayHook;
import fr.alexdoru.megawallsenhancementsmod.asm.hooks.NetHandlerPlayClientHook;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.features.SquadHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiPosition;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.IRenderer;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class SquadHealthHUD implements IRenderer {

    public static SquadHealthHUD instance;
    private final GuiPosition guiPosition;
    private final Minecraft mc = Minecraft.getMinecraft();
    private static final Ordering<NetworkPlayerInfo> ordering = Ordering.from(new SquadHealthHUD.PlayerComparator());

    public SquadHealthHUD() {
        instance = this;
        this.guiPosition = ConfigHandler.squadHUDPosition;
    }

    @Override
    public void render(ScaledResolution resolution) {
        final Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        final ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(0);
        if (mc.isIntegratedServerRunning() && scoreobjective == null) {
            return;
        }
        final List<NetworkPlayerInfo> list = new ArrayList<>();
        for (final String squadmateName : SquadHandler.getSquad().keySet()) {
            final NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.playerInfoMap.get(squadmateName);
            if (networkPlayerInfo != null) {
                list.add(networkPlayerInfo);
            }
        }
        if (list.size() <= 1) {
            return;
        }
        mc.mcProfiler.startSection("SquadHealthHUD");
        final List<NetworkPlayerInfo> playerlistToRender = new ArrayList<>(ordering.sortedCopy(list));
        int maxNameWidth = 0;
        int maxScoreWidth = 0;
        int maxFinalWidth = 0;
        for (final NetworkPlayerInfo networkplayerinfo : playerlistToRender) {
            maxNameWidth = Math.max(maxNameWidth, this.mc.fontRendererObj.getStringWidth(this.getPlayerName(networkplayerinfo)));
            if (scoreobjective != null && scoreobjective.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                maxScoreWidth = Math.max(maxScoreWidth, this.mc.fontRendererObj.getStringWidth(" " + scoreboard.getValueFromObjective(networkplayerinfo.getGameProfile().getName(), scoreobjective).getScorePoints()));
            }
            if (FKCounterMod.isInMwGame) {
                final int playerFinalkills = ((NetworkPlayerInfoAccessor) networkplayerinfo).getPlayerFinalkills();
                if (playerFinalkills != 0) {
                    maxFinalWidth = Math.max(maxFinalWidth, this.mc.fontRendererObj.getStringWidth(" " + playerFinalkills));
                }
            }
        }
        GlStateManager.pushMatrix();
        {
            final boolean flag = this.mc.isIntegratedServerRunning() || this.mc.getNetHandler().getNetworkManager().getIsencrypted();
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
                Gui.drawRect(xDrawingPos, yDrawingPos, hudXpos + maxLineWidth + 1, yDrawingPos + 8, 553648127);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                final NetworkPlayerInfo networkplayerinfo = playerlistToRender.get(i);
                final String formattedName = this.getPlayerName(networkplayerinfo);
                final GameProfile gameprofile = networkplayerinfo.getGameProfile();
                if (flag) {
                    final EntityPlayer entityplayer = this.mc.theWorld.getPlayerEntityByUUID(gameprofile.getId());
                    this.mc.getTextureManager().bindTexture(networkplayerinfo.getLocationSkin());
                    Gui.drawScaledCustomSizeModalRect(xDrawingPos, yDrawingPos, 8.0F, (float) 8, 8, 8, 8, 8, 64.0F, 64.0F);
                    if (entityplayer == null || entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                        Gui.drawScaledCustomSizeModalRect(xDrawingPos, yDrawingPos, 40.0F, 8, 8, 8, 8, 8, 64.0F, 64.0F);
                    }
                    xDrawingPos += 9;
                }
                this.mc.fontRendererObj.drawStringWithShadow(formattedName, (float) xDrawingPos, (float) yDrawingPos, -1);
                final int xStartFinalDrawingPos = xDrawingPos + maxNameWidth + 1;
                final int xStartScoreDrawingPos = xStartFinalDrawingPos + maxFinalWidth;
                if (maxScoreWidth + maxFinalWidth > 5) {
                    if (scoreobjective != null && networkplayerinfo.getGameType() != WorldSettings.GameType.SPECTATOR && scoreobjective.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                        final int scorePoints = scoreobjective.getScoreboard().getValueFromObjective(gameprofile.getName(), scoreobjective).getScorePoints();
                        final String scoreString = GuiPlayerTabOverlayHook.getColoredHP(scorePoints) + " " + scorePoints;
                        this.mc.fontRendererObj.drawStringWithShadow(scoreString, xStartScoreDrawingPos, yDrawingPos, 0xFFFFFF);
                    }
                    if (FKCounterMod.isInMwGame) {
                        final int playersFinals = ((NetworkPlayerInfoAccessor) networkplayerinfo).getPlayerFinalkills();
                        if (playersFinals != 0) {
                            final String finalsString = EnumChatFormatting.GOLD + " " + playersFinals;
                            mc.fontRendererObj.drawStringWithShadow(finalsString, xStartFinalDrawingPos, yDrawingPos, 0xFFFFFF);
                        }
                    }
                }
            }
        }
        GlStateManager.popMatrix();
        mc.mcProfiler.endSection();
    }

    private static final Pattern squadSuffixPattern = Pattern.compile("^" + EnumChatFormatting.GOLD + "\\[" + EnumChatFormatting.DARK_GREEN + "S" + EnumChatFormatting.GOLD + "\\] ");
    private String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        final String formattedName = NameUtil.getFormattedName(networkPlayerInfoIn);
        return ConfigHandler.iconsOnNames ? squadSuffixPattern.matcher(formattedName).replaceFirst("") : formattedName;
    }

    @Override
    public int getHeight() {
        return mc.fontRendererObj.FONT_HEIGHT * 4 + 2;
    }

    @Override
    public int getWidth() {
        return mc.fontRendererObj.getStringWidth(mc.thePlayer.getName());
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
            final int maxLineWidth = maxNameWidth + maxFinalWidth + maxScoreWidth;
            Gui.drawRect(hudXpos, hudYpos, hudXpos + maxLineWidth + 2, hudYpos + listSize * 9 + 1, Integer.MIN_VALUE);
            for (int i = 0; i < listSize; i++) {
                final int xDrawingPos = hudXpos + 1;
                final int yDrawingPos = hudYpos + 1 + i * 9;
                Gui.drawRect(xDrawingPos, yDrawingPos, hudXpos + maxLineWidth + 1, yDrawingPos + 8, 553648127);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                final String formattedName = EnumChatFormatting.GREEN + mc.thePlayer.getName();
                this.mc.fontRendererObj.drawStringWithShadow(formattedName, (float) xDrawingPos, (float) yDrawingPos, -1);
                final int xStartFinalDrawingPos = xDrawingPos + maxNameWidth + 1;
                final int xStartScoreDrawingPos = xStartFinalDrawingPos + maxFinalWidth;
                if (maxScoreWidth + maxFinalWidth > 5) {
                    final int scorePoints = 12 + (i * 18 + 22) % 8;
                    final String scoreString = GuiPlayerTabOverlayHook.getColoredHP(scorePoints) + " " + scorePoints;
                    this.mc.fontRendererObj.drawStringWithShadow(scoreString, xStartScoreDrawingPos, yDrawingPos, 0xFFFFFF);
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

    @Override
    public GuiPosition getGuiPosition() {
        return this.guiPosition;
    }

    @SideOnly(Side.CLIENT)
    static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
        private PlayerComparator() {}
        @Override
        public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_) {
            final ScorePlayerTeam scoreplayerteam = p_compare_1_.getPlayerTeam();
            final ScorePlayerTeam scoreplayerteam1 = p_compare_2_.getPlayerTeam();
            return ComparisonChain.start()
                    .compareTrueFirst(p_compare_1_.getGameType() != WorldSettings.GameType.SPECTATOR, p_compare_2_.getGameType() != WorldSettings.GameType.SPECTATOR)
                    .compare(scoreplayerteam != null ? scoreplayerteam.getRegisteredName() : "", scoreplayerteam1 != null ? scoreplayerteam1.getRegisteredName() : "")
                    .compare(p_compare_1_.getGameProfile().getName(), p_compare_2_.getGameProfile().getName())
                    .result();
        }
    }

}
