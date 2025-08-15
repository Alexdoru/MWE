package fr.alexdoru.mwe.http.cache;

import com.google.gson.JsonObject;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.http.requests.HypixelPlayerData;
import fr.alexdoru.mwe.utils.TimerUtil;

public class CachedHypixelPlayerData {

    private static final TimerUtil timer = new TimerUtil(60000L);
    private static JsonObject playerData;
    private static String uuid;

    public static synchronized JsonObject getPlayerData(String uuidIn) throws ApiException {
        if (!timer.update() && uuidIn.equals(uuid)) {
            return playerData;
        }
        playerData = new HypixelPlayerData(uuidIn).getPlayerData();
        uuid = uuidIn;
        return playerData;
    }

}
