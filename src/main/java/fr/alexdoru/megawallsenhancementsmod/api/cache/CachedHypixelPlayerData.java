package fr.alexdoru.megawallsenhancementsmod.api.cache;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.requests.HypixelPlayerData;

public class CachedHypixelPlayerData {

	private static JsonObject playerData;
	private static String uuid;
	private static long timestamp;
	
	public CachedHypixelPlayerData(String uuidIn, String apikey) throws ApiException {
		
		if(uuid != null && uuid.equals(uuidIn) && (System.currentTimeMillis() - timestamp) < 60000L) // don't send a request again if it is the same player as before
			return;
		
		HypixelPlayerData hypixelPlayerData = new HypixelPlayerData(uuidIn,apikey);
		
		playerData = hypixelPlayerData.getPlayerData();
		uuid = uuidIn;
		timestamp = System.currentTimeMillis();
		
	}
	
	public JsonObject getPlayerData() {
		return playerData;
	}

	public String getUuid() {
		return uuid;
	}
		
}
