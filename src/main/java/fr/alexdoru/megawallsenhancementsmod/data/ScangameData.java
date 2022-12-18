package fr.alexdoru.megawallsenhancementsmod.data;

import fr.alexdoru.megawallsenhancementsmod.events.MegaWallsGameEvent;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardUtils;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScangameData {

    private static final ScangameData instance = new ScangameData();
    private static final ConcurrentHashMap<UUID, ScanResult> scangameMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<UUID, String> skipScanMap = new ConcurrentHashMap<>();
    private static final HashSet<UUID> playersUsingRandom = new HashSet<>();
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

    public static void clearScanGameData() {
        scanGameId = null;
        scangameMap.clear();
        playersUsingRandom.clear();
    }

    private static void onGameStart() {
        final String currentGameId = ScoreboardUtils.getGameIdFromScoreboard();
        if (!currentGameId.equals("?") && !currentGameId.equals(scanGameId)) {
            clearScanGameData();
        }
    }

    public static void fectchRandomClasses() {
        playersUsingRandom.clear();
        if (ScoreboardUtils.isMegaWallsMythicGame()) {
            for (final NetworkPlayerInfo networkPlayerInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
                if (NameUtil.isPlayerUsingRandom(networkPlayerInfo)) {
                    playersUsingRandom.add(networkPlayerInfo.getGameProfile().getId());
                }
            }
        }
    }

    public static boolean didPlayerPickRandom(UUID uuid) {
        return playersUsingRandom.contains(uuid);
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
