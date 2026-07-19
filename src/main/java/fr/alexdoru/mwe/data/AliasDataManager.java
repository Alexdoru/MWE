package fr.alexdoru.mwe.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.api.events.AliasEvent;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

public final class AliasDataManager {

    private AliasDataManager() {}

    private static final Map<String, String> aliasMap = new LinkedHashMap<>();
    private static File aliasDataFile;
    private static volatile boolean dirty;
    private static boolean initialized;

    public static void loadData(File configFolder) {
        if (initialized) {
            throw new IllegalStateException("Already initialized");
        }
        initialized = true;
        aliasDataFile = new File(configFolder, "aliasData.json");
        MultithreadingUtil.queueIOTask(AliasDataManager::loadDataFromFiles);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> AliasDataManager.writeDataToFile(aliasDataFile, aliasMap)));
    }

    static void saveIfDirty() {
        if (dirty) {
            final Map<String, String> snapshot = new HashMap<>(aliasMap);
            MultithreadingUtil.queueIOTask(() -> writeDataToFile(aliasDataFile, snapshot));
        }
    }

    public static List<Map.Entry<String, String>> getEntries() {
        return new ArrayList<>(aliasMap.entrySet());
    }

    @Nullable
    public static String getAlias(@Nullable UUID id, @Nullable String playername) {
        if (id != null && PlayerDataManager.isRealPlayer(id)) {
            return aliasMap.get(id.toString().replace("-", ""));
        } else if (playername != null) {
            return aliasMap.get(playername);
        }
        return null;
    }

    public static void putAlias(@Nullable UUID id, @Nullable String playername, String alias) {
        String prevAlias = null;
        boolean added = false;
        if (id != null && PlayerDataManager.isRealPlayer(id)) {
            prevAlias = aliasMap.put(id.toString().replace("-", ""), alias);
            PlayerDataManager.updatePlayerDataAndEntityData(id);
            added = true;
        } else if (playername != null) {
            prevAlias = aliasMap.put(playername, alias);
            PlayerDataManager.updatePlayerDataAndEntityData(playername);
            added = true;
        }
        if (added) {
            dirty = true;
            if (prevAlias == null) {
                MinecraftForge.EVENT_BUS.post(new AliasEvent(AliasEvent.Type.ADDED, id, playername));
            } else if (!prevAlias.equals(alias)) {
                MinecraftForge.EVENT_BUS.post(new AliasEvent(AliasEvent.Type.ALIAS_CHANGED, id, playername));
            }
        }
    }

    /**
     * Removes a player from the alias list, returns true if the player was successfully removed
     */
    public static boolean removeAlias(@Nullable UUID id, @Nullable String playername) {
        String removed = null;
        if (id != null && PlayerDataManager.isRealPlayer(id)) {
            removed = aliasMap.remove(id.toString().replace("-", ""));
            PlayerDataManager.updatePlayerDataAndEntityData(id);
        } else if (playername != null) {
            removed = aliasMap.remove(playername);
            PlayerDataManager.updatePlayerDataAndEntityData(playername);
        }
        if (removed != null) {
            dirty = true;
            MinecraftForge.EVENT_BUS.post(new AliasEvent(AliasEvent.Type.REMOVED, id, playername));
        }
        return removed != null;
    }

    private static void loadDataFromFiles() {
        if (aliasDataFile.exists()) {
            loadDataFromFile(aliasDataFile);
            return;
        }
        final File legacyFile = new File(Minecraft.getMinecraft().mcDataDir, "config/aliasData.json");
        if (legacyFile.exists()) {
            final Map<String, String> map = loadDataFromFile(legacyFile);
            if (map != null) {
                writeDataToFile(aliasDataFile, map);
            }
            if (legacyFile.delete()) {
                MWE.logger.info("Deleted legacy alias data file: {}", legacyFile);
            } else {
                MWE.logger.error("Failed to delete legacy alias data file: {}", legacyFile);
            }
        }
    }

    private static Map<String, String> loadDataFromFile(File file) {
        try (FileReader reader = new FileReader(file)) {
            final HashMap<String, String> map = new Gson().fromJson(reader, new TypeToken<HashMap<String, String>>() {}.getType());
            if (map != null) {
                // off thread snapshot to feed to main thread
                final Map<String, String> snapshot = new HashMap<>(map);
                Minecraft.getMinecraft().addScheduledTask(() -> aliasMap.putAll(snapshot));
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void writeDataToFile(File aliasDataFile, Map<String, String> map) {
        try (final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(aliasDataFile))) {
            final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            final String jsonString = gson.toJson(map);
            bufferedWriter.write(jsonString);
            dirty = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}