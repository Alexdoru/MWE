package fr.alexdoru.mwe.data;

public class ReportTimestamp {

    public final long timestamp;
    public final String serverID;
    public final String timerOnReplay;

    public ReportTimestamp(long timestamp, String serverID, String timerOnReplay) {
        this.timestamp = timestamp;
        this.serverID = serverID;
        this.timerOnReplay = timerOnReplay;
    }

}
