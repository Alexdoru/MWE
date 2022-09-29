package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;

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

        if (uuid != null && uuid.equals(uuidIn))
            return;

        HttpClient httpclient = new HttpClient("https://api.mojang.com/user/profiles/" + uuidIn + "/names");
        String rawresponse = httpclient.getrawresponse();

        if (rawresponse == null)
            throw new ApiException("No response from Mojang's Api");

        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(rawresponse).getAsJsonArray();

        if (array.size() == 0)
            throw new ApiException("Name history data is empty");

        List<String> nameslist = new ArrayList<>();
        List<Long> timestampslist = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {

            JsonObject obj = array.get(i).getAsJsonObject();
            String name = obj.get("name").getAsString();
            Long timestamp = (i == 0) ? 0L : obj.get("changedToAt").getAsLong();

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
