package fr.alexdoru.megawallsenhancementsmod.api.cache;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.utils.TimerUtil;

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
