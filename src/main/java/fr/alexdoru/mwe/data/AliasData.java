package fr.alexdoru.mwe.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.alexdoru.mwe.api.events.AliasEvent;
import fr.alexdoru.mwe.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

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

    public static List<Map.Entry<String, String>> getEntries() {
        return new ArrayList<>(aliasMap.entrySet());
    }

    @Nullable
    public static String getAlias(@Nullable UUID id, @Nullable String playername) {
        if (id != null && NameUtil.isRealPlayer(id)) {
            return aliasMap.get(id.toString().replace("-", ""));
        } else if (playername != null) {
            return aliasMap.get(playername);
        }
        return null;
    }

    public static void putAlias(@Nullable UUID id, @Nullable String playername, String alias) {
        String prevAlias = null;
        boolean added = false;
        if (id != null && NameUtil.isRealPlayer(id)) {
            prevAlias = aliasMap.put(id.toString().replace("-", ""), alias);
            added = true;
        } else if (playername != null) {
            prevAlias = aliasMap.put(playername, alias);
            added = true;
        }
        if (added) {
            NameUtil.updateMWPlayerDataAndEntityData(playername, false);
            if (prevAlias == null) {
                MinecraftForge.EVENT_BUS.post(new AliasEvent(AliasEvent.Type.ADDED, id, playername));
            } else if (!prevAlias.equals(alias)) {
                MinecraftForge.EVENT_BUS.post(new AliasEvent(AliasEvent.Type.ALIAS_CHANGED, id, playername));
            }
        }
    }

    /**
     * Removes a player from the alias list, returns true if the player was succesfully removed
     */
    public static boolean removeAlias(@Nullable UUID id, @Nullable String playername) {
        String removed = null;
        if (id != null && NameUtil.isRealPlayer(id)) {
            removed = aliasMap.remove(id.toString().replace("-", ""));
        } else if (playername != null) {
            removed = aliasMap.remove(playername);
        }
        if (removed != null) {
            NameUtil.updateMWPlayerDataAndEntityData(playername, false);
            MinecraftForge.EVENT_BUS.post(new AliasEvent(AliasEvent.Type.REMOVED, id, playername));
        }
        return removed != null;
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
        try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(aliasDataFile))) {
            final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            final String jsonString = gson.toJson(aliasMap);
            bufferedWriter.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}