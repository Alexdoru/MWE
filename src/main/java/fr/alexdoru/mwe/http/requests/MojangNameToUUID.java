package fr.alexdoru.mwe.http.requests;

import com.google.gson.JsonObject;
import fr.alexdoru.mwe.api.IPlayerUUID;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.http.HttpClient;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.utils.JsonUtil;
import fr.alexdoru.mwe.utils.UUIDUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class MojangNameToUUID {

    private static final Pattern NAME_PATTERN = Pattern.compile("\\w{1,16}");
    private static final Map<String, IPlayerUUID> CACHE = new ConcurrentHashMap<>();

    public static IPlayerUUID getPlayerUUID(String playername) throws ApiException {
        if (!NAME_PATTERN.matcher(playername).matches()) {
            throw new ApiException(ChatUtil.invalidMinecraftNameMsg(playername));
        }
        final String nameKEY = playername.toLowerCase();
        if (CACHE.containsKey(nameKEY)) {
            return CACHE.get(nameKEY);
        }
        final JsonObject jsonObject = HttpClient.getAsJsonObject("https://api.mojang.com/users/profiles/minecraft/" + playername);
        final String name = JsonUtil.getString(jsonObject, "name");
        final String id = JsonUtil.getString(jsonObject, "id");
        if (name == null || id == null) {
            throw new ApiException(ChatUtil.inexistantMinecraftNameMsg(playername));
        }
        final UUID uuid = UUIDUtil.fromString(id);
        if (uuid == null) {
            throw new ApiException("Malformed UUID");
        }
        final IPlayerUUID playerUUID = new NameUuidData(name, uuid);
        CACHE.put(nameKEY, playerUUID);
        return playerUUID;
    }

    private static class NameUuidData implements IPlayerUUID {

        @NotNull
        private final String name;
        @NotNull
        private final UUID id;

        NameUuidData(@NotNull String name, @NotNull UUID id) {
            this.name = name;
            this.id = id;
        }

        @NotNull
        @Override
        public String getName() {
            return this.name;
        }

        @NotNull
        @Override
        public UUID getId() {
            return this.id;
        }
    }

}


