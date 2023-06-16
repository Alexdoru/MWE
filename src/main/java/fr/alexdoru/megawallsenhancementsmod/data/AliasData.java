package fr.alexdoru.megawallsenhancementsmod.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AliasData {

    private static final HashMap<String, String> aliasMap = new HashMap<>();
    private static File aliasDataFile;

    public static void init() {
        aliasDataFile = new File(Minecraft.getMinecraft().mcDataDir, "config/aliasData.json");
        readDataFromFile();
        Runtime.getRuntime().addShutdownHook(new Thread(AliasData::writeDataToFile));
    }

    public static List<String> getAllNames() {
        return new ArrayList<>(aliasMap.keySet());
    }

    public static String getAlias(String playername) {
        return aliasMap.get(playername);
    }

    public static void putAlias(String playername, String alias) {
        aliasMap.put(playername, alias);
    }

    public static void removeAlias(String playername) {
        aliasMap.remove(playername);
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