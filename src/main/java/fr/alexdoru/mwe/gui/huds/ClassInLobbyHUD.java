package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.api.enums.MWSkin;
import fr.alexdoru.mwe.api.enums.MWTeam;
import fr.alexdoru.mwe.api.utils.MapUtil;
import fr.alexdoru.mwe.api.utils.RenderUtil;
import fr.alexdoru.mwe.asm.interfaces.GuiPlayerTabOverlayAccessor;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.utils.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.*;

public class ClassInLobbyHUD extends AbstractRenderer {

    private final TimerUtil timer = new TimerUtil(1000L);

    private final List<Map.Entry<MWSkin, Integer>> dummyAllClassList;
    private List<Map.Entry<MWSkin, Integer>> allClassList = new ArrayList<>();

    private final MWTeam[] teams;
    private final List<Map.Entry<MWSkin, Integer>>[] perTeamRenderList;
    private final Map<MWSkin, Integer>[] perTeamData;

    @SuppressWarnings("unchecked")
    public ClassInLobbyHUD() {
        super(MWEConfig.classInLobbyHUDPosition);
        final Map<MWSkin, Integer> map = new HashMap<>();
        map.put(MWSkin.DREADLORD$DREADLORD, 10);
        map.put(MWSkin.ENDERMAN$ENDERMAN, 5);
        map.put(MWSkin.GOLEM$GOLEM, 6);
        map.put(MWSkin.HEROBRINE$HEROBRINE, 2);
        map.put(MWSkin.PIGMAN$PIGMAN, 5);
        map.put(MWSkin.WEREWOLF$WEREWOLF, 4);
        map.put(MWSkin.ZOMBIE$ZOMBIE, 2);
        this.dummyAllClassList = this.getSortedEntries(map);
        this.teams = MWTeam.values();
        this.perTeamRenderList = new List[this.teams.length];
        this.perTeamData = new Map[this.teams.length];
        for (int i = 0; i < this.teams.length; i++) {
            this.perTeamRenderList[i] = new ArrayList<>();
            this.perTeamData[i] = new HashMap<>();
        }
    }

    @Override
    public void render(ScaledResolution resolution) {
        final boolean showPerTeam = MWEConfig.classInLobbyHUDPerTeamDisplay && (this.parser.isInMwGame() || this.parser.isMWReplay());
        if (this.timer.update()) {
            if (showPerTeam) {
                this.updatePerTeamData();
            } else {
                this.updateClassData();
            }
        }
        this.rendererPosition.updateAbsolutePosition(resolution);
        if (showPerTeam) {
            this.renderTeamHUD();
        } else {
            this.renderClassHUD(this.allClassList);
        }
    }

    @Override
    public void renderDummy() {
        this.renderClassHUD(this.dummyAllClassList);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return this.rendererPosition.isEnabled()
                && (this.parser.isPreGameLobby() || MWEConfig.classInLobbyHUDDuringGames && (this.parser.isInMwGame() || this.parser.isMWReplay()))
                && (!MWEConfig.classInLobbyHUDOnlyInTablist || isTablistDisplayed());
    }

    private boolean isTablistDisplayed() {
        return ((GuiPlayerTabOverlayAccessor) Minecraft.getMinecraft().ingameGUI.getTabList()).mwe$isBeingRendered();
    }

    private void updatePerTeamData() {
        for (final Map<MWSkin, Integer> map : this.perTeamData) {
            map.clear();
        }
        for (final NetworkPlayerInfo netInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            final MWTeam team = MWTeam.ofPlayer(netInfo);
            final MWSkin defaultSkin = this.getDefaultSkin(netInfo);
            if (team != null && defaultSkin != null) {
                this.perTeamData[team.ordinal()].merge(defaultSkin, 1, Integer::sum);
            }
        }
        for (int i = 0; i < this.perTeamData.length; i++) {
            this.perTeamRenderList[i] = this.getSortedEntries(this.perTeamData[i]);
        }
    }

    private void updateClassData() {
        final Map<MWSkin, Integer> map = new HashMap<>();
        for (final NetworkPlayerInfo netInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            final MWSkin defaultSkin = this.getDefaultSkin(netInfo);
            if (defaultSkin != null) {
                map.merge(defaultSkin, 1, Integer::sum);
            }
        }
        this.allClassList = this.getSortedEntries(map);
    }

    private List<Map.Entry<MWSkin, Integer>> getSortedEntries(Map<MWSkin, Integer> map) {
        if (map.isEmpty()) {
            return Collections.emptyList();
        }
        return MapUtil.sortByKey(map);
    }

    private void renderTeamHUD() {
        final Minecraft mc = Minecraft.getMinecraft();
        int y = this.rendererPosition.getAbsoluteRenderY();
        for (int i = 0; i < this.perTeamRenderList.length; i++) {
            final List<Map.Entry<MWSkin, Integer>> teamData = this.perTeamRenderList[i];
            if (teamData.isEmpty()) continue;
            int x = this.rendererPosition.getAbsoluteRenderX();
            final String label = getTeamDisplayName(i);
            mc.fontRendererObj.drawStringWithShadow(label, x, y, 0xFFFFFF);
            x += mc.fontRendererObj.getStringWidth(label) + 4;
            drawClassListAt(mc.fontRendererObj, x, y, teamData, 100);
            y += mc.fontRendererObj.FONT_HEIGHT;
        }
    }

    private void renderClassHUD(List<Map.Entry<MWSkin, Integer>> list) {
        final int perLine = 5;
        final int x = this.rendererPosition.getAbsoluteRenderX();
        final int y = this.rendererPosition.getAbsoluteRenderY();
        drawClassListAt(Minecraft.getMinecraft().fontRendererObj, x, y, list, perLine);
    }

    private void drawClassListAt(FontRenderer fr, final int x, final int y, List<Map.Entry<MWSkin, Integer>> list, final int perLine) {
        final int size = list.size();

        int drawX = x;
        int maxWidth = 0;
        int col = 0;
        int index = 0;

        while (col < perLine && index < size) {
            final Map.Entry<MWSkin, Integer> entry = list.get(index);
            final int drawY = y + (index / perLine) * fr.FONT_HEIGHT;

            RenderUtil.renderSkinHead(entry.getKey().getSkin(), drawX, drawY, true, 8);
            final String text = entry.getValue().toString();
            maxWidth = Math.max(maxWidth, fr.getStringWidth(text));
            fr.drawStringWithShadow(text, drawX + 8 + 1, drawY, 0xFFFFFF);

            index += perLine;
            if (index >= size) {
                col++;
                index = col;
                drawX += maxWidth + 1 + 8 + 1;
                maxWidth = 0;
            }
        }
    }

    private String getTeamDisplayName(int ordinal) {
        final MWTeam team = this.teams[ordinal];
        return team.getColorPrefix() + '[' + team.name().charAt(0) + ']';
    }

    private MWSkin getDefaultSkin(NetworkPlayerInfo netInfo) {
        final MWSkin skin = MWSkin.ofPlayer(netInfo);
        if (skin == null || skin == MWSkin.RANDOM) return skin;
        return MWSkin.fromName(skin.mwClass.className);
    }

}