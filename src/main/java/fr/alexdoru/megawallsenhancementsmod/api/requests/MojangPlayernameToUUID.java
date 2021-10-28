package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;

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
		
		HttpClient httpclient = new HttpClient("https://api.mojang.com/users/profiles/minecraft/" + playername);
		String rawresponse = httpclient.getrawresponse();

		if(rawresponse == null) {
			this.name = null;
			this.uuid = null;
			throw new ApiException(ChatUtil.invalidplayernameMsg(playername));
		}

		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(rawresponse).getAsJsonObject();
		String id = obj.get("id").getAsString();

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
