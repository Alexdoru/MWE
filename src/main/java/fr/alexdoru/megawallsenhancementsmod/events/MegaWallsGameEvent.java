package fr.alexdoru.megawallsenhancementsmod.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class MegaWallsGameEvent extends Event {

    public enum EventType {
        CONNECT,
        DISCONNECT,
        GAME_START,
        GAME_END,
        THIRD_WITHER_DEATH,
        DEATHMATCH_START
    }

    private final EventType type;

    /**
     * CONNECT is fired when a game of MW starts or when you rejoin a server of mega walls
     * DISCONNECT is fired when you leave a game of MW
     * GAME_START is fired when the gates at spawn open
     * GAME_END is fired when the game ends
     * THIRD_WITHER_DIED is fired when the third wither dies
     * DEATHMATCH_START is fired when the last wither dies
     */
    public MegaWallsGameEvent(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

}
