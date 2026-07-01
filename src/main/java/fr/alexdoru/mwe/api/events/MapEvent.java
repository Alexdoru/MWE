package fr.alexdoru.mwe.api.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when the mod sets map data.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public final class MapEvent extends Event {

    @NotNull
    public final String mapName;

    public MapEvent(@NotNull String mapName) {
        this.mapName = mapName;
    }

}
