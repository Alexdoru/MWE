package fr.alexdoru.megawallsenhancementsmod.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.*;

public class AliasData {

    private static final Map<String, String> aliasMap = new LinkedHashMap<>();
    private static File aliasDataFile;

    public static void init() {
        aliasDataFile = new File(Minecraft.getMinecraft().mcDataDir, "config/aliasData.json");
        readDataFromFile();
        Runtime.getRuntime().addShutdownHook(new Thread(AliasData::writeDataToFile));
    }

    public static List<String> getAllNames() {
        return new ArrayList<>(aliasMap.keySet());
    }

    public static Map<String, String> getAliasMap() {
        return aliasMap;
    }

    public static String getAlias(String key) {
        return aliasMap.get(key);
    }

    public static void putAlias(String key, String alias) {
        aliasMap.put(key, alias);
    }

    public static void removeAlias(String key) {
        aliasMap.remove(key);
    }

    private static void readDataFromFile() {
        if (!aliasDataFile.exists()) {
            return;
        }
        try {
            final Gson gson = new Gson();
            final HashMap<String, String> hashMap = gson.fromJson(new FileReader(aliasDataFile), new TypeToken<HashMap<String, String>>() {}.getType());
            if (hashMap != null) {
                aliasMap.putAll(hashMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeDataToFile() {
        try {
            final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            final String jsonString = gson.toJson(aliasMap);
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(aliasDataFile));
            bufferedWriter.write(jsonString);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}