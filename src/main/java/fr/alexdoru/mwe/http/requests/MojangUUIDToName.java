package fr.alexdoru.mwe.http.requests;

import com.google.gson.JsonObject;
import fr.alexdoru.mwe.http.HttpClient;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.utils.JsonUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class MojangUUIDToName {

    private static final Map<UUID, String> CACHE = new ConcurrentHashMap<>();

    public static String getName(UUID id) throws ApiException {
        if (CACHE.containsKey(id)) {
            // if the player changes their username while we are playing, their new name will not show
            return CACHE.get(id);
        }
        final JsonObject jsonObject = HttpClient.getAsJsonObject("https://sessionserver.mojang.com/session/minecraft/profile/" + id.toString());
        final String name = JsonUtil.getString(jsonObject, "name");
        if (name == null) {
            throw new ApiException("Invalid UUID");
        }
        CACHE.put(id, name);
        return name;
    }

}
