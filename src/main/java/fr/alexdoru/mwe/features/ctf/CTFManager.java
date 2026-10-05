package fr.alexdoru.mwe.features.ctf;

import fr.alexdoru.mwe.api.IScoreboardParser;
import fr.alexdoru.mwe.api.enums.MWMap;
import fr.alexdoru.mwe.api.events.MapEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent.Type;
import fr.alexdoru.mwe.api.events.MegaWallsGameTimeEvent;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.scoreboard.ScoreboardUtils;
import fr.alexdoru.mwe.utils.SoundUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.regex.Pattern;

public final class CTFManager {

    private static final Pattern GAME_ID_PATTERN = Pattern.compile("\\d+/\\d+/\\d+\\s+\\w+");
    private static final int SPAWN_TIME = 10 + 6 * 60 + 30 + 30;
    private final IScoreboardParser parser = ScoreboardTracker.getParser();
    private MWMap map;
    private String currentGameID;
    private FlagObjective flag1, flag2;

    @SubscribeEvent
    public void on(MapEvent event) {
        if (this.parser.isInMwGame()) {
            map = MWMap.fromName(event.mapName);
        }
    }

    @SubscribeEvent
    public void on(MegaWallsGameTimeEvent event) {
        if (event.time >= SPAWN_TIME && ScoreboardTracker.getParser().getAliveWithers().size() == 4) {
            if (MWEConfig.captureTheFlag && flag1 == null) {
                this.startGame();
                if (event.time == SPAWN_TIME) {
                    SoundUtil.playNotePling();
                    if (flag1 != null) {
                        flag1.printLocation();
                    }
                    if (flag2 != null) {
                        flag2.printLocation();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void on(MegaWallsGameEvent event) {
        if (event.type == Type.DISCONNECT || event.type == Type.GAME_END || event.type == Type.FIRST_WITHER_DEATH) {
            this.stopGame();
        }
    }

    public void onSettingToggle() {
        if (!MWEConfig.captureTheFlag) {
            this.stopGame();
        }
    }

    private void startGame() {
        if (map != null && this.parser.getWitherCount() == 4) {
            final String gameID = getGameIdentifier(map);
            if (gameID != null && !gameID.equals(currentGameID)) {
                currentGameID = gameID;
                final int seed = gameID.hashCode();
                flag1 = new FlagObjective(map, new Random(seed));
                MinecraftForge.EVENT_BUS.register(flag1);
                flag2 = flag1.getOppositeSideFlag();
                if (flag2 != null) {
                    MinecraftForge.EVENT_BUS.register(flag2);
                }
            }
        }
    }

    private void stopGame() {
        if (flag1 != null) {
            MinecraftForge.EVENT_BUS.unregister(flag1);
            flag1 = null;
        }
        if (flag2 != null) {
            MinecraftForge.EVENT_BUS.unregister(flag2);
            flag2 = null;
        }
        currentGameID = null;
    }

    private static @Nullable String getGameIdentifier(@NotNull MWMap map) {
        String gameIdLine = null;
        for (final String line : ScoreboardUtils.getUnformattedSidebarText()) {
            if (GAME_ID_PATTERN.matcher(line).find()) {
                gameIdLine = line;
                break;
            }
        }
        if (gameIdLine == null) {
            return null;
        }
        return map.name() + gameIdLine;
    }

}
