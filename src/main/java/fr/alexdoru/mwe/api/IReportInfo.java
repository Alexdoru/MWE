package fr.alexdoru.mwe.api;

import java.util.List;

public interface IReportInfo {

    /**
     * Returns an unmodifiable view of the cheats this player is reported for
     */
    List<String> getCheats();

    /**
     * Returns true if the cheats give a red icon
     */
    boolean hasRedIcon();

    /**
     * Returns true if the cheats give a yellow icon
     */
    boolean hasYellowIcon();

    /**
     * Returns the last time the player has been reported
     */
    long getTimestamp();

}
