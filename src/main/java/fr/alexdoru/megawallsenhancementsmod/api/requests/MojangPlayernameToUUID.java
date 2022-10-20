package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;

/* Contacts Mojang's api to retrieve uuid
 * response looks like this :
 * 
 * {
    "name": "KrisJelbring",
    "id": "7125ba8b1c864508b92bb5c042ccfe2b"
	}
 * 
 */

public class MojangPlayernameToUUID {

    private final String name;
    private final String uuid;

    public MojangPlayernameToUUID(String playername) throws ApiException {

        final HttpClient httpclient = new HttpClient("https://api.mojang.com/users/profiles/minecraft/" + playername);
        final String rawresponse = httpclient.getRawResponse();

        if (rawresponse == null) {
            this.name = null;
            this.uuid = null;
            throw new ApiException(ChatUtil.invalidplayernameMsg(playername));
        }

        final JsonParser parser = new JsonParser();
        final JsonObject obj = parser.parse(rawresponse).getAsJsonObject();
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
