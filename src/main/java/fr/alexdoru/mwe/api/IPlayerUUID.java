package fr.alexdoru.mwe.api;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface IPlayerUUID {

    /**
     * The exact name of the player (respects the case)
     */
    @NotNull
    String getName();

    /**
     * The UUID if the player
     */
    @NotNull
    UUID getId();

}
