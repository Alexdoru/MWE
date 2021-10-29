package fr.alexdoru.megawallsenhancementsmod.utils;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

// https://github.com/5zig/The-5zig-Mod/blob/efce66b2cec83e76f40e4e18e96a2013a89e0a42/The%205zig%20Mod/src/eu/the5zig/mod/util/JsonUtil.java
public class JsonUtil {

    public static int getInt(JsonObject object, String name) {
        JsonElement element = object.get(name);
        if (element == null || element.isJsonNull())
            return 0;
        return element.getAsInt();
    }

    public static double getDouble(JsonObject object, String name) {
        JsonElement element = object.get(name);
        if (element == null || element.isJsonNull())
            return 0;
        return element.getAsDouble();
    }

    public static long getLong(JsonObject object, String name) {
        JsonElement element = object.get(name);
        if (element == null || element.isJsonNull())
            return 0;
        return element.getAsLong();
    }

    public static String getString(JsonObject object, String name) {
        JsonElement element = object.get(name);
        if (element == null || element.isJsonNull())
            return null;
        return element.getAsString();
    }

    public static JsonObject getJsonObject(JsonObject object, String name) {
        JsonElement element = object.get(name);
        if (element == null || element.isJsonNull())
            return null;
        return element.getAsJsonObject();
    }

    public static List<String> getList(JsonObject object, String name) {
        List<String> result = Lists.newArrayList();
        JsonElement element = object.get(name);
        if (element == null || element.isJsonNull())
            return result;
        for (JsonElement jsonElement : element.getAsJsonArray()) {
            result.add(jsonElement.getAsString());
        }
        return result;
    }

}
