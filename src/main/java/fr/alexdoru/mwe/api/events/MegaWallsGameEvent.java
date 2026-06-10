package fr.alexdoru.mwe.api.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public final class MegaWallsGameEvent extends Event {

    @NotNull
    public final Type type;

    public MegaWallsGameEvent(@NotNull Type type) {
        this.type = type;
    }

    public enum Type {
        /**
         * fired when a game of MW starts or when you rejoin a server of mega walls
         */
        CONNECT,
        /**
         * fired when you leave a game of MW
         */
        DISCONNECT,
        /**
         * fired when the gates at spawn open
         */
        GAME_START,
        /**
         * fired when the game ends
         */
        GAME_END,
        /**
         * fired when the third wither dies
         */
        THIRD_WITHER_DEATH,
        /**
         * fired when the last wither dies
         */
        DEATHMATCH_START
    }
}
