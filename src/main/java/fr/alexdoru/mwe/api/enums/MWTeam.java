package fr.alexdoru.mwe.api.enums;

import fr.alexdoru.mwe.features.FinalKillCounter;

import static fr.alexdoru.mwe.features.FinalKillCounter.*;

public enum MWTeam {

    BLUE,
    GREEN,
    RED,
    YELLOW;

    /**
     * Returns the name of the team with the appropriate chat color
     */
    public String formattedName() {
        return this.getColorPrefix() + this.name();
    }

    /**
     * Returns the chat color code for the team according to the colorblind setting set by the player
     */
    public String getColorPrefix() {
        switch (this) {
            case BLUE:
                return FinalKillCounter.getColorPrefixOfTeam(BLUE_TEAM);
            case GREEN:
                return FinalKillCounter.getColorPrefixOfTeam(GREEN_TEAM);
            case RED:
                return FinalKillCounter.getColorPrefixOfTeam(RED_TEAM);
            case YELLOW:
                return FinalKillCounter.getColorPrefixOfTeam(YELLOW_TEAM);
        }
        throw new IllegalStateException();
    }
}
