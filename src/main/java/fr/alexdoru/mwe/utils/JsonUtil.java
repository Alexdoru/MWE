package fr.alexdoru.mwe.utils;

import com.google.common.collect.Lists;
import com.google.gson.*;
import fr.alexdoru.mwe.MWE;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public final class JsonUtil {

    private JsonUtil() {}

    private static final Gson GSON = new Gson();
    private static final Gson GSON_PRETTY = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    public static <T> @Nullable T readFromFile(File file, Type type) {
        boolean useFallback = false;
        try (Reader reader = Files.newBufferedReader(file.toPath())) {
            return GSON.fromJson(reader, type);
        } catch (JsonSyntaxException e) {
            if (e.getCause() instanceof MalformedInputException) {
                useFallback = true;
            }
            MWE.logger.error(e);
        } catch (JsonIOException | IOException e) {
            MWE.logger.error(e);
        }
        if (useFallback) {
            MWE.logger.error("File {} read in wrong encoding, trying to read file with fallback method...", file);
            return readFromFileFallback(file, type);
        }
        return null;
    }

    private static @Nullable <T> T readFromFileFallback(File file, Type type) {
        try (final FileReader fileReader = new FileReader(file)) {
            final T t = GSON.fromJson(fileReader, type);
            MWE.logger.info("Fallback read of {} successful", file);
            return t;
        } catch (Exception e) {
            MWE.logger.error("Failed fallback read of {}", file, e);
        }
        return null;
    }

    public static boolean writeJsonToFile(File file, Object obj) {
        final Path path = file.toPath();
        final Path parent = path.getParent();
        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                MWE.logger.error("Unable to create parent directory {}, data will not be saved!", parent, e);
                return false;
            }
        }
        final Path tmp = path.resolveSibling(path.getFileName() + ".tmp");
        try (Writer writer = Files.newBufferedWriter(tmp)) {
            GSON_PRETTY.toJson(obj, writer);
        } catch (JsonIOException | IOException e) {
            MWE.logger.error(e);
            deleteQuietly(tmp);
            return false;
        }
        try {
            Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            return true;
        } catch (IOException e) {
            MWE.logger.error(e);
            deleteQuietly(tmp);
            return false;
        }
    }

    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {}
    }

    public static boolean getBoolean(JsonObject object, String name) {
        final JsonElement element = object.get(name);
        if (element == null || element.isJsonNull()) {
            return false;
        }
        return element.getAsBoolean();
    }

    public static int getInt(JsonObject object, String name) {
        final JsonElement element = object.get(name);
        if (element == null || element.isJsonNull()) {
            return 0;
        }
        return element.getAsInt();
    }

    public static double getDouble(JsonObject object, String name) {
        final JsonElement element = object.get(name);
        if (element == null || element.isJsonNull()) {
            return 0D;
        }
        return element.getAsDouble();
    }

    public static long getLong(JsonObject object, String name) {
        final JsonElement element = object.get(name);
        if (element == null || element.isJsonNull()) {
            return 0L;
        }
        return element.getAsLong();
    }

    public static String getString(JsonObject object, String name) {
        final JsonElement element = object.get(name);
        if (element == null || element.isJsonNull()) {
            return null;
        }
        return element.getAsString();
    }

    public static JsonObject getJsonObject(JsonObject object, String name) {
        final JsonElement element = object.get(name);
        if (element instanceof JsonObject) {
            return element.getAsJsonObject();
        }
        return null;
    }

    public static JsonArray getJsonArray(JsonObject object, String name) {
        final JsonElement element = object.get(name);
        if (element instanceof JsonArray) {
            return element.getAsJsonArray();
        }
        return null;
    }

    public static List<String> getList(JsonObject object, String name) {
        final List<String> result = Lists.newArrayList();
        final JsonElement element = object.get(name);
        if (element == null || element.isJsonNull()) {
            return result;
        }
        for (final JsonElement jsonElement : element.getAsJsonArray()) {
            result.add(jsonElement.getAsString());
        }
        return result;
    }

}
