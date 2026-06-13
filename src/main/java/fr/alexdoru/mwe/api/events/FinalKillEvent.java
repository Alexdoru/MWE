package fr.alexdoru.mwe.api.events;

import fr.alexdoru.mwe.api.enums.MWClass;
import fr.alexdoru.mwe.api.enums.MWTeam;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public abstract class FinalKillEvent extends Event {

    /** The name of the victim */
    @NotNull
    public final String victim;
    /** The team of the victim */
    @NotNull
    public final MWTeam victimTeam;
    /** The class of the victim */
    @Nullable
    public final MWClass victimClass;

    public FinalKillEvent(@NotNull String victim, @NotNull MWTeam victimTeam) {
        this.victim = victim;
        this.victimTeam = victimTeam;
        this.victimClass = MWClass.ofPlayer(victim);
    }

    public static final class PlayerKill extends FinalKillEvent {

        /** The name of the killer */
        @NotNull
        public final String killer;
        /** The team of the killer */
        @NotNull
        public final MWTeam killerTeam;
        /** The class of the killer */
        @Nullable
        public final MWClass killerClass;

        public PlayerKill(
                @NotNull String victim,
                @NotNull MWTeam victimTeam,
                @NotNull String killer,
                @NotNull MWTeam killerTeam) {
            super(victim, victimTeam);
            this.killer = killer;
            this.killerTeam = killerTeam;
            this.killerClass = MWClass.ofPlayer(killer);
        }

    }

    public static final class NaturalDeath extends FinalKillEvent {

        public NaturalDeath(@NotNull String victim, @NotNull MWTeam victimTeam) {
            super(victim, victimTeam);
        }

    }

}
