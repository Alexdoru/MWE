package fr.alexdoru.megawallsenhancementsmod.api.cache;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;
import fr.alexdoru.megawallsenhancementsmod.utils.TimerUtil;

public class CachedHypixelPlayerData {

    private static JsonObject playerData;
    private static String uuid;
    private static final TimerUtil timer = new TimerUtil(60000L);

    public CachedHypixelPlayerData(String uuidIn) throws ApiException {
        if (!timer.update() && uuid != null && uuid.equals(uuidIn)) {
            return;
        }
        final HypixelPlayerData hypixelPlayerData = new HypixelPlayerData(uuidIn);
        playerData = hypixelPlayerData.getPlayerData();
        uuid = uuidIn;
    }

    public JsonObject getPlayerData() {
        return playerData;
    }

    public String getUuid() {
        return uuid;
    }

}
