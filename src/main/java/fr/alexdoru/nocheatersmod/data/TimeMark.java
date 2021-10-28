package fr.alexdoru.nocheatersmod.data;

public class TimeMark {
	
	public final long timestamp;
	public final String serverID;
	public final String timerOnReplay;
	
	public TimeMark(long timestamp, String serverID, String timerOnReplay) {
		
		this.timestamp = timestamp;
		this.serverID = serverID;
		this.timerOnReplay = timerOnReplay;
		
	}

}
