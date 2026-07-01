package fr.alexdoru.mwe.api.enums;

import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum MWClass {

    ANGEL("ANG"),
    ARCANIST("ARC"),
    ASSASSIN("ASN"),
    AUTOMATON("ATN"),
    BLAZE("BLA"),
    COW("COW"),
    CREEPER("CRE"),
    DRAGON("DRG"),
    DREADLORD("DRE"),
    ENDERMAN("END"),
    GOLEM("GOL"),
    HEROBRINE("HBR"),
    HUNTER("HUN"),
    MOLEMAN("MOL"),
    PHOENIX("PHX"),
    PIGMAN("PIG"),
    PIRATE("PIR"),
    RENEGADE("REN"),
    SHAMAN("SHA"),
    SHARK("SRK"),
    SHEEP("SHP"),
    SKELETON("SKE"),
    SNOWMAN("SNO"),
    SPIDER("SPI"),
    SQUID("SQU"),
    WEREWOLF("WER"),
    ZOMBIE("ZOM");

    private static final Map<String, MWClass> tagMap = new HashMap<>();

    static {
        for (final MWClass mwClass : values()) {
            tagMap.put(mwClass.TAG, mwClass);
        }
    }

    public final String TAG;
    public final String className;

    MWClass(String TAG) {
        this.TAG = TAG;
        this.className = StringUtil.uppercaseFirstLetter(this.name().toLowerCase());
    }

    public static MWClass fromTagOrName(@NotNull String nameIn) {
        for (final MWClass mwClass : values()) {
            if (nameIn.equalsIgnoreCase(mwClass.TAG) || nameIn.equalsIgnoreCase(mwClass.className)) {
                return mwClass;
            }
        }
        return null;
    }

    /**
     * Returns the MWClass of a player, might be bull
     */
    @Nullable
    public static MWClass ofPlayer(@NotNull String playername) {
        final WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null) return null;
        final ScorePlayerTeam team = world.getScoreboard().getPlayersTeam(playername);
        if (team == null) return null;
        return MWClass.fromTeamTag(ScoreboardTracker.isMWReplay() ? team.getColorPrefix() : team.getColorSuffix());
    }

    /**
     * Returns the MWClass of a player, might be bull
     */
    @Nullable
    public static MWClass ofPlayer(@NotNull UUID uuid) {
        final NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();
        if (netHandler == null) return null;
        final ScorePlayerTeam team = netHandler.getPlayerInfo(uuid).getPlayerTeam();
        if (team == null) return null;
        return MWClass.fromTeamTag(ScoreboardTracker.isMWReplay() ? team.getColorPrefix() : team.getColorSuffix());
    }

    public static MWClass fromTeamTag(@NotNull String teamTag) {
        return MWClass.fromTag(StringUtil.removeFormattingCodes(teamTag).replaceAll("[\\[\\]\\s]", ""));
    }

    public static MWClass fromTag(String TAG) {
        return tagMap.get(TAG);
    }

}
