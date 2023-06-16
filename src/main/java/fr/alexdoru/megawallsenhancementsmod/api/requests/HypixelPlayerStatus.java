package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.enums.GameType;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;

public class HypixelPlayerStatus {

    private final boolean online;
    private String gamemode;
    private String mode;
    private String map;

    public HypixelPlayerStatus(String uuid) throws ApiException {
        final HttpClient httpClient = new HttpClient("https://api.hypixel.net/status?uuid=" + uuid);
        final JsonObject obj = httpClient.getJsonResponse();
        final JsonObject sessionobj = JsonUtil.getJsonObject(obj, "session");
        if (sessionobj == null) {
            throw new ApiException("Failed to retreive data from Hypixel's Api for this player");
        }
        this.online = JsonUtil.getBoolean(sessionobj, "online");
        if (this.online) {
            this.gamemode = JsonUtil.getString(sessionobj, "gameType");
            this.mode = JsonUtil.getString(sessionobj, "mode");
            this.map = JsonUtil.getString(sessionobj, "map");
        }
    }

    public boolean isOnline() {
        return this.online;
    }

    public String getGamemode() {
        if (this.gamemode == null) {
            return "?";
        }
        final GameType gameType = GameType.fromId(this.gamemode);
        return gameType == GameType.UNKNOWN ? this.gamemode : gameType.toString();
    }

    public String getMode() {

        if (this.mode == null) {
            return "?";
        }

        switch (this.mode) {

            case "LOBBY":
                return "Lobby";
            case "EIGHT_ONE":
                return "Solo";
            case "EIGHT_TWO":
                return "Doubles";
            case "FOUR_THREE":
                return "3v3v3v3";
            case "FOUR_FOUR":
                return "4v4v4v4";
            case "TWO_FOUR":
                return "4v4";

            case "dynamic":
                return "Private Island";
            case "hub":
                return "Hub";
            case "combat_1":
                return "Spider's Den";
            case "combat_2":
                return "Blazing Fortress";
            case "combat_3":
                return "The End";
            case "farming_1":
                return "The Barn";
            case "farming_2":
                return "Mushroom Desert";
            case "foraging_1":
                return "The Park";
            case "mining_1":
                return "Gold Mine";
            case "mining_2":
                return "Deep Caverns";
            case "mining_3":
                return "Dwarven Mines";

            case "teams_insane":
                return "duo insane";
            case "teams_normal":
                return "duo normal";
            case "ranked_normal":
                return "ranked";
            case "mega_normal":
                return "mega";
            case "solo_insane_rush":
                return "laboratory : rush";
            case "solo_insane_slime":
                return "laboratory : slime";

            case "MURDER_DOUBLE_UP":
                return "Double up !";
            case "MURDER_INFECTION	":
                return "Infection v2";
            case "MURDER_ASSASSINS":
                return "Assassins";
            case "MURDER_CLASSIC":
                return "Classic";

            case "PARTY":
                return "Party Games";
            case "DEFENDER":
                return "Creeper Attack";
            case "SIMON_SAYS":
                return "Hypixel Says";
            case "DAYONE":
                return "Blocking dead";
            case "DRAW_THEIR_THING":
                return "Pixel painters";
            case "SOCCER":
                return "Football";
            case "PVP_CTW":
                return "Capture the wool";
            case "ENDER":
                return "Ender spleef";
            case "STARWARS":
                return "";
            case "DRAGONWARS2":
                return "Dragon wars";

            case "SOLO_PRO":
                return "Pro mode";

            default:
                return this.mode.toLowerCase().replace('_', ' ');
        }

    }

    public String getMap() {
        return this.map;
    }

}
