package fr.alexdoru.mwe.api.events;

import fr.alexdoru.mwe.api.enums.MWTeam;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired during Mega Walls when a wither's health decays.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public final class WitherHealthDecayEvent extends Event {

    /**
     * The Wither's team
     */
    @NotNull
    public final MWTeam team;

    /**
     * The health of the wither
     */
    public final int health;

    public WitherHealthDecayEvent(@NotNull MWTeam team, int health) {
        this.team = team;
        this.health = health;
    }

}
