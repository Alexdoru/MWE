package fr.alexdoru.mwe.scoreboard;

import fr.alexdoru.mwe.api.enums.MWTeam;
import fr.alexdoru.mwe.api.events.MapEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent.Type;
import fr.alexdoru.mwe.api.events.WitherHealthDecayEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

public final class ScoreboardTracker {

    private static final ScoreboardParser PARSER = new ScoreboardParser();

    private final EventBus eventBus;
    private boolean prevIsInMW;
    private boolean prevHasGameEnded;
    private int prevAmountWitherAlive;
    private int prevBlueHp;
    private int prevGreenHp;
    private int prevRedHp;
    private int prevYellowHp;

    public ScoreboardTracker() {
        this(MinecraftForge.EVENT_BUS);
    }

    ScoreboardTracker(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @SubscribeEvent
    public void onGameStart(MegaWallsGameEvent event) {
        if (event.type == Type.GAME_START) {
            PARSER.onGameStart();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            final Profiler profiler = Minecraft.getMinecraft().mcProfiler;
            profiler.startSection("MWE Scoreboard");
            PARSER.reset();
            PARSER.update();
            this.fireEvents();
            profiler.endSection();
        }
    }

    void fireEvents() {

        if (PARSER.isMWReplay() && PARSER.getReplayMap() != null) {
            this.eventBus.post(new MapEvent(PARSER.getReplayMap()));
        }

        if (PARSER.isInMwGame()) {

            if (!this.prevIsInMW) {
                this.eventBus.post(new MegaWallsGameEvent(Type.CONNECT));
            }

            if (PARSER.getWitherCount() == 3 && prevAmountWitherAlive > 3) {
                this.eventBus.post(new MegaWallsGameEvent(Type.FIRST_WITHER_DEATH));
            }
            if (PARSER.getWitherCount() == 1 && prevAmountWitherAlive > 1) {
                this.eventBus.post(new MegaWallsGameEvent(Type.THIRD_WITHER_DEATH));
            }
            if (PARSER.getWitherCount() == 0 && prevAmountWitherAlive > 0) {
                this.eventBus.post(new MegaWallsGameEvent(Type.DEATHMATCH_START));
            }

            if (this.prevBlueHp != 0 && this.prevBlueHp > PARSER.getBlueWitherHp()) {
                this.eventBus.post(new WitherHealthDecayEvent(MWTeam.BLUE, PARSER.getBlueWitherHp()));
            }
            if (this.prevGreenHp != 0 && this.prevGreenHp > PARSER.getGreenWitherHp()) {
                this.eventBus.post(new WitherHealthDecayEvent(MWTeam.GREEN, PARSER.getGreenWitherHp()));
            }
            if (this.prevRedHp != 0 && this.prevRedHp > PARSER.getRedWitherHp()) {
                this.eventBus.post(new WitherHealthDecayEvent(MWTeam.RED, PARSER.getRedWitherHp()));
            }
            if (this.prevYellowHp != 0 && this.prevYellowHp > PARSER.getYellowWitherHp()) {
                this.eventBus.post(new WitherHealthDecayEvent(MWTeam.YELLOW, PARSER.getYellowWitherHp()));
            }

        } else {

            if (this.prevIsInMW) {
                this.eventBus.post(new MegaWallsGameEvent(Type.DISCONNECT));
            }

        }

        if (PARSER.hasGameEnded() && !this.prevHasGameEnded) {
            this.eventBus.post(new MegaWallsGameEvent(Type.GAME_END));
        }

        this.prevIsInMW = PARSER.isInMwGame();
        this.prevHasGameEnded = PARSER.hasGameEnded();
        this.prevAmountWitherAlive = PARSER.getWitherCount();
        this.prevBlueHp = PARSER.getBlueWitherHp();
        this.prevGreenHp = PARSER.getGreenWitherHp();
        this.prevRedHp = PARSER.getRedWitherHp();
        this.prevYellowHp = PARSER.getYellowWitherHp();

    }

    @NotNull
    public static ScoreboardParser getParser() {
        return PARSER;
    }

    public static boolean isInMwGame() {
        return PARSER.isInMwGame();
    }

    public static boolean isPreGameLobby() {
        return PARSER.isPreGameLobby();
    }

    public static boolean isPrepPhase() {
        return PARSER.isPrepPhase();
    }

    public static boolean isMWEnvironement() {
        return PARSER.isMWEnvironement();
    }

    public static boolean isReplayMode() {
        return PARSER.isReplayMode();
    }

    public static boolean isAtlasMode() {
        return PARSER.isAtlasMode();
    }

    public static boolean isMWReplay() {
        return PARSER.isMWReplay();
    }

    public static String getServerID() {
        return PARSER.getServerID();
    }

}