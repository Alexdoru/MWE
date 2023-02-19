package fr.alexdoru.megawallsenhancementsmod.enums;

import java.util.Map;

import com.google.common.collect.Maps;

public enum MWClass {

    ARCANIST("ARC", "arc", "Arcanist"),
    ASSASSIN("ASN", "asn", "Assassin"),
    AUTOMATON("ATN", "atn", "Automaton"),
    BLAZE("BLA", "bla", "Blaze"),
    CREEPER("CRE", "cre", "Creeper"),
    COW("COW", "cow", "Cow"),
    DREADLORD("DRE", "dre", "Dreadlord"),
    ENDERMAN("END", "end", "Enderman"),
    GOLEM("GOL", "gol", "Golem"),
    HEROBRINE("HBR", "hbr", "Herobrine"),
    HUNTER("HUN", "hun", "Hunter"),
    MOLEMAN("MOL", "mol", "Moleman"),
    PHOENIX("PHX", "phx", "Phoenix"),
    PIRATE("PIR", "pir", "Pirate"),
    RENEGADE("REN", "ren", "Renegade"),
    SHAMAN("SHA", "sha", "Shaman"),
    SHARK("SRK", "srk", "Shark"),
    SKELETON("SKE", "ske", "Skeleton"),
    SNOWMAN("SNO", "sno", "Snowman"),
    SPIDER("SPI", "spi", "Spider"),
    SQUID("SQU", "squ", "Squid"),
    PIGMAN("PIG", "pig", "Pigman"),
    WEREWOLF("WER", "wer", "Werewolf"),
    ZOMBIE("ZOM", "zom", "Zombie");

    public final String TAG;
    public final String tagLowerCase;
    public final String className;

    MWClass(String TAG, String tagLowerCase, String className) {
        this.TAG = TAG;
        this.tagLowerCase = tagLowerCase;
        this.className = className;
    }
    private static final Map<String, MWClass> tagMap = Maps.newHashMap();

    static {
        for(final MWClass mwClass : values()) {
            tagMap.put(mwClass.TAG, mwClass);
        }
    }

    public static MWClass fromTagOrName(String nameIn) {
        for (final MWClass mwClass : values()) {
            if (nameIn.equalsIgnoreCase(mwClass.tagLowerCase) || nameIn.equalsIgnoreCase(mwClass.className)) {
                return mwClass;
            }
        }
        return null;
    }

    public static MWClass fromTag(String TAG) {
        return tagMap.get(TAG);
    }

}
