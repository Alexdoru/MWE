package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;

import java.util.concurrent.ConcurrentHashMap;

public class MojangPlayernameToUUID {

    private static final ConcurrentHashMap<String, NameUuidData> nameUuidCache = new ConcurrentHashMap<>();
    private final String name;
    private final String uuid;

    public MojangPlayernameToUUID(String playername) throws ApiException {
        final String lowerCaseName = playername.toLowerCase();
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
            throw new ApiException(ChatUtil.invalidPlayernameMsg(playername));
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

    static class NameUuidData {

        public final String name;
        public final String uuid;

        NameUuidData(String name, String uuid) {
            this.name = name;
            this.uuid = uuid;
        }

    }

}


