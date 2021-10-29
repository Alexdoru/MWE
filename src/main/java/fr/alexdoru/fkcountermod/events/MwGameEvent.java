package fr.alexdoru.fkcountermod.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class MwGameEvent extends Event {

    public enum EventType {
        CONNECT,
        DISCONNECT,
        GAME_START,
        GAME_END
    }

    private final EventType type;

    /**
     * CONNECT is fired when a game of MW starts or when you rejoin a server of mega walls
     * DISCONNECT is fired when you leave a game of MW
     * GAME_START is fired when the gates open
     * GAME_END is fired when the game ends
     */
    public MwGameEvent(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

}
