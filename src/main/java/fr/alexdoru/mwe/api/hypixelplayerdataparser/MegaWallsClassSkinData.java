package fr.alexdoru.mwe.api.hypixelplayerdataparser;

import com.google.gson.JsonObject;
import fr.alexdoru.mwe.utils.JsonUtil;

public class MegaWallsClassSkinData {

    private String chosen_class;
    private String chosen_skin_class;

    public MegaWallsClassSkinData(JsonObject playerData) {
        if (playerData == null) {
            return;
        }
        final JsonObject statsObj = JsonUtil.getJsonObject(playerData, "stats");
        if (statsObj == null) {
            return;
        }
        final JsonObject megaWallsStatsObj = JsonUtil.getJsonObject(statsObj, "Walls3");
        if (megaWallsStatsObj == null) {
            return;
        }
        this.chosen_class = JsonUtil.getString(megaWallsStatsObj, "chosen_class");
        this.chosen_skin_class = JsonUtil.getString(megaWallsStatsObj, "chosen_skin_" + this.chosen_class);
    }

    public String getCurrentmwclass() {
        return this.chosen_class;
    }

    public String getCurrentmwskin() {
        if (this.chosen_skin_class == null) {
            return this.chosen_class;
        }
        return this.chosen_skin_class;
    }

}
