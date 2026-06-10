package fr.alexdoru.mwe.api.events;

import fr.alexdoru.mwe.api.IReportInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public final class ReportListEvent extends Event {

    @NotNull
    public final Type type;
    /**
     * UUID of the reported player if the player is not nicked, might be null
     */
    @Nullable
    public final UUID uuid;
    /**
     * Name of the reported player if the player is nicked, might be null
     */
    @Nullable
    public final String playername;
    @NotNull
    public final IReportInfo reportInfo;

    public ReportListEvent(@NotNull Type type, @Nullable UUID uuid, @Nullable String playername, @NotNull IReportInfo reportInfo) {
        this.type = type;
        this.uuid = uuid;
        this.playername = playername;
        this.reportInfo = reportInfo;
    }

    public enum Type {
        /**
         * fired when a player is added to the report list
         */
        ADDED,
        /**
         * fired when a player is removed from the report list
         */
        REMOVED
    }
}
