package fr.alexdoru.fkcountermod.events;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.gui.FKCounterGui;
import fr.alexdoru.fkcountermod.utils.ScoreboardUtils;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KillCounter {

	private static final String PREP_PHASE = "Prepare your defenses!";
	private static final String[] KILL_MESSAGES = {
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

	private static Pattern[] KILL_PATTERNS;
	private static final int TEAMS = 4;
	public static final int RED_TEAM = 0;
	public static final int GREEN_TEAM = 1;
	public static final int YELLOW_TEAM = 2;
	public static final int BLUE_TEAM = 3;	
	private static final String[] SCOREBOARD_PREFIXES = {"[R]", "[G]", "[Y]", "[B]"};
	private static final String[] DEFAULT_PREFIXES = {"c", "a", "e", "9"}; // RED GREEN YELLOW BLUE

	private static String gameId;
	private static String[] prefixes; // color codes prefix that you are using in your hypixel mega walls settings
	private static HashMap<String, Integer>[] teamKillsArray;
	private static ArrayList<String> deadPlayers;

	public KillCounter() {
		KILL_PATTERNS = new Pattern[KILL_MESSAGES.length];	
		for(int i = 0 ; i < KILL_MESSAGES.length; i++) {
			KILL_PATTERNS[i] =	Pattern.compile(KILL_MESSAGES[i]);
		}
		FKCounterGui.updateDisplayText();
	}

	/*
	 * Resets the Killcounter and assigns it to a new game ID
	 */
	@SuppressWarnings("unchecked")
	public static void ResetKillCounterTo(String gameIdIn) {

		gameId = gameIdIn;		
		prefixes = new String[TEAMS];
		teamKillsArray = new HashMap[TEAMS];
		deadPlayers = new ArrayList<>();

		for(int i = 0; i < TEAMS; i++) {
			prefixes[i] = DEFAULT_PREFIXES[i];
			teamKillsArray[i] = new HashMap<>();
		}
		FKCounterGui.updateDisplayText();

	}

	public static boolean processMessage(String FormattedText, String UnformattedText) {

		if (!FKCounterMod.isInMwGame()) {
			return false;
		}

		if(UnformattedText.equals(PREP_PHASE)) {
			MinecraftForge.EVENT_BUS.post(new MwGameEvent(MwGameEvent.EventType.GAME_START));
			setTeamPrefixes(); 
			return true;
		}

		if(areAllWithersAlive()) {
			return false;
		}

		// TODO ajouter des particules quand un hb ou dread fait un kill ?
		// this.worldObj.spawnParticle example d'utilisation dans le EntityVillager.class

		/*
		 * Kill message detection
		 */
		for(Pattern kill_pattern : KILL_PATTERNS) {
			Matcher killMessageMatcher = kill_pattern.matcher(UnformattedText);
			if(killMessageMatcher.matches()) {

				String killed = killMessageMatcher.group(1);
				String killer = killMessageMatcher.group(2);
				String[] split = FormattedText.split("\u00a7");

				if(split.length >= 7) {
					
					String killedTeam = split[2].substring(0, 1);
					String killerTeam = split[6].substring(0, 1);

					removeKilledPlayer(killed, killedTeam);

					if(isWitherDead(killedTeam)) {
						addKill(killer, killerTeam);
						sortTeamKills(killerTeam);
						sortTeamKills(killedTeam);
					}
					
					FKCounterGui.updateDisplayText();

				}

				return true;

			}
		}
		return false;
	}

	@SubscribeEvent
	public void onMwGame(MwGameEvent event) {
		/*
		 * this is here to fix the bug where the killcounter doesn't work if you re-start your minecraft during a game of MW
		 * or if you changed your colors for the teams in your MW settings and rejoined the game
		 */
		if (event.getType() == MwGameEvent.EventType.CONNECT) {
			setTeamPrefixes();
		}
	}

	public static String getGameId() {
		return gameId;
	}

	public static int getKills(int team) {
		if(isNotValidTeam(team)) {return 0;}

		int kills = 0;
		for(int k : teamKillsArray[team].values()) {
			kills += k;
		}
		return kills;
	}
	
	public static HashMap<Integer, Integer> getSortedTeamKillsMap() {	
		HashMap<Integer, Integer> hashmap = new HashMap<>();
		hashmap.put(RED_TEAM, getKills(RED_TEAM));
		hashmap.put(GREEN_TEAM, getKills(GREEN_TEAM));
		hashmap.put(YELLOW_TEAM, getKills(YELLOW_TEAM));
		hashmap.put(BLUE_TEAM, getKills(BLUE_TEAM));				
		return sortByDecreasingValue2(hashmap);
	}

	public static HashMap<String, Integer> getPlayers(int team){
		if(isNotValidTeam(team)) {return new HashMap<>();}
		return teamKillsArray[team];
	}

	private static boolean isWitherDead(String color) {
		return !ScoreboardEvent.getMwScoreboardParser().isWitherAlive(color);
	}

	private static boolean areAllWithersAlive() {
		return ScoreboardEvent.getMwScoreboardParser().areAllWithersAlive();
	}

	/*
	 * Detects the color codes you are using in your mega walls settings by looking at the scoreboard/sidebartext
	 */
	private static void setTeamPrefixes() { 
		for(String line : ScoreboardUtils.getFormattedSidebarText()) {
			for(int team = 0; team < TEAMS; team++) {
				if(line.contains(SCOREBOARD_PREFIXES[team])) {
					prefixes[team] = line.split("\u00a7")[1].substring(0, 1);
				}
			}
		}
		FKCounterGui.updateDisplayText();
	}

	private static void removeKilledPlayer(String player, String color) {
		int team = getTeamFromColor(color);
		if(isNotValidTeam(team)) {return;}

		if(isWitherDead(color)) {
			teamKillsArray[team].remove(player);
			deadPlayers.add(player);
		}

	}

	private static void addKill(String player, String color) {
		int team = getTeamFromColor(color);
		if(isNotValidTeam(team)) {return;}
		if(deadPlayers.contains(player)) {return;}

		if(teamKillsArray[team].containsKey(player)) {
			teamKillsArray[team].put(player, teamKillsArray[team].get(player) + 1);
		} else {
			teamKillsArray[team].put(player, 1);
		}
	}

	private static void sortTeamKills(String color) {
		int team = getTeamFromColor(color);
		if(isNotValidTeam(team)) {return;}
		teamKillsArray[team] = sortByDecreasingValue1(teamKillsArray[team]);
	}

	private static int getTeamFromColor(String color) {
		for(int team = 0; team < TEAMS; team++) {
			if(prefixes[team].equalsIgnoreCase(color)) {
				return team;
			}
		}
		return -1;
	}
	
	public static String getColorPrefixFromTeam(int team) {
		return "\u00a7" + prefixes[team];
	}
	
	public static String getTeamNameFromTeam(int team) {
		switch(team) {
		case 0:
			return "Red";
		case 1:
			return "Green";
		case 2:
			return "Yellow";
		case 3:
			return "Blue";
		default:
			return "?";	
		}
	}
	
	/*
	 * returns the name of the player from the team that has the highest finals
	 */
	public static Tuple getHighestFinalsPlayerOfTeam(int team) {
		
		HashMap<String, Integer> teamkills = teamKillsArray[team];
		
		Iterator<Map.Entry<String, Integer>> iterator = teamkills.entrySet().iterator();
	    if (iterator.hasNext()) {
	      Map.Entry<String, Integer> entry = iterator.next();
			//noinspection unchecked
			return new Tuple(entry.getKey(), entry.getValue());
	    }
	    return null;
	}	

	private static boolean isNotValidTeam(int team) {
		return (team < 0 || team >= TEAMS);
	}

	private static HashMap<String, Integer> sortByDecreasingValue1(HashMap<String, Integer> hashmapIn) {
		List<Map.Entry<String, Integer>> list = new LinkedList<>(hashmapIn.entrySet());
		list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
		HashMap<String, Integer> temp = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}
	
	private static HashMap<Integer, Integer> sortByDecreasingValue2(HashMap<Integer, Integer> hashmapIn) {
		List<Map.Entry<Integer, Integer>> list = new LinkedList<>(hashmapIn.entrySet());
		list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
		HashMap<Integer, Integer> temp = new LinkedHashMap<>();
		for (Map.Entry<Integer, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

}
