package fr.alexdoru.mwe.gui.huds;

import fr.alexdoru.mwe.api.enums.MWSkin;
import fr.alexdoru.mwe.api.enums.MWTeam;
import fr.alexdoru.mwe.api.utils.MapUtil;
import fr.alexdoru.mwe.api.utils.RenderUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.utils.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.*;

public class ClassCountHUD extends AbstractRenderer {

    private static final int PER_LINE_HEADS = 9;

    private final TimerUtil timer = new TimerUtil(1000L);

    private final List<Map.Entry<MWSkin, String>> dummyAllClassList;
    private List<Map.Entry<MWSkin, String>> allClassList = new ArrayList<>();

    private final MWTeam[] teams;
    private final List<Map.Entry<MWSkin, String>>[] perTeamRenderList;
    private final Map<MWSkin, Integer>[] perTeamData;

    @SuppressWarnings("unchecked")
    public ClassCountHUD() {
        super(MWEConfig.classCountHUDPosition);
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
        this.updateData();
        this.rendererPosition.updateAbsolutePosition(resolution);
        final int x = this.rendererPosition.getAbsoluteRenderX();
        final int y = this.rendererPosition.getAbsoluteRenderY();
        this.renderAt(x, y);
    }

    public void updateData() {
        if (this.timer.update()) {
            if (this.showPerTeamHud()) {
                this.updatePerTeamData();
            } else {
                this.updateClassData();
            }
        }
    }

    public void renderAt(int x, int y) {
        if (this.showPerTeamHud()) {
            this.renderTeamHUD(x, y);
        } else {
            this.renderClassHUD(this.allClassList, x, y);
        }
    }

    @Override
    public void renderDummy() {
        final int x = this.rendererPosition.getAbsoluteRenderX();
        final int y = this.rendererPosition.getAbsoluteRenderY();
        this.renderClassHUD(this.dummyAllClassList, x, y);
    }

    @Override
    public boolean isEnabled(long currentTimeMillis) {
        return !MWEConfig.classCountHUDOnlyInTablist && this.isEnabled();
    }

    private boolean isEnabled() {
        return this.rendererPosition.isEnabled() && (this.parser.isPreGameLobby() || MWEConfig.classCountHUDDuringGames && (this.parser.isInMwGame() || this.parser.isMWReplay()));
    }

    public boolean isTabDisplayEnabled() {
        return MWEConfig.classCountHUDOnlyInTablist && this.isEnabled();
    }

    private boolean showPerTeamHud() {
        return MWEConfig.classCountHUDPerTeamDisplay && (this.parser.isInMwGame() || this.parser.isMWReplay());
    }

    public int getLinesCount() {
        if (this.showPerTeamHud()) {
            int count = 0;
            for (int i = 0; i < this.teams.length; i++) {
                if (!this.perTeamRenderList[i].isEmpty()) count++;
            }
            return count;
        } else {
            final int size = this.allClassList.size();
            if (size == 0) {
                return 0;
            }
            return size / PER_LINE_HEADS + (size % PER_LINE_HEADS == 0 ? 0 : 1);
        }
    }

    public int getWidth() {
        final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        final int singleWidth = 8 + 1 + fr.getStringWidth("1") + 1;
        if (this.showPerTeamHud()) {
            int width = 0;
            for (int i = 0; i < this.teams.length; i++) {
                width = Math.max(width, this.perTeamRenderList[i].size() * singleWidth);
            }
            final int header = fr.getStringWidth(getTeamDisplayName(0));
            return width + header;
        } else {
            return Math.min(this.allClassList.size(), PER_LINE_HEADS) * singleWidth;
        }
    }

    private void updatePerTeamData() {
        for (final Map<MWSkin, Integer> map : this.perTeamData) {
            map.clear();
        }
        for (final NetworkPlayerInfo netInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            if (isSpectator(netInfo)) continue;
            final MWTeam team = MWTeam.ofPlayer(netInfo);
            final MWSkin defaultSkin = this.getDefaultSkin(netInfo);
            if (team != null && defaultSkin != null) {
                this.perTeamData[team.ordinal()].merge(defaultSkin, 1, Integer::sum);
            }
        }
        for (int i = 0; i < this.teams.length; i++) {
            this.perTeamRenderList[i] = this.getSortedEntries(this.perTeamData[i]);
        }
    }

    private void updateClassData() {
        final Map<MWSkin, Integer> map = new HashMap<>();
        for (final NetworkPlayerInfo netInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            if (isSpectator(netInfo)) continue;
            final MWSkin defaultSkin = this.getDefaultSkin(netInfo);
            if (defaultSkin != null) {
                map.merge(defaultSkin, 1, Integer::sum);
            }
        }
        this.allClassList = this.getSortedEntries(map);
    }

    private boolean isSpectator(NetworkPlayerInfo netInfo) {
        final ScorePlayerTeam team = netInfo.getPlayerTeam();
        if (team == null) return false;
        return team.getColorPrefix().indexOf('✖') > -1;
    }

    private List<Map.Entry<MWSkin, String>> getSortedEntries(Map<MWSkin, Integer> map) {
        if (map.isEmpty()) {
            return Collections.emptyList();
        }
        final Map<MWSkin, String> stringMap = new HashMap<>();
        for (final Map.Entry<MWSkin, Integer> entry : map.entrySet()) {
            stringMap.put(entry.getKey(), entry.getValue().toString());
        }
        return MapUtil.sortByKey(stringMap);
    }

    private void renderTeamHUD(int x, int y) {
        final Minecraft mc = Minecraft.getMinecraft();
        for (int i = 0; i < this.perTeamRenderList.length; i++) {
            final List<Map.Entry<MWSkin, String>> teamData = this.perTeamRenderList[i];
            if (teamData.isEmpty()) continue;
            int drawX = x;
            final String label = getTeamDisplayName(i);
            mc.fontRendererObj.drawStringWithShadow(label, drawX, y, 0xFFFFFF);
            drawX += mc.fontRendererObj.getStringWidth(label) + 4;
            drawClassListAt(mc.fontRendererObj, drawX, y, teamData, 100);
            y += mc.fontRendererObj.FONT_HEIGHT;
        }
    }

    private void renderClassHUD(List<Map.Entry<MWSkin, String>> list, int x, int y) {
        this.drawClassListAt(Minecraft.getMinecraft().fontRendererObj, x, y, list, PER_LINE_HEADS);
    }

    private void drawClassListAt(FontRenderer fr, final int x, final int y, List<Map.Entry<MWSkin, String>> list, final int perLine) {
        final int size = list.size();

        int drawX = x;
        int maxWidth = 0;
        int col = 0;
        int index = 0;

        while (col < perLine && index < size) {
            final Map.Entry<MWSkin, String> entry = list.get(index);
            final int drawY = y + (index / perLine) * fr.FONT_HEIGHT;

            RenderUtil.renderSkinHead(entry.getKey().getSkin(), drawX, drawY, true, 8);
            final String text = entry.getValue();
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