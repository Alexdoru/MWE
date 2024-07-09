package fr.alexdoru.mwe.utils;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class JsonUtil {

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
        if (element == null || element.isJsonNull())
            return 0L;
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
