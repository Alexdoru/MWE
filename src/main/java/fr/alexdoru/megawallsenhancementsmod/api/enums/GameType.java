package fr.alexdoru.megawallsenhancementsmod.api.enums;

public enum GameType {
	
	// TODO enum
	// to use in
	// HypixelPlayerStatus
	// LoginData

	  QUAKECRAFT("Quake"),
	  PAINTBALL("Paintball"),
	  MEGA_WALLS("WALLS3", "Mega Walls"),
	  UNKNOWN("?");

	  public static GameType fromId(String id) {
	    for (GameType game : values()) {
	      if (id.equals(game.id)) {
	        return game;
	      }
	    }
	    return UNKNOWN;
	  }

	  public final String id; // game name on the API
	  public final String friendlyName; // understandable game name

	  GameType(String friendlyName) {
	    this(null, friendlyName);
	  }

	  GameType(String id, String friendlyName) {
	    if (id == null) {
	      id = name();
	    }
	    this.id = id;
	    this.friendlyName = friendlyName;
	  }

	  @Override
	  public String toString() {
	    return friendlyName;
	  }
	  
}