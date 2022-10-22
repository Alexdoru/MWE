package fr.alexdoru.megawallsenhancementsmod.data;

import fr.alexdoru.megawallsenhancementsmod.events.MegaWallsGameEvent;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.GameInfoGrabber;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScangameData {

    private static final ScangameData instance = new ScangameData();
    private static final ConcurrentHashMap<UUID, ScanResult> scangameMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, String> skipScanMap = new ConcurrentHashMap<>();
    private static String scanGameId;

    static {
        MinecraftForge.EVENT_BUS.register(instance);
    }

    @SubscribeEvent
    public void onMwGame(MegaWallsGameEvent event) {
        if (event.getType() == MegaWallsGameEvent.EventType.GAME_START) {
            onGameStart();
        }
        if (event.getType() == MegaWallsGameEvent.EventType.GAME_END) {
            clearScanGameData();
        }
    }

    private static void clearScanGameData() {
        scanGameId = null;
        scangameMap.clear();
    }

    private static void onGameStart() {
        final String currentGameId = GameInfoGrabber.getGameIdFromScoreboard();
        if (!currentGameId.equals("?") && scanGameId != null && !scanGameId.equals(currentGameId)) {
            clearScanGameData();
        }
    }

    public static boolean doesPlayerFlag(UUID uuid) {
        final ScanResult scanResult = scangameMap.get(uuid);
        return scanResult != null && scanResult.msg != null;
    }

    public static void put(UUID uuid, IChatComponent msg) {
        scangameMap.put(uuid, new ScanResult(msg));
    }

    public static ScanResult get(UUID uuid) {
        return scangameMap.get(uuid);
    }

    public static void setScanGameId(String scanGameIdIn) {
        scanGameId = scanGameIdIn;
    }

    public static String getScanGameId() {
        return scanGameId;
    }

    public static void addToSkipSet(UUID uuid) {
        skipScanMap.put(uuid, "");
    }

    public static boolean skipScan(UUID uuid) {
        return skipScanMap.get(uuid) != null;
    }

    public static class ScanResult {

        public final IChatComponent msg;

        public ScanResult(IChatComponent msg) {
            this.msg = msg;
        }

    }

}
