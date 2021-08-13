package fr.alexdoru.fkcountermod.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.scoreboard.Scoreboard;

public class ScoreboardParser {
	
	// TODO ajouter un truc pour choper la vie du dernier wither et afficher un gui
	
    private static final Pattern GAME_ID_PATTERN = Pattern.compile("\\s*\\d+/\\d+/\\d+\\s+([\\d\\w]+)\\s*", Pattern.CASE_INSENSITIVE);
    private static final Pattern MW_TITLE_PATTERN = Pattern.compile("\\s*MEGA\\sWALLS\\s*", Pattern.CASE_INSENSITIVE);
    private static final Pattern PREGAME_LOBBY_PATTERN = Pattern.compile(".+[0-9]+/[0-9]+\\s*");
    private static final Pattern WITHER_ALIVE_PATTERN = Pattern.compile("\\s*\\[.\\].*(?:HP|\u2764|\u2665).*", Pattern.CASE_INSENSITIVE);

    private ArrayList<String> aliveWithers = new ArrayList<>();
    private String gameId = null;
    private boolean hasgameended = false;
    private boolean isitPrepPhase = false;

    /* This gets run on every tick to parse the scoreboard data */
    public ScoreboardParser(Scoreboard scoreboard) {
        if (scoreboard == null) {
            return;
        }

        String title = ScoreboardUtils.getUnformattedSidebarTitle(scoreboard);
        if (!MW_TITLE_PATTERN.matcher(title).matches()) {
            return;
        }

        List<String> scoresColor = ScoreboardUtils.getFormattedSidebarText(scoreboard);
        List<String> scoresRaw = ScoreboardUtils.stripControlCodes(scoresColor);

        if (scoresRaw.size() == 0) {
            return;
        }

        Matcher matcher = GAME_ID_PATTERN.matcher(scoresRaw.get(0));
        if (!matcher.matches()) {
            return;
        }

        gameId = matcher.group(1);

        for (String line: scoresRaw) {
            if (PREGAME_LOBBY_PATTERN.matcher(line).matches()) {
                gameId = null;
                return;
            }
        }
        
        if (scoresRaw.size() < 7) {
            return;
        }
        
        if(scoresRaw.get(1).contains("Walls Fall:") || scoresRaw.get(1).contains("Gates Open:")) {
        	isitPrepPhase = true;
        }
                
        int eliminated_teams = 0;

        for (int i = 3; i < Math.min(scoresRaw.size(),7); i++) {
        	
            String line = scoresRaw.get(i);
            /*Wither alive detection*/
            if (WITHER_ALIVE_PATTERN.matcher(line).matches()) {
            	
                String lineColor = scoresColor.get(i);
                String colorCode = lineColor.split("\u00a7")[1].substring(0, 1);
                aliveWithers.add(colorCode);
                
            }
            
            if(line.contains("eliminated!")) {
            	eliminated_teams++;
            }
                                  
        }
        
        if(eliminated_teams == 3 || scoresRaw.get(1).contains("None!:")) {
        	hasgameended = true;
        }
        
    }

	public boolean isWitherAlive(String colorCode) {
        return aliveWithers.contains(colorCode);
    }
	
	public boolean areAllWithersAlive() {
		return aliveWithers.size() == 4;
	}

    public String getGameId() {
        return gameId;
    }

    public List<String> getAliveWithers() {
        return aliveWithers;
    }
    
    public boolean hasGameEnded() {
    	return hasgameended;
    }
    
    public boolean isitPrepPhase() {
    	return isitPrepPhase;
    }
   
}
