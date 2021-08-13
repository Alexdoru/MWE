package fr.alexdoru.nocheatersmod.events;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.utils.ScoreboardUtils;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import net.minecraft.util.ChatComponentText;

public class GameInfoGrabber {

	private static final Pattern GAME_ID_PATTERN = Pattern.compile("\\s*\\d+/\\d+/\\d+\\s+([\\d\\w]+)\\s*", Pattern.CASE_INSENSITIVE);

	private static final String TIME_WALLS_FALL = "Walls Fall"; // finish 6min after replay starts
	private static final String TIME_ENRAGE_OFF = "Enrage Off"; // lasts for 8mins
	private static final String TIME_DEATHMATCH = "Deathmatch"; // lasts for 31min
	private static final String TIME_GAME_END = "Game End"; // at the end it makes 45min total

	private static long gamestarttimestamp=0;
	private static String gameID="?";

	public static String getGameIDfromscoreboard() {

		List<String> scoresRaw = ScoreboardUtils.getUnformattedSidebarText();

		if (scoresRaw == null || scoresRaw.size() == 0) {
			return"?";
		}

		Matcher matcher = GAME_ID_PATTERN.matcher(scoresRaw.get(0));
		if (!matcher.matches()) {
			return"?";
		}

		return matcher.group(1);

	}
	
	/**
	 * Prints scoreboard in chat
	 */
	public static void debugGetScoreboard() {

		List<String> scoresColor = ScoreboardUtils.getFormattedSidebarText();

		if (scoresColor == null || scoresColor.size() == 0) {
			ChatUtil.addChatMessage(new ChatComponentText("There are no active scoreboards in this world."));
			return;
		}

		for (String sidebarScore : scoresColor) {
			ChatUtil.addChatMessage(new ChatComponentText(sidebarScore));
		}

	}
	
	public static void saveinfoOnGameStart() {
		gamestarttimestamp = (new Date()).getTime() + 1000L;
		gameID = getGameIDfromscoreboard();	
	}

	/**
	 * Returns the time since the start of the game as a string. example : "24min54sec"
	 * 
	 * @param timestamp - date time in millisecond when you press the timestamp keybind
	 * @param serverID - current serverID when you press the timestamp keybind
	 * @return 
	 */
	public static String getTimeSinceGameStart(long timestamp, String serverID, int delay) { 

		if(FKCounterMod.isInMwGame()) {

			List<String> scoresRaw = ScoreboardUtils.getUnformattedSidebarText();

			if (scoresRaw == null || scoresRaw.size() < 2) {
				return "?";
			}

			String time_line = scoresRaw.get(1);			
			String [] split = time_line.split(":");

			int score_sec = 60*Integer.parseInt(split[1].replace(" ", "")) + Integer.parseInt(split[2].replace(" ", ""));			
			int sec_since_start = 0;

			if(split[0].equals(TIME_WALLS_FALL)) {

				sec_since_start = 0 + (6*60 - score_sec);

			} else if(split[0].equals(TIME_ENRAGE_OFF)) {

				sec_since_start = 6*60 + (8*60 - score_sec);

			} else if(split[0].equals(TIME_DEATHMATCH)) {

				if (getstoredGameID() != "?" && getstoredGameID().equals(serverID)) {

					long long_sec_since_start = (timestamp > getstoredTimestamp() ? timestamp - getstoredTimestamp() : 0L )/1000; //en secondes
					return String.valueOf(long_sec_since_start / 60) + "min"+ String.valueOf(long_sec_since_start%60) + "sec";	

				} else {
					return "?";
				}

			} else if(split[0].equals(TIME_GAME_END)) {

				sec_since_start = (45*60 - score_sec);

			} 
			
			int result = sec_since_start > delay ? sec_since_start-delay : 0 ;
			return String.valueOf(result/60) + "min"+ String.valueOf(result%60) + "sec";					

		} else if (getstoredGameID() != "?" && getstoredGameID().equals(serverID)) {

			long sec_since_start = (timestamp > getstoredTimestamp() ? timestamp - getstoredTimestamp() : 0L )/1000; //en secondes			
			return String.valueOf(sec_since_start / 60) + "min"+ String.valueOf(sec_since_start%60) + "sec";	

		}

		return "?";
	}

	public static String getstoredGameID() {
		return gameID;
	}

	public static long getstoredTimestamp() {
		return gamestarttimestamp;
	}

}
