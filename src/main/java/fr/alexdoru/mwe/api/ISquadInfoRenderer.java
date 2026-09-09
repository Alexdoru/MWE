package fr.alexdoru.mwe.api;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows rendering arbitrary extra information next to each player line of the Squad Health HUD
 */
public interface ISquadInfoRenderer {

    /**
     * @param netInfo      the NetworkPlayerInfo of the squad member on this line
     * @param entityPlayer the corresponding EntityPlayer if loaded in the world, may be null
     * @return the width in pixels needed to render this info for the given player,
     * or 0 if nothing should be rendered for this player
     */
    int getWidth(@NotNull NetworkPlayerInfo netInfo, @Nullable EntityPlayer entityPlayer);

    /**
     * Renders the extra info. Implementations should stay within [x, x + reservedWidth].
     *
     * @param netInfo       the NetworkPlayerInfo of the squad member on this line
     * @param entityPlayer  the corresponding EntityPlayer if loaded in the world, may be null
     * @param x             left edge of the column reserved for this renderer
     * @param y             y position of the line
     * @param reservedWidth the max width reserved for this renderer across all displayed players,
     *                      as previously returned by {@link #getWidth}
     */
    void render(@NotNull NetworkPlayerInfo netInfo, @Nullable EntityPlayer entityPlayer, int x, int y, int reservedWidth);

}
