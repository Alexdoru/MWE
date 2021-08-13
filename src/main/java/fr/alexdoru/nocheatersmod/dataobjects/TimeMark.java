package fr.alexdoru.nocheatersmod.dataobjects;

public class TimeMark {
	
	public long timestamp;
	public String serverID;
	public String timerOnReplay;
	
	public TimeMark(long timestamp, String serverID, String timerOnReplay) {
		
		this.timestamp = timestamp;
		this.serverID = serverID;
		this.timerOnReplay = timerOnReplay;
		
	}

}
