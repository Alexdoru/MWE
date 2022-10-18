package fr.alexdoru.megawallsenhancementsmod.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.HashMap;

public class AliasData {

    private static final HashMap<String, String> aliasMap = new HashMap<>();
    private static File alliasDataFile;

    public static void init() {
        alliasDataFile = new File(Minecraft.getMinecraft().mcDataDir, "config/alliasData.json");
        readDataFromFile();
        Runtime.getRuntime().addShutdownHook(new Thread(AliasData::writeDataToFile));
    }

    public static HashMap<String, String> getMap() {
        return aliasMap;
    }

    public static String getAlias(String playername) {
        return aliasMap.get(playername);
    }

    public static void putAlias(String playername, String alias) {
        aliasMap.put(playername, alias);
    }

    public static String removeAlias(String playername) {
        return aliasMap.remove(playername);
    }

    private static void readDataFromFile() {
        if (!alliasDataFile.exists()) {
            return;
        }
        try {
            final Gson gson = new Gson();
            final HashMap<String, String> hashMap = gson.fromJson(new FileReader(alliasDataFile), new TypeToken<HashMap<String, String>>() {
            }.getType());
            if (hashMap != null) {
                aliasMap.putAll(hashMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeDataToFile() {
        try {
            final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            final String jsonString = gson.toJson(aliasMap);
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(alliasDataFile));
            bufferedWriter.write(jsonString);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
