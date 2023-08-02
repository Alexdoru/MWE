package fr.alexdoru.megawallsenhancementsmod.data;

import fr.alexdoru.megawallsenhancementsmod.commands.CommandReport;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class WDR implements Comparable<WDR> {

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
        this.hacks.trimToSize();
    }

    /**
     * Compares the timestamp
     */
    @Override
    public int compareTo(@Nonnull WDR wdr) {
        return Long.compare(this.timestamp, wdr.timestamp);
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
            if (s.startsWith("bhop")
                    || s.startsWith("autoblock")
                    || s.startsWith("fastbreak")
                    || s.startsWith("noslowdown")) {
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
        //        && ScoreboardTracker.isInMwGame
        //        && !ScoreboardTracker.isitPrepPhase
        //        && canBeReported(datenow)
        //        && !isOlderThanMaxAutoreport(datenow);
    }

    /**
     * Only prints the big text when you are in a mw environement but not in game
     * to prevent chat spam while playing
     */
    public boolean shouldPrintBigText(long datenow) {
        return false;
        //return ScoreboardTracker.isMWEnvironement
        //        && !ScoreboardTracker.isInMwGame
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