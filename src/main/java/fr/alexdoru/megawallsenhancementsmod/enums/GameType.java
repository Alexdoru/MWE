package fr.alexdoru.megawallsenhancementsmod.enums;

import java.util.HashMap;
import java.util.Map;

public enum GameType {

    QUAKECRAFT("QUAKECRAFT", "Quake"),
    WALLS("WALLS", "Walls"),
    PAINTBALL("PAINTBALL", "Paintball"),
    BLITZ("SURVIVAL_GAMES", "Blitz Survival Games"),
    TNTGAMES("TNTGAMES", "TNT Games"),
    VAMPIREZ("VAMPIREZ", "VampireZ"),
    MEGA_WALLS("WALLS3", "Mega Walls"),
    ARCADE("ARCADE", "Arcade"),
    ARENA("ARENA", "Arena"),
    UHC("UHC", "UHC Champions"),
    MCGO("MCGO", "Cops and Crims"),
    WARLORDS("BATTLEGROUND", "Warlords"),
    SMASH_HEROES("SUPER_SMASH", "Smash Heroes"),
    TKR("GINGERBREAD", "Turbo Kart Racers"),
    HOUSING("HOUSING", "Housing"),
    SKYWARS("SKYWARS", "SkyWars"),
    CRAZY_WALLS("TRUE_COMBAT", "Crazy Walls"),
    SPEED_UHC("SPEED_UHC", "Speed UHC"),
    SKYCLASH("SKYCLASH", "SkyClash"),
    CLASSIC_GAMES("LEGACY", "Classic Games"),
    PROTOTYPE("PROTOTYPE", "Prototype"),
    BEDWARS("BEDWARS", "Bed Wars"),
    MURDER_MYSTERY("MURDER_MYSTERY", "Murder Mystery"),
    BUILD_BATTLE("BUILD_BATTLE", "Build Battle"),
    DUELS("DUELS", "Duels"),
    SKYBLOCK("SKYBLOCK", "SkyBlock"),
    PIT("PIT", "Pit"),
    REPLAY("REPLAY", "Replay"),
    SMP("SMP", "SMP"),
    UNKNOWN("?", "?");

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