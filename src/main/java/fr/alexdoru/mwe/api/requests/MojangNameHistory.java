package fr.alexdoru.mwe.api.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.alexdoru.mwe.api.HttpClient;
import fr.alexdoru.mwe.api.exceptions.ApiException;
import fr.alexdoru.mwe.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * API was terminated by Microsoft RIP
 */
@Deprecated
public class MojangNameHistory {

    private static List<String> names;
    private static List<Long> timestamps;
    private static String uuid;

    public MojangNameHistory(String uuidIn) throws ApiException {

        if (uuid != null && uuid.equals(uuidIn)) {
            return;
        }

        final HttpClient httpClient = new HttpClient("https://api.mojang.com/user/profiles/" + uuidIn + "/names");
        final JsonArray array = httpClient.getJsonResponse().getAsJsonArray();

        if (array.size() == 0) {
            throw new ApiException("Name history data is empty");
        }

        final List<String> nameslist = new ArrayList<>();
        final List<Long> timestampslist = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            final JsonObject obj = array.get(i).getAsJsonObject();
            final String name = JsonUtil.getString(obj, "name");
            final Long timestamp = (i == 0) ? 0L : JsonUtil.getLong(obj, "changedToAt");
            nameslist.add(name);
            timestampslist.add(timestamp);
        }

        names = nameslist;
        timestamps = timestampslist;
        uuid = uuidIn;

    }

    public List<String> getNames() {
        return names;
    }

    public List<Long> getTimestamps() {
        return timestamps;
    }

}
