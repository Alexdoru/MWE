package fr.alexdoru.megawallsenhancementsmod.api.cache;

import com.google.gson.JsonObject;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;

public class CachedHypixelPlayerData {

	private static JsonObject playerData;
	private static String uuid;
	private static long timestamp;
	
	public CachedHypixelPlayerData(String uuid, String apikey) throws ApiException {
		
		if(this.uuid != null && this.uuid.equals(uuid) && (System.currentTimeMillis() - this.timestamp) < 60000L) // don't send a request again if it is the same player as before
			return;
		
		HypixelPlayerData hypixelPlayerData = new HypixelPlayerData(uuid,apikey);
		
		this.playerData = hypixelPlayerData.getPlayerData();
		this.uuid = uuid;
		this.timestamp = System.currentTimeMillis();
		
	}
	
	public JsonObject getPlayerData() {
		return this.playerData;
	}

	public String getUuid() {
		return this.uuid;
	}
		
}
