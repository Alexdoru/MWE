package fr.alexdoru.mwe.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.api.events.AliasEvent;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class AliasDataManager {

    private AliasDataManager() {}

    private static final Map<String, String> aliasMap = new LinkedHashMap<>();
    private static File aliasDataFile;
    private static final AtomicBoolean dirty = new AtomicBoolean(false);
    private static boolean initialized;

    public static void loadData(File configFolder) {
        if (initialized) {
            throw new IllegalStateException("Already initialized");
        }
        initialized = true;
        aliasDataFile = new File(configFolder, "aliasData.json");
        MultithreadingUtil.queueIOTask(() -> {
            final Map<String, String> map = loadDataFromFiles();
            if (map != null) {
                Minecraft.getMinecraft().addScheduledTask(() -> aliasMap.putAll(map));
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (dirty.get()) {
                writeDataToFile(aliasDataFile, aliasMap);
            }
        }));
    }

    static void saveIfDirty() {
        if (dirty.get()) {
            dirty.set(false);
            final Map<String, String> snapshot = new LinkedHashMap<>(aliasMap);
            MultithreadingUtil.queueIOTask(() -> {
                if (!writeDataToFile(aliasDataFile, snapshot)) {
                    dirty.set(true);
                }
            });
        }
    }

    public static List<Map.Entry<String, String>> getEntries() {
        return new ArrayList<>(aliasMap.entrySet());
    }

    private static @NotNull String toKey(@NotNull UUID id) {
        return id.toString().replace("-", "");
    }

    @Nullable
    public static String getAlias(@Nullable UUID id, @Nullable String playername) {
        if (id != null && PlayerDataManager.isRealPlayer(id)) {
            return aliasMap.get(toKey(id));
        } else if (playername != null) {
            return aliasMap.get(playername);
        }
        return null;
    }

    public static void putAlias(@Nullable UUID id, @Nullable String playername, String alias) {
        String prevAlias = null;
        boolean added = false;
        if (id != null && PlayerDataManager.isRealPlayer(id)) {
            prevAlias = aliasMap.put(toKey(id), alias);
            PlayerDataManager.updatePlayerDataAndEntityData(id);
            added = true;
        } else if (playername != null) {
            prevAlias = aliasMap.put(playername, alias);
            PlayerDataManager.updatePlayerDataAndEntityData(playername);
            added = true;
        }
        if (added) {
            dirty.set(true);
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
            removed = aliasMap.remove(toKey(id));
            PlayerDataManager.updatePlayerDataAndEntityData(id);
        } else if (playername != null) {
            removed = aliasMap.remove(playername);
            PlayerDataManager.updatePlayerDataAndEntityData(playername);
        }
        if (removed != null) {
            dirty.set(true);
            MinecraftForge.EVENT_BUS.post(new AliasEvent(AliasEvent.Type.REMOVED, id, playername));
        }
        return removed != null;
    }

    @Nullable
    private static Map<String, String> loadDataFromFiles() {
        if (aliasDataFile.exists()) {
            return loadDataFromFile(aliasDataFile);
        }
        final File legacyFile = new File(Minecraft.getMinecraft().mcDataDir, "config/aliasData.json");
        if (legacyFile.exists()) {
            final Map<String, String> map = loadDataFromFile(legacyFile);
            if (map != null) {
                if (writeDataToFile(aliasDataFile, map)) {
                    if (legacyFile.delete()) {
                        MWE.logger.info("Deleted legacy alias data file: {}", legacyFile);
                    } else {
                        MWE.logger.error("Failed to delete legacy alias data file: {}", legacyFile);
                    }
                }
            }
            return map;
        }
        return null;
    }

    @Nullable
    private static Map<String, String> loadDataFromFile(File file) {
        try (FileReader reader = new FileReader(file)) {
            return new Gson().fromJson(reader, new TypeToken<HashMap<String, String>>() {}.getType());
        } catch (Exception e) {
            MWE.logger.error(e);
        }
        return null;
    }

    private static boolean writeDataToFile(File file, Map<String, String> map) {
        try {
            if (file.getParentFile() != null) {
                Files.createDirectories(file.getParentFile().toPath());
            }
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
                final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
                bufferedWriter.write(gson.toJson(map));
                return true;
            }
        } catch (IOException e) {
            MWE.logger.error(e);
        }
        return false;
    }

}