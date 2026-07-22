package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.api.enums.MWSkin;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.MapUtil;
import fr.alexdoru.mwe.utils.RenderHelper;
import fr.alexdoru.mwe.utils.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;

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
            int i = 0;
            int x = this.rendererPosition.getAbsoluteRenderX();
            int y = this.rendererPosition.getAbsoluteRenderY();
            int maxWidth = 0;
            final Minecraft mc = Minecraft.getMinecraft();
            for (final Map.Entry<MWSkin, Integer> entry : list) {
                maxWidth = Math.max(maxWidth, mc.fontRendererObj.getStringWidth(entry.getValue().toString()));
                RenderHelper.renderSkinHead(entry.getKey().getSkin(), x, y, true, 8);
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
