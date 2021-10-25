package fr.alexdoru.megawallsenhancementsmod.api.cache;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.api.requests.MojangPlayernameToUUID;

public class CachedMojangUUID {

	private static String name;
	private static String uuid;

	public CachedMojangUUID(String playername) throws ApiException {		
		
		if(this.name !=null && this.name.equalsIgnoreCase(playername)) // don't send a request again if it is the same player as before
			return;
		
		MojangPlayernameToUUID mojangPlayernameToUUID = new MojangPlayernameToUUID(playername);
		
		this.name = mojangPlayernameToUUID.getName();
		this.uuid = mojangPlayernameToUUID.getUuid();
		
	}
	
	public String getName() {
		return this.name;
	}

	public String getUuid() {
		return this.uuid;
	}
	
}
