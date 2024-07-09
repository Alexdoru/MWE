package fr.alexdoru.mwe.data;

import fr.alexdoru.mwe.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.scoreboard.ScoreboardUtils;
import fr.alexdoru.mwe.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

public class ScangameData {

    private static final ScangameData instance = new ScangameData();
    private static final Map<UUID, ScanResult> scangameMap = new HashMap<>();
    private static final Set<UUID> skipScanSet = new HashSet<>();
    private static final Set<UUID> randomKitSet = new HashSet<>();
    private static String scanGameId;

    static {
        MinecraftForge.EVENT_BUS.register(instance);
    }

    @SubscribeEvent
    public void onMwGame(MegaWallsGameEvent event) {
        if (event.getType() == MegaWallsGameEvent.EventType.GAME_START) {
            final String currentGameId = ScoreboardUtils.getGameIdFromScoreboard();
            if (currentGameId != null && !currentGameId.equals(scanGameId)) {
                clearScanGameData();
            }
        }
        if (event.getType() == MegaWallsGameEvent.EventType.GAME_END) {
            clearScanGameData();
        }
    }

    public static void clearScanGameData() {
        scanGameId = null;
        scangameMap.clear();
    }

    public static void clearRandomKits() {
        randomKitSet.clear();
    }

    public static void fectchRandomClasses() {
        randomKitSet.clear();
        if (ScoreboardUtils.isMegaWallsMythicGame()) {
            for (final NetworkPlayerInfo netInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
                if (NameUtil.isPlayerUsingRandom(netInfo)) {
                    randomKitSet.add(netInfo.getGameProfile().getId());
                }
            }
        }
    }

    public static boolean didPlayerPickRandom(UUID uuid) {
        return randomKitSet.contains(uuid);
    }

    public static boolean doesPlayerFlag(UUID uuid) {
        final ScanResult scanResult = scangameMap.get(uuid);
        return scanResult != null && scanResult.msg != null;
    }

    public static void put(UUID uuid, IChatComponent msg) {
        scangameMap.put(uuid, new ScanResult(msg));
    }

    public static void put(UUID uuid, int networkLvl, int questsCompleted) {
        scangameMap.put(uuid, new ScanResult(networkLvl, questsCompleted));
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
        skipScanSet.add(uuid);
    }

    public static boolean skipScan(UUID uuid) {
        return skipScanSet.contains(uuid);
    }

    public static class ScanResult {

        public IChatComponent msg;
        public final int networkLvl;
        public final int questamount;

        public ScanResult(IChatComponent msg) {
            this.msg = msg;
            this.networkLvl = -1;
            this.questamount = -1;
        }

        public ScanResult(int networkLvl, int questamount) {
            this.msg = null;
            this.networkLvl = networkLvl;
            this.questamount = questamount;
        }

        public boolean isLowLevelAccount() {
            if (networkLvl == -1 && questamount == -1) {
                return false;
            }
            return networkLvl + questamount < 11;
        }

    }

}
