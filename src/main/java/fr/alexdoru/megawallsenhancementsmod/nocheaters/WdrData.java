package fr.alexdoru.megawallsenhancementsmod.nocheaters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.MegaWallsGameEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class WdrData {

    private static final long TIME_TRANSFORM_NICKED_REPORT = 86400000L; // 24hours
    private static final File legacywdrFile = new File(Minecraft.getMinecraft().mcDataDir, "config/wdred.txt");
    private static final File wdrJsonFile = new File(Minecraft.getMinecraft().mcDataDir, "config/WDRList.json");
    // used for backwards compat
    private static final String IGNORED = "ignored";

    private static final Map<String, WDR> wdrMap = new HashMap<>();
    private static boolean dirty;

    public WdrData() {
        WdrData.loadReportedPlayers();
        Runtime.getRuntime().addShutdownHook(new Thread(WdrData::saveReportedPlayers));
    }

    @SubscribeEvent
    public void onMWGameEvent(MegaWallsGameEvent event) {
        if (event.getType() == MegaWallsGameEvent.EventType.GAME_START || event.getType() == MegaWallsGameEvent.EventType.GAME_END) {
            if (dirty) {
                WdrData.saveReportedPlayers();
            }
        }
    }

    public static void markDirty() {
        dirty = true;
    }

    public static Map<String, WDR> getWdredMap() {
        return wdrMap;
    }

    public static WDR getWdr(String uuid) {
        return wdrMap.get(uuid);
    }

    public static WDR getWdr(String uuid, String playername) {
        WDR wdr = wdrMap.get(uuid);
        if (wdr != null) {
            return wdr;
        }
        wdr = wdrMap.get(playername);
        return wdr;
    }

    public static WDR getWdr(UUID uuid, String playername) {
        if (uuid.version() == 4) {
            return wdrMap.get(uuid.toString().replace("-", ""));
        }
        return wdrMap.get(playername);
    }

    public static void put(String uuid, WDR wdr) {
        wdrMap.put(uuid, wdr);
    }

    public static void remove(String uuid) {
        wdrMap.remove(uuid);
    }

    public static void saveReportedPlayers() {
        final ArrayList<String> reportLines = new ArrayList<>(wdrMap.size());
        for (final Entry<String, WDR> entry : wdrMap.entrySet()) {
            final String uuid = entry.getKey();
            final WDR wdr = entry.getValue();
            reportLines.add(uuid + " " + wdr.time + wdr.hacksToString());
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
            legacywdrFile.deleteOnExit();
        }
    }

    private static List<String> loadReportsFromJSONFile() {
        final List<String> reportLines = new ArrayList<>();
        if (!wdrJsonFile.exists()) {
            return reportLines;
        }
        try {
            final Gson gson = new Gson();
            return gson.fromJson(new FileReader(wdrJsonFile), new TypeToken<ArrayList<String>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return reportLines;
        }
    }

    private static List<String> loadReportsFromLegacyFile() {
        final List<String> reportLines = new ArrayList<>();
        if (!legacywdrFile.exists()) {
            return reportLines;
        }
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
        final String uuid = split[0];
        long timestamp = 0L;
        try {
            timestamp = Long.parseLong(split[1]);
            try {
                Long.parseLong(split[2]);
            } catch (Exception e) {
                oldDataFormat = true;
            }
        } catch (Exception ignored) {}
        final long datenow = new Date().getTime();
        if (ConfigHandler.deleteOldReports && datenow > timestamp + ConfigHandler.timeDeleteReport * 24f * 3600f * 1000f) {
            return;
        }
        final ArrayList<String> hacks = new ArrayList<>(Arrays.asList(split).subList(oldDataFormat ? 2 : 3, split.length));
        if (hacks.contains(WDR.NICK) && (datenow > timestamp + TIME_TRANSFORM_NICKED_REPORT)) {
            return;
        }
        // remove reports for players that are only ignored
        if (hacks.remove(IGNORED) && hacks.isEmpty()) {
            return;
        }
        wdrMap.put(uuid, new WDR(timestamp, hacks));
    }

}
