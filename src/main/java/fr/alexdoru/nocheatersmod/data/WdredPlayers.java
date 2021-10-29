package fr.alexdoru.nocheatersmod.data;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

public class WdredPlayers {

    private static final HashMap<String, WDR> wdred = new HashMap<>();
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
                writer.write(uuid + " " + wdr.timestamp);
                for (String hack : wdr.hacks) {
                    writer.write(" " + hack);
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadReportedPlayers() {
        if (!wdrsFile.exists())
            return;
        try {

            long datenow = (new Date()).getTime();

            BufferedReader reader = new BufferedReader(new FileReader(wdrsFile));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] split = line.split(" ");
                if (split.length >= 3) {

                    long timestamp = 0L;
                    try {
                        timestamp = Long.parseLong(split[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ArrayList<String> hacks = transformOldReports(split, datenow);

                    if (hacks.contains("nick") && (datenow > timestamp + 172800000L)) { // 48hours
                        continue;
                    }

                    wdred.put(split[0], new WDR(timestamp, hacks));
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Transforms the timestamped reports older than a month into normal reports
     */
    private static ArrayList<String> transformOldReports(String[] split, long datenow) {

        ArrayList<String> hacks = new ArrayList<>();

        if (split[2].charAt(0) == '-' && datenow > Long.parseLong(split[5]) + 2592000000L) {

            int j = 0; // indice of timestamp
            for (int i = 2; i < split.length; i++) {

                if (split[i].charAt(0) == '-') { // serverID
                    j = i;
                } else if (i > j + 3) { // cheats
                    hacks.add(split[i]);
                }

            }

            return removeDuplicates(hacks);

        } else {

            hacks.addAll(Arrays.asList(split).subList(2, split.length));
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
