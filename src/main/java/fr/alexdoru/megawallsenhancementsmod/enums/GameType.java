package fr.alexdoru.megawallsenhancementsmod.enums;

import java.util.HashMap;
import java.util.Map;

public enum GameType {

    ARCADE("ARCADE", "Arcade"),
    ARENA("ARENA", "Arena"),
    BEDWARS("BEDWARS", "Bed Wars"),
    BLITZ("SURVIVAL_GAMES", "Blitz Survival Games"),
    BUILD_BATTLE("BUILD_BATTLE", "Build Battle"),
    CLASSIC_GAMES("LEGACY", "Classic Games"),
    CRAZY_WALLS("TRUE_COMBAT", "Crazy Walls"),
    DUELS("DUELS", "Duels"),
    HOUSING("HOUSING", "Housing"),
    MCGO("MCGO", "Cops and Crims"),
    MEGA_WALLS("WALLS3", "Mega Walls"),
    MURDER_MYSTERY("MURDER_MYSTERY", "Murder Mystery"),
    PAINTBALL("PAINTBALL", "Paintball"),
    PIT("PIT", "Pit"),
    PROTOTYPE("PROTOTYPE", "Prototype"),
    QUAKECRAFT("QUAKECRAFT", "Quake"),
    REPLAY("REPLAY", "Replay"),
    SKYBLOCK("SKYBLOCK", "SkyBlock"),
    SKYCLASH("SKYCLASH", "SkyClash"),
    SKYWARS("SKYWARS", "SkyWars"),
    SMASH_HEROES("SUPER_SMASH", "Smash Heroes"),
    SMP("SMP", "SMP"),
    SPEED_UHC("SPEED_UHC", "Speed UHC"),
    TKR("GINGERBREAD", "Turbo Kart Racers"),
    TNTGAMES("TNTGAMES", "TNT Games"),
    UHC("UHC", "UHC Champions"),
    UNKNOWN("?", "?"),
    VAMPIREZ("VAMPIREZ", "VampireZ"),
    WALLS("WALLS", "Walls"),
    WARLORDS("BATTLEGROUND", "Warlords");

    public final String id; // game name on the API
    public final String friendlyName; // understandable game name

    private static final Map<String, GameType> typeMap = new HashMap<>();

    static {
        for (final GameType game : values()) {
            typeMap.put(game.id, game);
        }
    }

    GameType(String id, String friendlyName) {
        if (id == null) {
            id = name();
        }
        this.id = id;
        this.friendlyName = friendlyName;
    }

    public static GameType fromId(String id) {
        return typeMap.getOrDefault(id, UNKNOWN);
    }

    @Override
    public String toString() {
        return friendlyName;
    }

}