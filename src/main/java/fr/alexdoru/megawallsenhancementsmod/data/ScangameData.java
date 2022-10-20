package fr.alexdoru.megawallsenhancementsmod.data;

import fr.alexdoru.megawallsenhancementsmod.events.MegaWallsGameEvent;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.events.GameInfoGrabber;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class ScangameData {

    private static final ScangameData instance = new ScangameData();
    private static final HashMap<UUID, ScanResult> scangameMap = new HashMap<>();
    private static final HashSet<UUID> skipScanSetUUID = new HashSet<>();
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
        final String currentGameId = GameInfoGrabber.getGameIDfromscoreboard();
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
        skipScanSetUUID.add(uuid);
    }

    public static boolean skipScan(UUID uuid) {
        return skipScanSetUUID.contains(uuid);
    }

    public static class ScanResult {

        public final IChatComponent msg;

        public ScanResult(IChatComponent msg) {
            this.msg = msg;
        }

    }

}
