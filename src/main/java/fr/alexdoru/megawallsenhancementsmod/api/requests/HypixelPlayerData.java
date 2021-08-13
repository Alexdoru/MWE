package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;

public class HypixelPlayerData {

	private JsonObject playerData;
	private String uuid;
	
	public HypixelPlayerData(String uuid, String apikey) throws ApiException {
				
		HttpClient httpclient = new HttpClient("https://api.hypixel.net/player?key=" + apikey + "&uuid=" + uuid);
		String rawresponse = httpclient.getrawresponse();
								
		if(rawresponse == null) 
			throw new ApiException("No response from Hypixel's Api");		

		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(rawresponse).getAsJsonObject();

		if(obj == null)
			throw new ApiException("Cannot parse response from Hypixel's Api");	

		if(!obj.get("success").getAsBoolean()) {
			
			String msg = JsonUtil.getString(obj, "cause");
			
			if(msg==null) {				
				throw new ApiException("Failed to retreive data from Hypixel's Api for this player");
			} else {
				throw new ApiException(msg);				
			}
			
		}

		JsonElement playerdataElem = obj.get("player");

		if(playerdataElem == null || !playerdataElem.isJsonObject()) {
			throw new ApiException("This player never joined Hypixel");
		}
			
		JsonObject playerdata = playerdataElem.getAsJsonObject();

		if (playerdata == null) 
			throw new ApiException("An error occured while parsing data for this player on Hypixel's Api");

		this.playerData = playerdata;
		this.uuid = uuid;
		return;

	}

	public JsonObject getPlayerData() {
		return this.playerData;
	}

	public String getUuid() {
		return this.uuid;
	}
}
