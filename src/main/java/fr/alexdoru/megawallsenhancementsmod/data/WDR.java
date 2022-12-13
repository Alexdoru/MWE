package fr.alexdoru.megawallsenhancementsmod.data;

import fr.alexdoru.megawallsenhancementsmod.commands.CommandReport;

import java.util.ArrayList;

public class WDR {

    public static final String NICK = "nick";
    public static final String IGNORED = "ignored";
    public static final long TIME_BETWEEN_AUTOREPORT = 120L * 60L * 1000L; //120 minutes
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

    public boolean transformName() {
        return !(hacks.size() == 1 && hacks.contains(IGNORED));
    }

    public boolean shouldPutGrayIcon() {
        return false;
        //return ConfigHandler.toggleAutoreport
        //        && ConfigHandler.stopAutoreportAfterWeek
        //        && isOlderThanMaxAutoreport((new Date()).getTime());
    }

    public boolean shouldPutRedIcon() {
        for (final String s : this.hacks) {
            if ("bhop".equalsIgnoreCase(s) || "autoblock".equalsIgnoreCase(s) || "fastbreak".equalsIgnoreCase(s) || "noslowdown".equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOnlyIgnored() {
        return hacks.size() == 1 && hacks.contains(IGNORED);
    }

    public boolean isNicked() {
        return hacks.contains(NICK);
    }

    public boolean isIgnored() {
        return hacks.contains(IGNORED);
    }

    public boolean canBeReported(long datenow) {
        return false;
        //return datenow - timestamp - TIME_BETWEEN_AUTOREPORT > 0;
    }

    public boolean isOlderThanMaxAutoreport(long datenow) {
        return datenow - timeLastManualReport - TIME_MAX_AUTOREPORT > 0;
    }

    public boolean canBeAutoreported(long datenow) {
        return false;
        //return ConfigHandler.toggleAutoreport
        //        && FKCounterMod.isInMwGame
        //        && !FKCounterMod.isitPrepPhase
        //        && canBeReported(datenow)
        //        && !isOlderThanMaxAutoreport(datenow);
    }

    /**
     * Only prints the big text when you are in a mw environement but not in game
     * to prevent chat spam while playing
     */
    public boolean shouldPrintBigText(long datenow) {
        return false;
        //return FKCounterMod.isMWEnvironement
        //        && !FKCounterMod.isInMwGame
        //        && ConfigHandler.toggleAutoreport
        //        && ConfigHandler.stopAutoreportAfterWeek
        //        && isOlderThanMaxAutoreport(datenow);
    }

    public boolean hasValidCheats() {
        for (final String cheat : hacks) {
            if (CommandReport.cheatsList.contains(cheat)) {
                return true;
            }
        }
        return false;
    }

    public String hacksToString() {
        final StringBuilder cheats = new StringBuilder();
        for (final String hack : hacks) {
            cheats.append(" ").append(hack);
        }
        return cheats.toString();
    }

}