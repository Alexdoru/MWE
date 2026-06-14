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
public class KillCounterEvent extends Event {

    /** The type of Event */
    public final Type type;
    /** The name of the victim */
    @NotNull
    public final String victim;
    /** The team of the victim */
    @NotNull
    public final MWTeam victimTeam;
    /** The class of the victim */
    @Nullable
    public final MWClass victimClass;

    public enum Type {
        /** Fired when a player gets killed by another player */
        NORMAL_KILL,
        /** Fired when a player gets final killed by another player */
        FINAL_KILL,
        /** Fired for normal kills when a player dies on his own */
        NORMAL_DEATH,
        /** Fired for final kills when a player dies on his own */
        FINAL_DEATH
    }

    public KillCounterEvent(@NotNull Type type, @NotNull String victim, @NotNull MWTeam victimTeam) {
        this.type = type;
        this.victim = victim;
        this.victimTeam = victimTeam;
        this.victimClass = MWClass.ofPlayer(victim);
    }

    public static abstract class KillEvent extends KillCounterEvent {

        /** The name of the killer */
        @NotNull
        public final String killer;
        /** The team of the killer */
        @NotNull
        public final MWTeam killerTeam;
        /** The class of the killer */
        @Nullable
        public final MWClass killerClass;

        public KillEvent(
                Type type,
                @NotNull String victim,
                @NotNull MWTeam victimTeam,
                @NotNull String killer,
                @NotNull MWTeam killerTeam) {
            super(type, victim, victimTeam);
            this.killer = killer;
            this.killerTeam = killerTeam;
            this.killerClass = MWClass.ofPlayer(killer);
        }

        @Override
        public String toString() {
            return "KillEvent{" +
                    "victimClass=" + victimClass +
                    ", victimTeam=" + victimTeam +
                    ", victim='" + victim + '\'' +
                    ", type=" + type +
                    ", killerClass=" + killerClass +
                    ", killerTeam=" + killerTeam +
                    ", killer='" + killer + '\'' +
                    '}';
        }

    }

    public static final class NormalKill extends KillEvent {

        public NormalKill(
                @NotNull String victim,
                @NotNull MWTeam victimTeam,
                @NotNull String killer,
                @NotNull MWTeam killerTeam) {
            super(Type.NORMAL_KILL, victim, victimTeam, killer, killerTeam);
        }

    }

    public static final class FinalKill extends KillEvent {

        public FinalKill(
                @NotNull String victim,
                @NotNull MWTeam victimTeam,
                @NotNull String killer,
                @NotNull MWTeam killerTeam) {
            super(Type.FINAL_KILL, victim, victimTeam, killer, killerTeam);
        }

    }

    public static abstract class DeathEvent extends KillCounterEvent {

        public DeathEvent(@NotNull Type type, @NotNull String victim, @NotNull MWTeam victimTeam) {
            super(type, victim, victimTeam);
        }

        @Override
        public String toString() {
            return "DeathEvent{" +
                    "type=" + type +
                    ", victim='" + victim + '\'' +
                    ", victimTeam=" + victimTeam +
                    ", victimClass=" + victimClass +
                    '}';
        }

    }

    public static final class NormalDeath extends DeathEvent {

        public NormalDeath(@NotNull String victim, @NotNull MWTeam victimTeam) {
            super(Type.NORMAL_DEATH, victim, victimTeam);
        }

    }

    public static final class FinalDeath extends DeathEvent {

        public FinalDeath(@NotNull String victim, @NotNull MWTeam victimTeam) {
            super(Type.FINAL_DEATH, victim, victimTeam);
        }

    }

}
