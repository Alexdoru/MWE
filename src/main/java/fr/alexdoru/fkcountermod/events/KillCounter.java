package fr.alexdoru.fkcountermod.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.utils.ScoreboardUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class KillCounter {

	private static final String PREP_PHASE = "Prepare your defenses!";

	private static final String[] KILL_PATTERNS = {
			"(\\w+) was shot and killed by (\\w+).*",
			"(\\w+) was snowballed to death by (\\w+).*",
			"(\\w+) was killed by (\\w+).*",
			"(\\w+) was killed with a potion by (\\w+).*",
			"(\\w+) was killed with an explosion by (\\w+).*",
			"(\\w+) was killed with magic by (\\w+).*",
			"(\\w+) was filled full of lead by (\\w+).*",
			"(\\w+) was iced by (\\w+).*",
			"(\\w+) met their end by (\\w+).*",
			"(\\w+) lost a drinking contest with (\\w+).*",
			"(\\w+) was killed with dynamite by (\\w+).*",
			"(\\w+) lost the draw to (\\w+).*",
			"(\\w+) was struck down by (\\w+).*",
			"(\\w+) was turned to dust by (\\w+).*",
			"(\\w+) was turned to ash by (\\w+).*",
			"(\\w+) was melted by (\\w+).*",
			"(\\w+) was incinerated by (\\w+).*",
			"(\\w+) was vaporized by (\\w+).*",
			"(\\w+) was struck with Cupid's arrow by (\\w+).*",
			"(\\w+) was given the cold shoulder by (\\w+).*",
			"(\\w+) was hugged too hard by (\\w+).*",
			"(\\w+) drank a love potion from (\\w+).*",
			"(\\w+) was hit by a love bomb from (\\w+).*",
			"(\\w+) was no match for (\\w+).*",
			"(\\w+) was smote from afar by (\\w+).*",
			"(\\w+) was justly ended by (\\w+).*",
			"(\\w+) was purified by (\\w+).*",
			"(\\w+) was killed with holy water by (\\w+).*",
			"(\\w+) was dealt vengeful justice by (\\w+).*",
			"(\\w+) was returned to dust by (\\w+).*",
			"(\\w+) be shot and killed by (\\w+).*",
			"(\\w+) be snowballed to death by (\\w+).*",
			"(\\w+) be sent to Davy Jones' locker by (\\w+).*",
			"(\\w+) be killed with rum by (\\w+).*",
			"(\\w+) be shot with cannon by (\\w+).*",
			"(\\w+) be killed with magic by (\\w+).*",
			"(\\w+) was glazed in BBQ sauce by (\\w+).*",
			"(\\w+) was sprinked in chilli poweder by (\\w+).*",
			"(\\w+) was sliced up by (\\w+).*",
			"(\\w+) was overcooked by (\\w+).*",
			"(\\w+) was deep fried by (\\w+).*",
			"(\\w+) was boiled by (\\w+).*",
			"(\\w+) was injected with malware by (\\w+).*",
			"(\\w+) was DDoS'd by (\\w+).*",
			"(\\w+) was deleted by (\\w+).*",
			"(\\w+) was purged by an antivirus owned by (\\w+).*",
			"(\\w+) was fragmented by (\\w+).*",
			"(\\w+) was squeaked from a distance by (\\w+).*",
			"(\\w+) was hit by frozen cheese from (\\w+).*",
			"(\\w+) was chewed up by (\\w+).*",
			"(\\w+) was chemically cheesed by (\\w+).*",
			"(\\w+) was turned into cheese wiz by (\\w+).*",
			"(\\w+) was magically squeaked by (\\w+).*",
			"(\\w+) was corrupted by (\\w+).*"
	};

	private static final int TEAMS = 4;
	public static final int RED_TEAM = 0;
	public static final int GREEN_TEAM = 1;
	public static final int YELLOW_TEAM = 2;
	public static final int BLUE_TEAM = 3;	
	private static final String[] SCOREBOARD_PREFIXES = {"[R]", "[G]", "[Y]", "[B]"};
	private static final String[] DEFAULT_PREFIXES = {"c", "a", "e", "9"};

	private static String gameId;
	private static String[] prefixes; // color codes prefix that you are using in your hypixel mega walls settings
	private static HashMap<String, Integer>[] teamKills;
	private static ArrayList<String> deadPlayers;

	/*
	 * Resets the Killcounter and assigns it to a new game ID
	 */
	public static void ResetKillCounterTo(String gameIdIn) {

		gameId = gameIdIn;		
		prefixes = new String[TEAMS];
		teamKills = new HashMap[TEAMS]; // TODO ya pas un pb avec la définition de cet objet ?
		deadPlayers = new ArrayList<String>();

		for(int i = 0; i < TEAMS; i++) {
			prefixes[i] = DEFAULT_PREFIXES[i];
			teamKills[i] = new HashMap<String, Integer>();
		}

	}

	public static boolean processMessage(String FormattedText, String UnformattedText) {

		if (!FKCounterMod.isInMwGame()) {
			return false;
		}

		if(UnformattedText.equals(PREP_PHASE)) {
			MinecraftForge.EVENT_BUS.post(new MwGameEvent(MwGameEvent.EventType.GAME_START));
			setTeamPrefixes(); 			
		}
		
		if(areAllWithersAlive()) {
			return false;
		}

		// TODO refresh le GUI seulement quand ya un kill et sinon cache le GUI
		// TODO ajouter des particules quand un hb ou dread fait un kill ?
		// this.worldObj.spawnParticle example d'utilisation dans le EntityVillager.class

		/*
		 * Kill message detection
		 */
		for(String kill_pattern : KILL_PATTERNS) {
			Matcher killMessageMatcher = Pattern.compile(kill_pattern).matcher(UnformattedText);
			if(killMessageMatcher.matches()) {
				String killed = killMessageMatcher.group(1);
				String killer = killMessageMatcher.group(2);

				String killedTeam = FormattedText.split("\u00a7")[2].substring(0, 1);
				String killerTeam = FormattedText.split("\u00a7")[8].substring(0, 1);

				removeKilledPlayer(killed, killedTeam);

				if(isWitherDead(killedTeam)) {
					addKill(killer, killerTeam);
				}
				
				return true;

			}
		}
		return false;
	}

	@SubscribeEvent
	public void onMwGame(MwGameEvent event) {
		/*
		 * this is here to fix the bug where to killcounter doesn't work if you re-start you minecraft during a MW game and you are using different colors for the teams in your MW settings
		 */
		if (event.getType() == MwGameEvent.EventType.CONNECT) {
			setTeamPrefixes();
		}		
	}

	public static String getGameId() {
		return gameId;
	}

	public static int getKills(int team) {
		if(!isValidTeam(team)) { return 0; }

		int kills = 0;
		for(int k : teamKills[team].values()) {
			kills += k;
		}
		return kills;
	}

	public static HashMap<String, Integer> getPlayers(int team){
		if(!isValidTeam(team)) { return new HashMap<String, Integer>(); }
		return teamKills[team];
	}

	private static boolean isWitherDead(String color) {
		return !ScoreboardEvent.getMwScoreboardParser().isWitherAlive(color);
	}
	
	private static boolean areAllWithersAlive() {
		return ScoreboardEvent.getMwScoreboardParser().areAllWithersAlive();
	}

	/*
	 * Detects the color codes your are using in your mega walls settings by looking at the scoreboard/sidebartext
	 */
	private static void setTeamPrefixes() { // TODO regarder les couleurs costum utilisées et les reprendre pour le GUI fk counter ?
		for(String line : ScoreboardUtils.getFormattedSidebarText()) {
			for(int team = 0; team < TEAMS; team++) {
				if(line.contains(SCOREBOARD_PREFIXES[team])) {
					prefixes[team] = line.split("\u00a7")[1].substring(0, 1);
				}
			}
		}
	}

	private static void removeKilledPlayer(String player, String color) {
		int team = getTeamFromColor(color);
		if(!isValidTeam(team)) { return; }

		if(isWitherDead(color)) {
			teamKills[team].remove(player);
			deadPlayers.add(player);
		}

	}

	private static void addKill(String player, String color) {
		int team = getTeamFromColor(color);
		if(!isValidTeam(team)) { return; }
		if(deadPlayers.contains(player)) { return; }

		if(teamKills[team].containsKey(player)) {
			teamKills[team].put(player, teamKills[team].get(player) + 1);
		} else {
			teamKills[team].put(player, 1);
		}

		sortTeamKills(team);

	}

	private static void sortTeamKills(int team) {
		if(!isValidTeam(team)) { return; }

		teamKills[team] = teamKills[team].entrySet().stream().sorted(Entry.<String, Integer>comparingByValue().reversed())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	private static int getTeamFromColor(String color) {
		for(int team = 0; team < TEAMS; team++) {
			if(prefixes[team].equalsIgnoreCase(color))
				return team;
		}

		return -1;
	}

	private static boolean isValidTeam(int team) {
		return (team >= 0 && team < TEAMS);
	}

}
