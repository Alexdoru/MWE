package fr.alexdoru.nocheatersmod.data;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;

import java.util.ArrayList;

public class WDR {

    public final ArrayList<String> hacks;
    public long timestamp;

    public WDR(long timestamp, ArrayList<String> hacks) {
        this.timestamp = timestamp;
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
        return datenow - this.timestamp - ConfigHandler.timeBetweenReports > 0;
    }

    public boolean canBeAutoreported(long datenow) {
        return ConfigHandler.toggleautoreport
                && FKCounterMod.isInMwGame
                && !FKCounterMod.isitPrepPhase
                && this.canBeReported(datenow)
                && 0 < ConfigHandler.timeAutoReport - datenow + this.timestamp;
    }

}