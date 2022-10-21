package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;

public class HypixelPlayerData {

    private final JsonObject playerData;
    private final String uuid;

    public HypixelPlayerData(String uuid) throws ApiException {

        final HttpClient httpclient = new HttpClient("https://api.hypixel.net/player?key=" + HypixelApiKeyUtil.getApiKey() + "&uuid=" + uuid);
        final String rawresponse = httpclient.getRawResponse();

        if (rawresponse == null) {
            throw new ApiException("No response from Hypixel's Api");
        }

        final JsonObject obj = new JsonParser().parse(rawresponse).getAsJsonObject();

        if (obj == null) {
            throw new ApiException("Cannot parse response from Hypixel's Api");
        }

        if (!JsonUtil.getBoolean(obj, "success")) {
            final String msg = JsonUtil.getString(obj, "cause");
            if (msg == null) {
                throw new ApiException("Failed to retreive data from Hypixel's Api for this player");
            } else {
                throw new ApiException(msg);
            }
        }

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
