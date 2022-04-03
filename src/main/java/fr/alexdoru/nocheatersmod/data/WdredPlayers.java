package fr.alexdoru.nocheatersmod.data;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.nocheatersmod.NoCheatersMod;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class WdredPlayers {

    private static final long TIME_TRANSFORM_NICKED_REPORT = 86400000L; // 24hours
    private static final long TIME_TRANSFORM_TIMESTAMPED_REPORT = 14L * 24L * 60L * 60L * 1000L; //14 days
    private static final HashMap<String, WDR> wdred = new HashMap<>();
    /**
     * In the wdred file the data is saved with the following pattern
     * uuid timestamp timeLastManualReport hack1 hack2 hack3 hack4 hack5
     */
    public static File wdrsFile;

    public static HashMap<String, WDR> getWdredMap() {
        return wdred;
    }

    public static void saveReportedPlayers() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(wdrsFile));
            for (Entry<String, WDR> entry : wdred.entrySet()) {

                String uuid = entry.getKey();
                WDR wdr = entry.getValue();
                writer.write(uuid + " " + wdr.timestamp + " " + wdr.timeLastManualReport);
                for (String hack : wdr.hacks) {
                    writer.write(" " + hack);
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            NoCheatersMod.logger.error("Failed to write date to the wdr file");
        }
    }

    public static void loadReportedPlayers() {
        if (!wdrsFile.exists()) {
            NoCheatersMod.logger.info("Couldn't find existing wdr file");
            return;
        }
        try {

            long datenow = (new Date()).getTime();
            BufferedReader reader = new BufferedReader(new FileReader(wdrsFile));

            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] split = line.split(" ");
                if (split.length >= 3) {

                    boolean oldDataFormat = false;
                    String uuid = split[0];
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
                        NoCheatersMod.logger.error("Failed to parse timestamp for : " + uuid);
                    }

                    if (ConfigHandler.deleteReports && datenow > timestamp + ConfigHandler.timeDeleteReport) {
                        ArrayList<String> hacks = transformOldReports(Arrays.copyOfRange(split, oldDataFormat ? 2 : 3, split.length));
                        if (hacks.isEmpty()) {
                            continue;
                        }
                    }

                    ArrayList<String> hacks = filterTimestampedReports(
                            Arrays.copyOfRange(split, oldDataFormat ? 2 : 3, split.length),
                            datenow
                    );

                    if (hacks.contains(WDR.NICK) && (datenow > timestamp + TIME_TRANSFORM_NICKED_REPORT)) {
                        continue;
                    }

                    wdred.put(uuid, new WDR(timestamp, timeManualReport, hacks));

                }
            }

            reader.close();

        } catch (Exception e) {
            NoCheatersMod.logger.error("Failed to read the wdr file");
        }

    }

    /**
     * Deletes old reports exept if they are ignored players
     */
    private static ArrayList<String> transformOldReports(String[] split) {
        ArrayList<String> hacks = new ArrayList<>();
        List<String> splitList = Arrays.asList(split);
        if (splitList.contains(WDR.IGNORED)) {
            hacks.add(WDR.IGNORED);
        }
        return hacks;
    }

    /**
     * Transforms the timestamped reports older into normal reports
     */
    private static ArrayList<String> filterTimestampedReports(String[] split, long datenow) {

        ArrayList<String> hacks = new ArrayList<>();

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

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {

        ArrayList<T> newList = new ArrayList<>();

        for (T element : list) {
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }

        return newList;

    }

}
