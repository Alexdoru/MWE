package fr.alexdoru.mwe.api.enums;

import fr.alexdoru.mwe.features.FinalKillCounter;

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
        return FinalKillCounter.getColorPrefixOfTeam(this);
    }

    public String getName() {
        switch (this) {
            case BLUE:
                return "Blue";
            case GREEN:
                return "Green";
            case RED:
                return "Red";
            case YELLOW:
                return "Yellow";
        }
        throw new IllegalStateException();
    }
}
