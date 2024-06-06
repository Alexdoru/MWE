package fr.alexdoru.megawallsenhancementsmod.enums;

import fr.alexdoru.megawallsenhancementsmod.utils.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Map;

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

    public final String TAG;
    public final String className;

    private static final Map<String, MWClass> tagMap = new HashMap<>();

    static {
        for (final MWClass mwClass : values()) {
            tagMap.put(mwClass.TAG, mwClass);
        }
    }

    MWClass(String TAG) {
        this.TAG = TAG;
        this.className = StringUtil.uppercaseFirstLetter(this.name().toLowerCase());
    }

    public static MWClass fromTagOrName(String nameIn) {
        for (final MWClass mwClass : values()) {
            if (nameIn.equalsIgnoreCase(mwClass.TAG) || nameIn.equalsIgnoreCase(mwClass.className)) {
                return mwClass;
            }
        }
        return null;
    }

    public static MWClass ofPlayer(String playername) {
        final WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null) {
            return null;
        }
        final ScorePlayerTeam team = world.getScoreboard().getPlayersTeam(playername);
        if (team == null) {
            return null;
        }
        return MWClass.fromTeamTag(team.getColorSuffix());
    }

    public static MWClass fromTeamTag(String teamTag) {
        return MWClass.fromTag(EnumChatFormatting.getTextWithoutFormattingCodes(teamTag).replaceAll("[\\[\\]\\s]", ""));
    }

    public static MWClass fromTag(String TAG) {
        return tagMap.get(TAG);
    }

}
