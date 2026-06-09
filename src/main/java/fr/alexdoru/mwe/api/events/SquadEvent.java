package fr.alexdoru.mwe.api.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class SquadEvent extends Event {

    @NotNull
    public final Type type;
    /**
     * the name of the player
     */
    @NotNull
    public final String playername;

    public SquadEvent(@NotNull Type type, @NotNull String playername) {
        this.type = type;
        this.playername = playername;
    }

    public enum Type {
        /**
         * fired when a player is added to the squad
         */
        ADDED,
        /**
         * fired when the alias for a squadmate is changed
         */
        NAME_CHANGED,
        /**
         * fired when a player is removed from the squad
         */
        REMOVED
    }
}
