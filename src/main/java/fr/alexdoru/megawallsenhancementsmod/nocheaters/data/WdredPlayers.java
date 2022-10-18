package fr.alexdoru.megawallsenhancementsmod.nocheaters.data;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class WdredPlayers {

    public static final Logger logger = LogManager.getLogger("NoCheaters WDR Data");
    private static final long TIME_TRANSFORM_NICKED_REPORT = 86400000L; // 24hours
    private static final long TIME_TRANSFORM_TIMESTAMPED_REPORT = 14L * 24L * 60L * 60L * 1000L; //14 days
    private static final HashMap<String, WDR> WDR_HASH_MAP = new HashMap<>();
    /**
     * In the wdred file the data is saved with the following pattern
     * uuid timestamp timeLastManualReport hack1 hack2 hack3 hack4 hack5
     */
    private static File wdrsFile;

    public static void init() {
        wdrsFile = new File(Minecraft.getMinecraft().mcDataDir, "config/wdred.txt");
        loadReportedPlayers();
        Runtime.getRuntime().addShutdownHook(new Thread(WdredPlayers::saveReportedPlayers));
    }

    public static HashMap<String, WDR> getWdredMap() {
        return WDR_HASH_MAP;
    }

    public static WDR getPlayer(String uuid, String playername) {
        WDR wdr = WDR_HASH_MAP.get(uuid);
        if (wdr != null) {
            return wdr;
        }
        wdr = WDR_HASH_MAP.get(playername);
        return wdr;
    }

    public static void saveReportedPlayers() {
        try {
            final BufferedWriter writer = new BufferedWriter(new FileWriter(wdrsFile));
            for (final Entry<String, WDR> entry : WDR_HASH_MAP.entrySet()) {
                final String uuid = entry.getKey();
                final WDR wdr = entry.getValue();
                writer.write(uuid + " " + wdr.timestamp + " " + wdr.timeLastManualReport + wdr.hacksToString() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            logger.error("Failed to write data to the wdr file");
        }
    }

    public static void loadReportedPlayers() {
        if (!wdrsFile.exists()) {
            logger.info("Couldn't find existing wdr file");
            return;
        }
        try {

            final long datenow = (new Date()).getTime();
            final BufferedReader reader = new BufferedReader(new FileReader(wdrsFile));

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                final String[] split = line.split(" ");
                if (split.length >= 3) {

                    boolean oldDataFormat = false;
                    final String uuid = split[0];
                    long timestamp = 0L;
                    long timeManualReport = 0L;

                    try {
                        timestamp = Long.parseLong(split[1]);
                        try {
                            timeManualReport = Long.parseLong(split[2]);
                        } catch (Exception e) {
                            timeManualReport = timestamp;
                            oldDataFormat = true;
                        }
                    } catch (Exception e) {
                        logger.error("Failed to parse timestamp for : " + uuid);
                    }

                    if (ConfigHandler.deleteOldReports && datenow > timestamp + ConfigHandler.timeDeleteReport) {
                        final ArrayList<String> hacks = transformOldReports(Arrays.copyOfRange(split, oldDataFormat ? 2 : 3, split.length));
                        if (hacks.isEmpty()) {
                            continue;
                        }
                    }

                    final ArrayList<String> hacks = filterTimestampedReports(
                            Arrays.copyOfRange(split, oldDataFormat ? 2 : 3, split.length),
                            datenow
                    );

                    if (hacks.contains(WDR.NICK) && (datenow > timestamp + TIME_TRANSFORM_NICKED_REPORT)) {
                        continue;
                    }

                    WDR_HASH_MAP.put(uuid, new WDR(timestamp, timeManualReport, hacks));

                }
            }

            reader.close();

        } catch (Exception e) {
            logger.error("Failed to read the wdr file");
        }

    }

    /**
     * Deletes old reports exept if they are ignored players
     */
    private static ArrayList<String> transformOldReports(String[] split) {
        final ArrayList<String> hacks = new ArrayList<>();
        final List<String> splitList = Arrays.asList(split);
        if (splitList.contains(WDR.IGNORED) && !splitList.contains(WDR.NICK)) {
            hacks.add(WDR.IGNORED);
        }
        return hacks;
    }

    /**
     * Transforms the timestamped reports older into normal reports
     */
    private static ArrayList<String> filterTimestampedReports(String[] split, long datenow) {
        final ArrayList<String> hacks = new ArrayList<>();
        if (split[0].charAt(0) == '-' && datenow - Long.parseLong(split[3]) - TIME_TRANSFORM_TIMESTAMPED_REPORT > 0) {
            int j = 0; // indice of timestamp
            for (int i = 0; i < split.length; i++) {
                if (split[i].charAt(0) == '-') { // serverID
                    j = i;
                } else if (i > j + 3) { // cheats
                    hacks.add(split[i]);
                }
            }
            return removeDuplicates(hacks);
        } else {
            hacks.addAll(Arrays.asList(split));
            return hacks;
        }
    }

    private static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        final ArrayList<T> newList = new ArrayList<>();
        for (final T element : list) {
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

}
