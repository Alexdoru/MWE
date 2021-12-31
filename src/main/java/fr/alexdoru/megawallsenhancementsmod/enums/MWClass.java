package fr.alexdoru.megawallsenhancementsmod.enums;

public enum MWClass {

    ARCANIST("arc", "Arcanist"),
    ASSASSIN("asn", "Assassin"),
    AUTOMATON("atn", "Automaton"),
    BLAZE("bla", "Blaze"),
    CREEPER("cre", "Creeper"),
    COW("cow", "Cow"),
    DREADLORD("dre", "Dreadlord"),
    ENDERMAN("end", "Enderman"),
    GOLEM("gol", "Golem"),
    HEROBRINE("hbr", "Herobrine"),
    HUNTER("hun", "Hunter"),
    MOLEMAN("mol", "Moleman"),
    PHOENIX("phx", "Phoenix"),
    PIRATE("pir", "Pirate"),
    RENEGADE("ren", "Renegade"),
    SHAMAN("sha", "Shaman"),
    SHARK("srk", "Shark"),
    SKELETON("ske", "Skeleton"),
    SNOWMAN("sno", "Snowman"),
    SPIDER("spi", "Spider"),
    SQUID("squ", "Squid"),
    PIGMAN("pig", "Pigman"),
    WEREWOLF("wer", "Werewolf"),
    ZOMBIE("zom", "Zombie");

    public final String tag;
    public final String className;

    MWClass(String tag, String className) {
        this.tag = tag;
        this.className = className;
    }

    public static MWClass fromTagOrName(String nameIn) {
        for (MWClass mwClass : values()) {
            if (nameIn.equalsIgnoreCase(mwClass.tag) || nameIn.equalsIgnoreCase(mwClass.className)) {
                return mwClass;
            }
        }
        return null;
    }

}
