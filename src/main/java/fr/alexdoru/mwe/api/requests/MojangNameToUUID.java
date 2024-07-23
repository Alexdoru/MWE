package fr.alexdoru.mwe.api.requests;

import com.google.gson.JsonObject;
import fr.alexdoru.mwe.api.HttpClient;
import fr.alexdoru.mwe.api.exceptions.ApiException;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.utils.JsonUtil;
import fr.alexdoru.mwe.utils.UUIDUtil;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class MojangNameToUUID {

    private static final ConcurrentHashMap<String, NameUuidData> nameUuidCache = new ConcurrentHashMap<>();
    private final String name;
    private final String uuid;

    public MojangNameToUUID(String playername) throws ApiException {
        final String lowerCaseName = playername.toLowerCase();
        if (!Pattern.compile("\\w{1,16}").matcher(lowerCaseName).matches()) {
            throw new ApiException(ChatUtil.invalidMinecraftNameMsg(playername));
        }
        final NameUuidData cachedData = nameUuidCache.get(lowerCaseName);
        if (cachedData != null) {
            this.name = cachedData.name;
            this.uuid = cachedData.uuid;
            return;
        }
        final HttpClient httpClient = new HttpClient("https://api.mojang.com/users/profiles/minecraft/" + playername);
        final JsonObject obj = httpClient.getJsonResponse();
        this.name = JsonUtil.getString(obj, "name");
        final String id = JsonUtil.getString(obj, "id");
        if (this.name == null || id == null) {
            throw new ApiException(ChatUtil.inexistantMinecraftNameMsg(playername));
        }
        this.uuid = id.replace("-", "");
        nameUuidCache.put(lowerCaseName, new NameUuidData(this.name, this.uuid));
    }

    public String getName() {
        return this.name;
    }

    public String getUuid() {
        return this.uuid;
    }

    public UUID getUUID() {
        return UUIDUtil.fromString(this.uuid);
    }

    static class NameUuidData {

        public final String name;
        public final String uuid;

        NameUuidData(String name, String uuid) {
            this.name = name;
            this.uuid = uuid;
        }

    }

}


