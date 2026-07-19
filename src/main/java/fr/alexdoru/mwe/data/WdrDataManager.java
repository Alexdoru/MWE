package fr.alexdoru.mwe.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.api.events.ReportListEvent;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.nocheaters.WDR;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import fr.alexdoru.mwe.utils.UUIDUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class WdrDataManager {

    private WdrDataManager() {}

    private static final Map<UUID, WDR> uuidMap = new HashMap<>();
    private static final Map<String, WDR> nickMap = new HashMap<>();
    private static File wdrDataFile;
    private static final AtomicBoolean dirty = new AtomicBoolean(false);
    private static boolean initialized;

    public static void loadData(File configFolder) {
        if (initialized) {
            throw new IllegalStateException("Already initialized");
        }
        initialized = true;
        wdrDataFile = new File(configFolder, "ReportList.json");
        MultithreadingUtil.queueIOTask(() -> {
            final Map<Object, WDR> map = loadDataFromFiles();
            final Map<UUID, WDR> uuidReports = new HashMap<>();
            final Map<String, WDR> nameReports = new HashMap<>();
            map.forEach((key, value) -> {
                if (key instanceof String) {
                    nameReports.put((String) key, value);
                } else if (key instanceof UUID) {
                    uuidReports.put((UUID) key, value);
                }
            });
            Minecraft.getMinecraft().addScheduledTask(() -> {
                uuidMap.putAll(uuidReports);
                nickMap.putAll(nameReports);
            });
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (dirty.get()) {
                writeDataToFile(getAllReports());
            }
        }));
    }

    static void saveIfDirty() {
        if (dirty.get()) {
            dirty.set(false);
            final Map<Object, WDR> snapshot = getAllReports();
            MultithreadingUtil.queueIOTask(() -> {
                if (!writeDataToFile(snapshot)) {
                    dirty.set(true);
                }
            });
        }
    }

    public static Map<Object, WDR> getAllReports() {
        final Map<Object, WDR> mergedMap = new HashMap<>(uuidMap.size() + nickMap.size());
        mergedMap.putAll(uuidMap);
        mergedMap.putAll(nickMap);
        return mergedMap;
    }

    public static WDR getWdr(UUID uuid, String playername) {
        if (PlayerDataManager.isRealPlayer(uuid)) {
            return uuidMap.get(uuid);
        }
        return nickMap.get(playername);
    }

    /**
     * Adds or update a report for a player, returns true if it added a new report
     */
    public static boolean addReport(UUID uuid, String playername, String cheat) {
        WDR wdr = getWdr(uuid, playername);
        boolean added = false;
        final boolean refreshName;
        if (wdr == null) {
            wdr = new WDR(cheat);
            added = put(uuid, playername, wdr);
            refreshName = true;
        } else {
            refreshName = wdr.addCheat(cheat);
        }
        if (refreshName) {
            if (PlayerDataManager.isRealPlayer(uuid)) {
                PlayerDataManager.updatePlayerDataAndEntityData(uuid);
            } else {
                PlayerDataManager.updatePlayerDataAndEntityData(playername);
            }
        }
        dirty.set(true);
        if (added) {
            MinecraftForge.EVENT_BUS.post(new ReportListEvent(ReportListEvent.Type.ADDED, uuid, playername, wdr));
        }
        return added;
    }

    /**
     * Adds or update a report for a player, returns true if it added a new report
     */
    public static boolean addReport(UUID uuid, String playername, List<String> cheats) {
        WDR wdr = getWdr(uuid, playername);
        boolean added = false;
        if (wdr == null) {
            wdr = new WDR(cheats);
            added = put(uuid, playername, wdr);
        } else {
            wdr.addCheats(cheats);
        }
        if (PlayerDataManager.isRealPlayer(uuid)) {
            PlayerDataManager.updatePlayerDataAndEntityData(uuid);
        } else {
            PlayerDataManager.updatePlayerDataAndEntityData(playername);
        }
        dirty.set(true);
        if (added) {
            MinecraftForge.EVENT_BUS.post(new ReportListEvent(ReportListEvent.Type.ADDED, uuid, playername, wdr));
        }
        return added;
    }

    private static boolean put(UUID uuid, String playername, WDR wdr) {
        if (PlayerDataManager.isRealPlayer(uuid)) {
            uuidMap.put(uuid, wdr);
            return true;
        }
        if (playername != null) {
            nickMap.put(playername, wdr);
            return true;
        }
        return false;
    }

    /**
     * Removes a player from the reportlist, returns true if the player was succesfully removed
     */
    public static boolean remove(UUID uuid, String playername) {
        WDR removed = null;
        if (uuid != null) {
            removed = uuidMap.remove(uuid);
            PlayerDataManager.updatePlayerDataAndEntityData(uuid);
        } else if (playername != null) {
            removed = nickMap.remove(playername);
            PlayerDataManager.updatePlayerDataAndEntityData(playername);
        }
        if (removed != null) {
            MinecraftForge.EVENT_BUS.post(new ReportListEvent(ReportListEvent.Type.REMOVED, uuid, playername, removed));
            dirty.set(true);
        }
        return removed != null;
    }

    private static boolean writeDataToFile(Map<Object, WDR> map) {
        final List<String> reportLines = new ArrayList<>(map.size());
        map.forEach((key, value) -> {
            if (key instanceof String || key instanceof UUID) {
                reportLines.add(key + " " + value.getTimestamp() + value.cheatsToString());
            }
        });
        try {
            final File file = wdrDataFile;
            if (file.getParentFile() != null) {
                Files.createDirectories(file.getParentFile().toPath());
            }
            try (final BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
                final String jsonString = gson.toJson(reportLines);
                bw.write(jsonString);
                return true;
            }
        } catch (IOException e) {
            MWE.logger.error(e);
        }
        return false;
    }

    @NotNull
    private static Map<Object, WDR> loadDataFromFiles() {
        final Map<Object, WDR> map = new HashMap<>();
        if (wdrDataFile.exists()) {
            loadDataFromFile(wdrDataFile, map);
            return map;
        }
        final File legacyFile = new File(Minecraft.getMinecraft().mcDataDir, "config/WDRList.json");
        if (legacyFile.exists()) {
            loadDataFromFile(legacyFile, map);
            if (!map.isEmpty()) {
                if (writeDataToFile(map)) {
                    if (legacyFile.delete()) {
                        MWE.logger.info("Deleted report data file: {}", legacyFile);
                    } else {
                        MWE.logger.error("Failed to delete legacy report data file: {}", legacyFile);
                    }
                }
            }
            return map;
        }
        return map;
    }

    private static void loadDataFromFile(File file, Map<Object, WDR> map) {
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                final List<String> list = new Gson().fromJson(reader, new TypeToken<List<String>>() {}.getType());
                if (list != null) {
                    for (final String line : list) {
                        loadReportLine(line, map);
                    }
                }
            } catch (Exception e) {
                MWE.logger.error(e);
            }
        }
    }

    private static void loadReportLine(String reportLine, Map<Object, WDR> map) {
        //In the wdr file the data is saved with the following pattern :
        //uuid timestamp hack1 hack2 hack3 hack4 hack5
        final String[] split = reportLine.split(" ");
        if (split.length < 3) return;
        long timestamp = 0L;
        try {
            timestamp = Long.parseLong(split[1]);
        } catch (Exception ignored) {}
        if (timestamp == 0L) return;
        final long datenow = new Date().getTime();
        if (MWEConfig.deleteOldReports && datenow > timestamp + MWEConfig.timeDeleteReport * 24f * 3600f * 1000f) {
            return;
        }
        final ArrayList<String> hacks = new ArrayList<>(Arrays.asList(split).subList(2, split.length));
        // remove reports for players that are only ignored
        // used for backwards compat
        final String IGNORED = "ignored";
        hacks.remove(IGNORED);
        if (hacks.isEmpty()) {
            return;
        }
        final String mapKey = split[0];
        if (mapKey.isEmpty()) return;
        // mapkey could be :
        // - a playername for nicked players
        // - a uuid without ----
        // - a uuid with ----
        final UUID uuid = UUIDUtil.fromString(mapKey);
        final int VALID_NAME_MAX_LENGTH = 16;
        if (uuid == null && mapKey.length() <= VALID_NAME_MAX_LENGTH) {
            final long TIME_TRANSFORM_NICKED_REPORT = 86400000L; // 24 hours
            if (datenow > timestamp + TIME_TRANSFORM_NICKED_REPORT) {
                return;
            }
            map.put(mapKey, new WDR(hacks, timestamp));
        } else if (uuid != null) {
            map.put(uuid, new WDR(hacks, timestamp));
        }
    }

}
