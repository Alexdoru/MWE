package fr.alexdoru.nocheatersmod.data;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;

import java.util.ArrayList;

public class WDR {

    public long timestamp;
    public final ArrayList<String> hacks;

    public WDR(long timestamp, ArrayList<String> hacks) {
        this.timestamp = timestamp;
        this.hacks = hacks;
    }

    /**
     * Compares the timestamp of timestamped reports
     */
    public int compareTo(WDR wdr) {
        return compare(this, wdr);
    }

    /**
     * Compares the timestamp of timestamped reports
     */
    private static int compare(WDR wdr1, WDR wdr2) {

        long x = Long.parseLong(wdr1.hacks.get(3));
        long y = Long.parseLong(wdr2.hacks.get(3));

        return Long.compare(x, y);
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