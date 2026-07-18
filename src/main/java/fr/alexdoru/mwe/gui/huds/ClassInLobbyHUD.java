package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.api.enums.MWSkin;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.MapUtil;
import fr.alexdoru.mwe.utils.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassInLobbyHUD extends AbstractRenderer {

    private static final List<Map.Entry<MWSkin, Integer>> dummyToRenderList;

    private final TimerUtil timer = new TimerUtil(1000L);
    private List<Map.Entry<MWSkin, Integer>> toRenderList = new ArrayList<>();

    static {
        final Map<MWSkin, Integer> map = new HashMap<>();
        map.put(MWSkin.DREADLORD$DREADLORD, 10);
        map.put(MWSkin.ENDERMAN$ENDERMAN, 5);
        map.put(MWSkin.GOLEM$GOLEM, 6);
        map.put(MWSkin.HEROBRINE$HEROBRINE, 2);
        map.put(MWSkin.PIGMAN$PIGMAN, 5);
        map.put(MWSkin.WEREWOLF$WEREWOLF, 4);
        map.put(MWSkin.ZOMBIE$ZOMBIE, 2);
        dummyToRenderList = MapUtil.sortByKey(map);
    }

    public ClassInLobbyHUD() {
        super(MWEConfig.classInLobbyHUDPosition);
    }

    @Override
    public void render(ScaledResolution resolution) {
        if (this.timer.update()) {
            this.updateClassInLobby();
        }
        this.rendererPosition.updateAbsolutePosition(resolution);
        renderClassHUD(this.toRenderList);
    }

    @Override
    public void renderDummy() {
        this.renderClassHUD(dummyToRenderList);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return ScoreboardTracker.isPreGameLobby() && this.rendererPosition.isEnabled();
    }

    private void renderClassHUD(List<Map.Entry<MWSkin, Integer>> list) {
        GlStateManager.pushMatrix();
        {
            int i = 0;
            int x = this.rendererPosition.getAbsoluteRenderX();
            int y = this.rendererPosition.getAbsoluteRenderY();
            int maxWidth = 0;
            final Minecraft mc = Minecraft.getMinecraft();
            for (final Map.Entry<MWSkin, Integer> entry : list) {
                maxWidth = Math.max(maxWidth, mc.fontRendererObj.getStringWidth(entry.getValue().toString()));
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                mc.getTextureManager().bindTexture(entry.getKey().getSkin());
                Gui.drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, 8, 8, 64.0F, 64.0F);
                Gui.drawScaledCustomSizeModalRect(x, y, 40, 8, 8, 8, 8, 8, 64.0F, 64.0F);
                mc.fontRendererObj.drawStringWithShadow(entry.getValue().toString(), x + 9, y, 0xFFFFFF);
                y += mc.fontRendererObj.FONT_HEIGHT;
                i++;
                if (i == 5) {
                    i = 0;
                    x = x + maxWidth + 10;
                    y = this.rendererPosition.getAbsoluteRenderY();
                    maxWidth = 0;
                }
            }
        }
        GlStateManager.popMatrix();
    }

    private void updateClassInLobby() {
        final Map<MWSkin, Integer> map = new HashMap<>();
        for (final NetworkPlayerInfo netInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            final MWSkin skin = MWSkin.ofPlayer(netInfo);
            if (skin != null) {
                final MWSkin defaultSkin;
                if (skin == MWSkin.RANDOM) {
                    defaultSkin = MWSkin.RANDOM;
                } else {
                    defaultSkin = MWSkin.fromName(skin.mwClass.className);
                }
                if (defaultSkin == null) {
                    continue;
                }
                map.merge(defaultSkin, 1, Integer::sum);
            }
        }
        this.toRenderList = MapUtil.sortByKey(map);
    }

}
