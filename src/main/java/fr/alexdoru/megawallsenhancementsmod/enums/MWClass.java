package fr.alexdoru.megawallsenhancementsmod.enums;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Map;

public enum MWClass {

    ARCANIST("ARC", "arc", "Arcanist"),
    ASSASSIN("ASN", "asn", "Assassin"),
    AUTOMATON("ATN", "atn", "Automaton"),
    BLAZE("BLA", "bla", "Blaze"),
    COW("COW", "cow", "Cow"),
    CREEPER("CRE", "cre", "Creeper"),
    DREADLORD("DRE", "dre", "Dreadlord"),
    ENDERMAN("END", "end", "Enderman"),
    GOLEM("GOL", "gol", "Golem"),
    HEROBRINE("HBR", "hbr", "Herobrine"),
    HUNTER("HUN", "hun", "Hunter"),
    MOLEMAN("MOL", "mol", "Moleman"),
    PHOENIX("PHX", "phx", "Phoenix"),
    PIGMAN("PIG", "pig", "Pigman"),
    PIRATE("PIR", "pir", "Pirate"),
    RENEGADE("REN", "ren", "Renegade"),
    SHAMAN("SHA", "sha", "Shaman"),
    SHARK("SRK", "srk", "Shark"),
    SKELETON("SKE", "ske", "Skeleton"),
    SNOWMAN("SNO", "sno", "Snowman"),
    SPIDER("SPI", "spi", "Spider"),
    SQUID("SQU", "squ", "Squid"),
    WEREWOLF("WER", "wer", "Werewolf"),
    ZOMBIE("ZOM", "zom", "Zombie");

    public final String TAG;
    public final String tagLowerCase;
    public final String className;

    private static final Map<String, MWClass> tagMap = new HashMap<>();

    static {
        for (final MWClass mwClass : values()) {
            tagMap.put(mwClass.TAG, mwClass);
        }
    }

    MWClass(String TAG, String tagLowerCase, String className) {
        this.TAG = TAG;
        this.tagLowerCase = tagLowerCase;
        this.className = className;
    }

    public static MWClass fromTagOrName(String nameIn) {
        for (final MWClass mwClass : values()) {
            if (nameIn.equalsIgnoreCase(mwClass.tagLowerCase) || nameIn.equalsIgnoreCase(mwClass.className)) {
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
