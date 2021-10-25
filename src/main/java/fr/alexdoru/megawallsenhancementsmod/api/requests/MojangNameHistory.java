package fr.alexdoru.megawallsenhancementsmod.api.requests;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;

public class MojangNameHistory {

	private static List<String> names;
	private static List<Long> timestamps;
	private static String uuid;

	public MojangNameHistory(String uuid) throws ApiException {
		
		if(this.uuid != null && this.uuid.equals(uuid))
			return;

		HttpClient httpclient = new HttpClient("https://api.mojang.com/user/profiles/" + uuid + "/names");
		String rawresponse = httpclient.getrawresponse();

		if(rawresponse == null)
			throw new ApiException("No response from Mojang's Api");	

		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(rawresponse).getAsJsonArray();
		
		if (array.size() == 0) 
			throw new ApiException("Name history data is empty");	

		List<String> nameslist = new ArrayList<String>();
		List<Long> timestampslist = new ArrayList<Long>();

		for (int i = 0; i < array.size(); i++) {

			JsonObject obj = array.get(i).getAsJsonObject();
			String name = obj.get("name").getAsString();
			Long timestamp = (i == 0) ? 0L : obj.get("changedToAt").getAsLong();

			nameslist.add(name);
			timestampslist.add(timestamp);
		} 

		this.names = nameslist;
		this.timestamps = timestampslist;
		this.uuid = uuid;

		return;

	}

	public List<String> getNames() {
		return this.names;
	}

	public List<Long> getTimestamps() {
		return this.timestamps;
	}

}
