package fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;

public class MegaWallsClassSkinData {

	private String chosen_class;
	private String chosen_skin_class;

	public MegaWallsClassSkinData(JsonObject playerData) {

		if(playerData == null) 
			return;	

		JsonObject statsdata = playerData.get("stats").getAsJsonObject();

		if(statsdata == null)
			return;

		JsonElement mwElem = statsdata.get("Walls3");

		if(mwElem == null)
			return;

		JsonObject mwdata = mwElem.getAsJsonObject();
		
		this.chosen_class = JsonUtil.getString(mwdata,"chosen_class");
		this.chosen_skin_class = JsonUtil.getString(mwdata,"chosen_skin_" + this.chosen_class);
		
	}

	public String getCurrentmwclass() {
		return this.chosen_class;
	}

	public String getCurrentmwskin() {
		return this.chosen_skin_class;
	}
}
