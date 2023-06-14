package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class MojangPlayernameToUUID {

    private static final ConcurrentHashMap<String, NameUuidData> nameUuidCache = new ConcurrentHashMap<>();
    private final String name;
    private final String uuid;

    public MojangPlayernameToUUID(String playername) throws ApiException {
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
        // 8 - 4 - 4 - 4 - 12
        final StringBuilder sb = new StringBuilder(this.uuid);
        sb.insert(8 + 4 + 4 + 4, '-');
        sb.insert(8 + 4 + 4, '-');
        sb.insert(8 + 4, '-');
        sb.insert(8, '-');
        return UUID.fromString(sb.toString());
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


