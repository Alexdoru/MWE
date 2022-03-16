package fr.alexdoru.nocheatersmod.data;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;

import java.util.ArrayList;

public class WDR {

    private static final long TIME_BETWEEN_AUTOREPORT = 40L * 60L * 1000L; //40 minutes
    private static final long TIME_MAX_AUTOREPORT = 7L * 24L * 60L * 60L * 1000L; //1 week

    public long timestamp;
    public long timeLastManualReport;
    public final ArrayList<String> hacks;

    public WDR(long timestamp, long timeLastManualReport, ArrayList<String> hacks) {
        this.timestamp = timestamp;
        this.timeLastManualReport = timeLastManualReport;
        this.hacks = hacks;
    }

    /**
     * Compares the timestamp
     */
    private static int compare(WDR wdr1, WDR wdr2) {
        return Long.compare(wdr1.timestamp, wdr2.timestamp);
    }

    /**
     * Compares the timestamp
     */
    public int compareTo(WDR wdr) {
        return compare(this, wdr);
    }

    public int compareToInvert(WDR wdr) {
        return compare(wdr, this);
    }

    public boolean canBeReported(long datenow) {
        return datenow - timestamp - TIME_BETWEEN_AUTOREPORT > 0;
    }

    public boolean canBeAutoreported(long datenow) {
        return ConfigHandler.toggleautoreport
                && FKCounterMod.isInMwGame
                && !FKCounterMod.isitPrepPhase
                && canBeReported(datenow)
                && 0 < TIME_MAX_AUTOREPORT - datenow + timestamp;
    }

}