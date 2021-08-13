package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;

public class HypixelPlayerStatus {

	private boolean online;
	private String gamemode;
	private String mode;
	private String map;

	public HypixelPlayerStatus(String uuid, String apikey) throws ApiException {
		
		if(uuid.equals("57715d32a6854e2eae6854c19808b58d")
		|| uuid.equals("02106bcbab12443c80854c1ad076a0a2")
		|| uuid.equals("5855376dd4e246b2950b53f4b6abc130")
		|| uuid.equals("e2e52a0fae3f4536ae0eb0c06765b845")
		|| uuid.equals("092ae9d2b0084ecf9d898671d82a627d")
		|| uuid.equals("5f78e31ebfac4368a79d6bd5d5bf3fa5")) {
			throw new ApiException("User blacklisted L");
		}

		HttpClient httpclient = new HttpClient("https://api.hypixel.net/status?key=" + apikey + "&uuid=" + uuid);
		String rawresponse = httpclient.getrawresponse();

		if(rawresponse == null)
			throw new ApiException("No response from Hypixel's Api");

		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(rawresponse).getAsJsonObject();

		if (obj == null)
			throw new ApiException("Cannot parse response from Hypixel's Api");	

		if(!obj.get("success").getAsBoolean()) {

			String msg = JsonUtil.getString(obj, "cause");

			if(msg==null) {				
				throw new ApiException("Failed to retreive data from Hypixel's Api for this player");
			} else {
				throw new ApiException(msg);				
			}

		}

		JsonObject sessionobj = obj.get("session").getAsJsonObject();
		this.online = sessionobj.get("online").getAsBoolean();

		if(this.online) {
			this.gamemode = sessionobj.get("gameType").getAsString(); // can be null
			this.mode = sessionobj.get("mode").getAsString();         // can be null

			JsonElement mapelem = sessionobj.get("map");

			if(mapelem!=null)
				this.map = mapelem.getAsString();                     // can be null    

		}

		return;

	}

	public boolean getOnline() {
		return this.online;
	}

	public String getGamemode() {

		if(this.gamemode == null)
			return "?";

		switch(this.gamemode) {
		case "QUAKECRAFT":
			return "Quake";
		case "WALLS":
			return "Walls";
		case "PAINTBALL":
			return "Paintball";
		case "SURVIVAL_GAMES":
			return "Blitz Survival Games";
		case "TNTGAMES":
			return "TNT Games";
		case "VAMPIREZ":
			return "VampireZ";
		case "WALLS3":
			return "Mega Walls";
		case "ARCADE":
			return "Arcade";
		case "ARENA":
			return "Arena";
		case "UHC":
			return "UHC Champions";
		case "MCGO":
			return "Cops and Crims";	
		case "BATTLEGROUND":
			return "Warlords";
		case "SUPER_SMASH":
			return "Smash Heroes";	
		case "GINGERBREAD":
			return "Turbo Kart Racers";
		case "HOUSING":
			return "Housing";	
		case "SKYWARS":
			return "SkyWars";
		case "TRUE_COMBAT":
			return "Crazy Walls";	
		case "SPEED_UHC":
			return "Speed UHC";		
		case "SKYCLASH":
			return "SkyClash";	
		case "LEGACY":
			return "Classic Games";		
		case "PROTOTYPE":
			return "Prototype";	
		case "BEDWARS":
			return "Bed Wars";	
		case "MURDER_MYSTERY":
			return "Murder Mystery";	
		case "BUILD_BATTLE":
			return "Build Battle";		
		case "DUELS":
			return "Duels";	
		case "SKYBLOCK":
			return "SkyBlock";		
		case "PIT":
			return "Pit";	
		case "REPLAY":
			return "Replay";	
		case "SMP":
			return "SMP";	
		default:
			return this.gamemode;

		}

	}

	public String getMode() {

		if(this.mode==null)
			return "?";		

		switch(this.mode) {

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
