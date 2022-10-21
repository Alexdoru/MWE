package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;

public class MojangPlayernameToUUID {

    private final String name;
    private final String uuid;

    public MojangPlayernameToUUID(String playername) throws ApiException {
        final HttpClient httpClient = new HttpClient("https://api.mojang.com/users/profiles/minecraft/" + playername);
        final JsonObject obj = httpClient.getJsonResponse();
        this.name = JsonUtil.getString(obj, "name");
        final String id = JsonUtil.getString(obj, "id");
        if (this.name == null || id == null) {
            throw new ApiException(ChatUtil.invalidplayernameMsg(playername));
        }
        this.uuid = id.replace("-", "");
    }

    public String getName() {
        return this.name;
    }

    public String getUuid() {
        return this.uuid;
    }

}
