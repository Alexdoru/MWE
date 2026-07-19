package fr.alexdoru.mwe.nocheaters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.alexdoru.mwe.api.events.MegaWallsGameEvent;
import fr.alexdoru.mwe.api.events.ReportListEvent;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.data.PlayerDataManager;
import fr.alexdoru.mwe.utils.UUIDUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class WdrData {

    private static final File legacywdrFile = new File(Minecraft.getMinecraft().mcDataDir, "config/wdred.txt");
    private static final File wdrJsonFile = new File(Minecraft.getMinecraft().mcDataDir, "config/WDRList.json");
    private static final Map<UUID, WDR> uuidMap = new HashMap<>();
    private static final Map<String, WDR> nickMap = new HashMap<>();
    private static boolean dirty;

    public WdrData() {
        WdrData.loadReportedPlayers();
        Runtime.getRuntime().addShutdownHook(new Thread(WdrData::saveReportedPlayers));
    }

    @SubscribeEvent
    public void onMWGameEvent(MegaWallsGameEvent event) {
        if (event.type == MegaWallsGameEvent.Type.GAME_START || event.type == MegaWallsGameEvent.Type.GAME_END) {
            if (dirty) {
                WdrData.saveReportedPlayers();
            }
        }
    }

    public static Map<Object, WDR> getAllWDRs() {
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
        WDR wdr = WdrData.getWdr(uuid, playername);
        boolean added = false;
        final boolean refreshName;
        if (wdr == null) {
            wdr = new WDR(cheat);
            added = WdrData.put(uuid, playername, wdr);
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
        dirty = true;
        if (added) {
            MinecraftForge.EVENT_BUS.post(new ReportListEvent(ReportListEvent.Type.ADDED, uuid, playername, wdr));
        }
        return added;
    }

    /**
     * Adds or update a report for a player, returns true if it added a new report
     */
    public static boolean addReport(UUID uuid, String playername, List<String> cheats) {
        WDR wdr = WdrData.getWdr(uuid, playername);
        boolean added = false;
        if (wdr == null) {
            wdr = new WDR(cheats);
            added = WdrData.put(uuid, playername, wdr);
        } else {
            wdr.addCheats(cheats);
        }
        if (PlayerDataManager.isRealPlayer(uuid)) {
            PlayerDataManager.updatePlayerDataAndEntityData(uuid);
        } else {
            PlayerDataManager.updatePlayerDataAndEntityData(playername);
        }
        dirty = true;
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
            dirty = true;
        }
        return removed != null;
    }

    public static void saveReportedPlayers() {
        final ArrayList<String> reportLines = new ArrayList<>(uuidMap.size() + nickMap.size());
        for (final Entry<UUID, WDR> entry : uuidMap.entrySet()) {
            final String uuid = entry.getKey().toString();
            final WDR wdr = entry.getValue();
            reportLines.add(uuid + " " + wdr.getTimestamp() + wdr.cheatsToString());
        }
        for (final Entry<String, WDR> entry : nickMap.entrySet()) {
            final String playername = entry.getKey();
            final WDR wdr = entry.getValue();
            reportLines.add(playername + " " + wdr.getTimestamp() + wdr.cheatsToString());
        }
        try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(wdrJsonFile))) {
            final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            final String jsonString = gson.toJson(reportLines);
            bufferedWriter.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dirty = false;
    }

    private static void loadReportedPlayers() {
        final List<String> jsonReportLines = loadReportsFromJSONFile();
        final List<String> legacyReportLines = loadReportsFromLegacyFile();
        final boolean deleteLegacyFile = !legacyReportLines.isEmpty();
        jsonReportLines.forEach(WdrData::loadReportLine);
        legacyReportLines.forEach(WdrData::loadReportLine);
        if (deleteLegacyFile) {
            WdrData.saveReportedPlayers();
            //noinspection ResultOfMethodCallIgnored
            legacywdrFile.delete();
        }
    }

    private static List<String> loadReportsFromJSONFile() {
        if (!wdrJsonFile.exists()) {
            return Collections.emptyList();
        }
        final List<String> reportLines = new ArrayList<>();
        try {
            final Gson gson = new Gson();
            final List<String> list = gson.fromJson(new FileReader(wdrJsonFile), new TypeToken<List<String>>() {}.getType());
            if (list != null) reportLines.addAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reportLines;
    }

    private static List<String> loadReportsFromLegacyFile() {
        if (!legacywdrFile.exists()) {
            return Collections.emptyList();
        }
        final List<String> reportLines = new ArrayList<>();
        try (final BufferedReader reader = new BufferedReader(new FileReader(legacywdrFile))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                reportLines.add(line);
            }
        } catch (Exception ignored) {}
        return reportLines;
    }

    private static void loadReportLine(String reportLine) {
        //In the wdr file the data is saved with the following pattern :
        //uuid timestamp hack1 hack2 hack3 hack4 hack5
        final String[] split = reportLine.split(" ");
        if (split.length < 3) return;
        boolean oldDataFormat = false;
        long timestamp = 0L;
        try {
            timestamp = Long.parseLong(split[1]);
            try {
                Long.parseLong(split[2]);
            } catch (Exception e) {
                oldDataFormat = true;
            }
        } catch (Exception ignored) {}
        if (timestamp == 0L) return;
        final long datenow = new Date().getTime();
        if (MWEConfig.deleteOldReports && datenow > timestamp + MWEConfig.timeDeleteReport * 24f * 3600f * 1000f) {
            return;
        }
        final ArrayList<String> hacks = new ArrayList<>(Arrays.asList(split).subList(oldDataFormat ? 2 : 3, split.length));
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
        if (uuid == null && mapKey.length() < 17) {
            final long TIME_TRANSFORM_NICKED_REPORT = 86400000L; // 24 hours
            if (datenow > timestamp + TIME_TRANSFORM_NICKED_REPORT) {
                return;
            }
            nickMap.put(mapKey, new WDR(hacks, timestamp));
        } else if (uuid != null) {
            uuidMap.put(uuid, new WDR(hacks, timestamp));
        }
    }

}
