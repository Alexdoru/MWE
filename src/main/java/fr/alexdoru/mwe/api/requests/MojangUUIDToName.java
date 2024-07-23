package fr.alexdoru.mwe.api.requests;

import com.google.gson.JsonObject;
import fr.alexdoru.mwe.api.HttpClient;
import fr.alexdoru.mwe.api.exceptions.ApiException;
import fr.alexdoru.mwe.utils.JsonUtil;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MojangUUIDToName {

    private static final ConcurrentHashMap<UUID, String> nameCache = new ConcurrentHashMap<>();

    public static String getName(UUID uuid) throws ApiException {
        if (nameCache.containsKey(uuid)) {
            // if the player changes their username while we are playing, their new name will not show
            return nameCache.get(uuid);
        }
        final JsonObject jsonObject = HttpClient.getAsJsonObject("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString());
        final String name = JsonUtil.getString(jsonObject, "name");
        if (name == null) {
            throw new ApiException("Invalid UUID");
        }
        nameCache.put(uuid, name);
        return name;
    }

}
