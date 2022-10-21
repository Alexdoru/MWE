package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;

public class MojangPlayernameToUUID {

    private final String name;
    private final String uuid;

    public MojangPlayernameToUUID(String playername) throws ApiException {
        final HttpClient httpClient = new HttpClient("https://api.mojang.com/users/profiles/minecraft/" + playername);
        final JsonObject obj = httpClient.getJsonResponse();
        final String id = obj.get("id").getAsString();
        this.name = obj.get("name").getAsString();
        this.uuid = id.replace("-", "");
    }

    public String getName() {
        return this.name;
    }

    public String getUuid() {
        return this.uuid;
    }

}
