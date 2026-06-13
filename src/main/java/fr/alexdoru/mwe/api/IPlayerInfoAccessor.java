package fr.alexdoru.mwe.api;

import fr.alexdoru.mwe.api.enums.MWClass;
import org.jetbrains.annotations.Nullable;

public interface IPlayerInfoAccessor {

    /**
     * Returns the color code from the Team of this player,
     * as defined by the minecraft Teams.
     * May return '\0' if the player doesn't have a Team
     */
    char getPlayerTeamColor();

    /**
     * Returns the color used to render special player elements, such as the hitbox,
     * hurt color, colored leather armor, this color doesn't necessarilly match the Team Color
     */
    int getPlayerSpecialRenderColor();

    /**
     * Set the color used to render special player elements, such as the hitbox,
     * hurt color, colored leather armor, this color doesn't necessarilly match the Team Color
     */
    void setPlayerSpecialRenderColor(int color);

    /**
     * Returns the Mega Walls class that this player is using,
     * may be null outside Mega Walls.
     */
    @Nullable
    MWClass getMWClass();

}
