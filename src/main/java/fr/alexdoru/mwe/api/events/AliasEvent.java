package fr.alexdoru.mwe.api.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public final class AliasEvent extends Event {

    @NotNull
    public final Type type;
    /**
     * UUID of the player if the player is not nicked, might be null
     */
    @Nullable
    public final UUID uuid;
    /**
     * Name of the player if the player is nicked, might be null
     */
    @Nullable
    public final String playername;

    public AliasEvent(@NotNull Type type, @Nullable UUID uuid, @Nullable String playername) {
        this.type = type;
        this.uuid = uuid;
        this.playername = playername;
    }

    public enum Type {
        /**
         * fired when an alias is added to a player
         */
        ADDED,
        /**
         * fired when the alias for a player is changed
         */
        ALIAS_CHANGED,
        /**
         * fired when an alias for a player is removed
         */
        REMOVED
    }

}
