package fr.alexdoru.mwe.http.cache;

import com.google.gson.JsonObject;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.http.requests.HypixelPlayerData;
import fr.alexdoru.mwe.utils.TimerUtil;

import java.util.Objects;
import java.util.UUID;

public class CachedHypixelPlayerData {

    private static final TimerUtil TIMER = new TimerUtil(60000L);

    private static JsonObject cachedData;
    private static UUID cachedUUID;

    public static synchronized JsonObject getPlayerData(UUID id) throws ApiException {
        Objects.requireNonNull(id);
        if (!TIMER.update() && id.equals(cachedUUID)) {
            return cachedData;
        }
        cachedData = new HypixelPlayerData(id).getPlayerData();
        cachedUUID = id;
        return cachedData;
    }

}
