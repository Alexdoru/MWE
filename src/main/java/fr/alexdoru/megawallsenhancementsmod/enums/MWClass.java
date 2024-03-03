package fr.alexdoru.megawallsenhancementsmod.enums;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Map;

public enum MWClass {

    ARCANIST("ARC", "Arcanist"),
    ASSASSIN("ASN", "Assassin"),
    AUTOMATON("ATN", "Automaton"),
    BLAZE("BLA", "Blaze"),
    COW("COW", "Cow"),
    CREEPER("CRE", "Creeper"),
    DREADLORD("DRE", "Dreadlord"),
    ENDERMAN("END", "Enderman"),
    GOLEM("GOL", "Golem"),
    HEROBRINE("HBR", "Herobrine"),
    HUNTER("HUN", "Hunter"),
    MOLEMAN("MOL", "Moleman"),
    PHOENIX("PHX", "Phoenix"),
    PIGMAN("PIG", "Pigman"),
    PIRATE("PIR", "Pirate"),
    RENEGADE("REN", "Renegade"),
    SHAMAN("SHA", "Shaman"),
    SHARK("SRK", "Shark"),
    SKELETON("SKE", "Skeleton"),
    SNOWMAN("SNO", "Snowman"),
    SPIDER("SPI", "Spider"),
    SQUID("SQU", "Squid"),
    WEREWOLF("WER", "Werewolf"),
    ZOMBIE("ZOM", "Zombie");

    public final String TAG;
    public final String className;

    private static final Map<String, MWClass> tagMap = new HashMap<>();

    static {
        for (final MWClass mwClass : values()) {
            tagMap.put(mwClass.TAG, mwClass);
        }
    }

    MWClass(String TAG, String className) {
        this.TAG = TAG;
        this.className = className;
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
