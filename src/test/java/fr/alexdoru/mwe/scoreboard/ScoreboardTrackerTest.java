package fr.alexdoru.mwe.scoreboard;

import fr.alexdoru.mwe.api.enums.MWTeam;
import fr.alexdoru.mwe.api.events.MapEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.api.events.MegaWallsGameTimeEvent;
import fr.alexdoru.mwe.api.events.WitherHealthDecayEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ScoreboardTrackerTest {

    private static class FakeEventBus extends EventBus {

        public final List<Event> postedEvents = new ArrayList<>();

        @Override
        public boolean post(Event event) {
            this.postedEvents.add(event);
            return false;
        }

    }

    private final FakeEventBus eventBus = new FakeEventBus();
    private ScoreboardTracker tracker;
    private ScoreboardParser parser;

    @Before
    public void setup() {
        this.eventBus.postedEvents.clear();
        this.tracker = new ScoreboardTracker(this.eventBus);
        this.parser = ScoreboardTracker.getParser();
        this.parser.reset();
    }

    private void onTick(String title, List<String> formattedLines) {
        this.eventBus.postedEvents.clear();
        this.parser.reset();
        this.parser.update(title, formattedLines);
        this.tracker.fireEvents();
    }

    private <T extends Event> void assertPostedEventOfType(Class<T> expectedType, int expectedAmount) {
        int amount = 0;
        for (final Event event : this.eventBus.postedEvents) {
            if (expectedType == event.getClass()) {
                amount++;
            }
        }
        assertEquals(expectedAmount, amount);
    }

    private <T extends Event> void assertPostedEventOfType(Class<T> expectedType, Consumer<T> test) {
        boolean found = false;
        for (final Event event : this.eventBus.postedEvents) {
            if (expectedType == event.getClass()) {
                //noinspection unchecked
                test.accept(((T) event));
                found = true;
            }
        }
        if (!found) {
            fail("Did not find Event matching the expected type");
        }
    }

    @Test
    public void mapEventTest() {
        final String title = "§e§lREPLAY";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M4§8C",
                "§7Replay §7from §7mega34B",
                "",
                "Date: §a08/30/20§a26",
                "Time: §a14:45 (E§aST)",
                "",
                "Game: §aMega Wal§als",
                "Mode: §aStandard",
                "",
                "Map: §aShadowsto§ane",
                "§ewww.hypixel.ne§et"
        ));
        this.onTick(title, formattedLines);
        assertPostedEventOfType(MapEvent.class, e -> assertEquals("Shadowstone", e.mapName));
    }

    @Test
    public void connectEventTest() {
        String title = "§e§lHYPIXEL";
        List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8L2§84B",
                "",
                "Rank: §bMVP§0+§b",
                "Achievements: §e§e10,060",
                "Hypixel Level: §3404",
                "",
                "Lobby: §a1",
                "Players: §a26,83§a7",
                "",
                "§eSummer 2026",
                "Event Level: §61§600",
                "",
                "§ewww.hypixel.ne§et"
        ));
        this.onTick(title, formattedLines);
        title = "§6§lMEGA WALLS";
        formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "Walls Fall: §a§a06:14",
                "",
                "§6[Y] Wither§6 H§6P§7: §61,000",
                "§1[B] §fWither§1§1 HP§7: §11,000",
                "§2[G] §fWither§2§2 HP§7: §21,000",
                "§c[R] §fWither§c§c HP§7: §c1,000",
                "",
                "§a0 §fKills §a0 Assists",
                "§a0 §fF. Kills §a0 §fF. Assists",
                "§60 §fCoins",
                "§60 §fClass Points",
                "",
                "§ewww.hypixel.ne§et"
        ));
        this.onTick(title, formattedLines);
        assertPostedEventOfType(MegaWallsGameEvent.class, e -> assertEquals(MegaWallsGameEvent.Type.CONNECT, e.type));
    }

    @Test
    public void disconnectEventTest() {
        String title = "§6§lMEGA WALLS";
        List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "Walls Fall: §a§a06:14",
                "",
                "§6[Y] Wither§6 H§6P§7: §61,000",
                "§1[B] §fWither§1§1 HP§7: §11,000",
                "§2[G] §fWither§2§2 HP§7: §21,000",
                "§c[R] §fWither§c§c HP§7: §c1,000",
                "",
                "§a0 §fKills §a0 Assists",
                "§a0 §fF. Kills §a0 §fF. Assists",
                "§60 §fCoins",
                "§60 §fClass Points",
                "",
                "§ewww.hypixel.ne§et"
        ));
        this.onTick(title, formattedLines);
        title = "§e§lHYPIXEL";
        formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8L2§84B",
                "",
                "Rank: §bMVP§0+§b",
                "Achievements: §e§e10,060",
                "Hypixel Level: §3404",
                "",
                "Lobby: §a1",
                "Players: §a26,83§a7",
                "",
                "§eSummer 2026",
                "Event Level: §61§600",
                "",
                "§ewww.hypixel.ne§et"
        ));
        this.onTick(title, formattedLines);
        assertPostedEventOfType(MegaWallsGameEvent.class, e -> assertEquals(MegaWallsGameEvent.Type.DISCONNECT, e.type));
    }

    @Test
    public void firstWitherDeathEventTest() {
        final String title = "§6§lMEGA WALLS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "Gates Open: §a§a00:06",
                "",
                "§6[Y] Wither§6 H§6P§7: §61,000",
                "§1[B] §fWither§1§1 HP§7: §11,000",
                "§2[G] §fWither§2§2 HP§7: §21,000",
                "§c[R] §fWither§c§c HP§7: §c1,000",
                "",
                "§a0 §fKills §a0 Assists",
                "§a0 §fF. Kills §a0 §fF. Assists",
                "§60 §fCoins",
                "§60 §fClass Points",
                "",
                "§ewww.hypixel.ne§et"
        ));
        this.onTick(title, formattedLines);
        formattedLines.set(6, "§c[R] §fPlayers§7: §c11");
        this.onTick(title, formattedLines);
        assertPostedEventOfType(MegaWallsGameEvent.class, e -> assertEquals(MegaWallsGameEvent.Type.FIRST_WITHER_DEATH, e.type));
    }

    @Test
    public void thirdWitherDeathEventTest() {
        final String title = "§6§lMEGA WALLS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "Game End: §a31§a:12",
                "",
                "§6[Y] Wither§6 H§6P§7: §6870",
                "§1[B] §fWither§1§1 HP§7: §1892",
                "§2[G] §fPlayers§7: §27",
                "§c[R] §fPlayers§7: §c11",
                "",
                "§a0 §fKills §a2 Assists",
                "§a0 §fF. Kills §a0 §fF. Assists",
                "§61,056 §fCoins",
                "§61 §fClass Point",
                "",
                "§ewww.hypixel.ne§et"
        ));
        this.onTick(title, formattedLines);
        formattedLines.set(4, "§1[B] §fPlayers§7: §19");
        this.onTick(title, formattedLines);
        assertPostedEventOfType(MegaWallsGameEvent.class, e -> assertEquals(MegaWallsGameEvent.Type.THIRD_WITHER_DEATH, e.type));
    }

    @Test
    public void deathmatchStartEventTest() {
        final String title = "§6§lMEGA WALLS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "Game End: §a31§a:12",
                "",
                "§6[Y] Wither§6 H§6P§7: §6870",
                "§1[B] §fPlayers§7: §19",
                "§2[G] §fPlayers§7: §27",
                "§c[R] §fPlayers§7: §c11",
                "",
                "§a0 §fKills §a2 Assists",
                "§a0 §fF. Kills §a0 §fF. Assists",
                "§61,056 §fCoins",
                "§61 §fClass Point",
                "",
                "§ewww.hypixel.ne§et"
        ));
        this.onTick(title, formattedLines);
        formattedLines.set(3, "§6[Y] Players§7:§7 §65");
        this.onTick(title, formattedLines);
        assertPostedEventOfType(MegaWallsGameEvent.class, e -> assertEquals(MegaWallsGameEvent.Type.DEATHMATCH_START, e.type));
    }

    @Test
    public void gameEndEventTest() {
        final String title = "§6§lMEGA WALLS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "Game End: §a03§a:38",
                "",
                "§6[Y] §fPlayers§7: §63",
                "§1[B] §fPlayers§7: §19",
                "§7Green eliminat§7ed!",
                "§7Red eliminated§7!",
                "",
                "§a6 §fKills §a8 Assists",
                "§a0 §fF. Kills §a0 §fF. Assists",
                "§615,509 §fCoins",
                "§616 §fClass Points",
                "",
                "§ewww.hypixel.ne§et"
        ));
        this.onTick(title, formattedLines);
        formattedLines.set(4, "§7Blue eliminate§7d!");
        this.onTick(title, formattedLines);
        assertPostedEventOfType(MegaWallsGameEvent.class, e -> assertEquals(MegaWallsGameEvent.Type.GAME_END, e.type));
    }

    @Test
    public void witherHealthDecayEventTest() {
        final String title = "§6§lMEGA WALLS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "Gates Open: §a§a00:06",
                "",
                "§6[Y] Wither§6 H§6P§7: §6854",
                "§1[B] §fWither§1§1 HP§7: §1158",
                "§2[G] §fWither§2§2 HP§7: §2458",
                "§c[R] §fWither§c§c HP§7: §c745",
                "",
                "§a0 §fKills §a0 Assists",
                "§a0 §fF. Kills §a0 §fF. Assists",
                "§60 §fCoins",
                "§60 §fClass Points",
                "",
                "§ewww.hypixel.ne§et"
        ));
        this.onTick(title, formattedLines);

        formattedLines.set(6, "§c[R] §fWither§c§c HP§7: §c740");
        this.onTick(title, formattedLines);
        assertPostedEventOfType(WitherHealthDecayEvent.class, 1);
        assertPostedEventOfType(WitherHealthDecayEvent.class, e -> {
            assertEquals(MWTeam.RED, e.team);
            assertEquals(740, e.health);
        });

        formattedLines.set(3, "§6[Y] Wither§6 H§6P§7: §6830");
        formattedLines.set(4, "§1[B] §fWither§1§1 HP§7: §1120");
        formattedLines.set(5, "§2[G] §fWither§2§2 HP§7: §2410");
        formattedLines.set(6, "§c[R] §fWither§c§c HP§7: §c600");
        this.onTick(title, formattedLines);
        assertPostedEventOfType(WitherHealthDecayEvent.class, 4);
    }

    @Test
    public void gameTimeEventTest() {
        final String title = "§6§lMEGA WALLS";
        final List<String> formattedLines = new ArrayList<>(Arrays.asList(
                "§708/31/26  §8M2§82C",
                "Gates Open: §a§a00:06",
                "",
                "§6[Y] Wither§6 H§6P§7: §61,000",
                "§1[B] §fWither§1§1 HP§7: §11,000",
                "§2[G] §fWither§2§2 HP§7: §21,000",
                "§c[R] §fWither§c§c HP§7: §c1,000",
                "",
                "§a0 §fKills §a0 Assists",
                "§a0 §fF. Kills §a0 §fF. Assists",
                "§60 §fCoins",
                "§60 §fClass Points",
                "",
                "§ewww.hypixel.ne§et"
        ));
        this.onTick(title, formattedLines);

        formattedLines.set(1, "Gates Open: §a§a00:05");
        this.onTick(title, formattedLines);
        assertPostedEventOfType(MegaWallsGameTimeEvent.class, e -> {
            assertEquals(5, e.time);
        });

        formattedLines.set(1, "Walls Fall: §a§a1:01");
        this.onTick(title, formattedLines);
        formattedLines.set(1, "Walls Fall: §a§a1:00");
        this.onTick(title, formattedLines);
        assertPostedEventOfType(MegaWallsGameTimeEvent.class, e -> {
            assertEquals(10 + 5 * 60 + 30, e.time);
        });

        formattedLines.set(1, "Enrage Off: §a§a1:01");
        this.onTick(title, formattedLines);
        formattedLines.set(1, "Enrage Off: §a§a1:00");
        this.onTick(title, formattedLines);
        assertPostedEventOfType(MegaWallsGameTimeEvent.class, e -> {
            assertEquals(10 + 6 * 60 + 30 + 5 * 60, e.time);
        });

        formattedLines.set(1, "Game End: §a§a1:01");
        this.onTick(title, formattedLines);
        formattedLines.set(1, "Game End: §a§a1:00");
        this.onTick(title, formattedLines);
        assertPostedEventOfType(MegaWallsGameTimeEvent.class, e -> {
            assertEquals(49 * 60, e.time);
        });
    }

}
