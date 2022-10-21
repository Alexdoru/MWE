package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;

public class HypixelPlayerData {

    private final JsonObject playerData;
    private final String uuid;

    public HypixelPlayerData(String uuid) throws ApiException {
        final HttpClient httpClient = new HttpClient("https://api.hypixel.net/player?key=" + HypixelApiKeyUtil.getApiKey() + "&uuid=" + uuid);
        final JsonObject obj = httpClient.getJsonResponse();
        final JsonElement playerdataElem = obj.get("player");
        if (playerdataElem == null || !playerdataElem.isJsonObject()) {
            throw new ApiException("This player never joined Hypixel, it might be a nick.");
        }
        final JsonObject playerdata = playerdataElem.getAsJsonObject();
        if (playerdata == null) {
            throw new ApiException("An error occured while parsing data for this player on Hypixel's Api");
        }
        this.playerData = playerdata;
        this.uuid = uuid;
    }

    public JsonObject getPlayerData() {
        return this.playerData;
    }

    public String getUuid() {
        return this.uuid;
    }
}
