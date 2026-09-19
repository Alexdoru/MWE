package fr.alexdoru.mwe.api;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * Implementing this interface and registering it via
 * {@link fr.alexdoru.mwe.api.MWEApi.Hud#registerSquadHUDExtraRenderer(ISquadInfoRenderer)}
 * allows you to render arbitrary extra information next to each player line of the Squad Health HUD.
 */
public interface ISquadInfoRenderer {

    /**
     * The priority of this renderer
     */
    default int getPriority() {
        return 0;
    }

    /** @deprecated Use {@link #getWidth(int, NetworkPlayerInfo, EntityPlayer)} */
    @Deprecated
    default int getWidth(@NotNull NetworkPlayerInfo netInfo, @Nullable EntityPlayer entityPlayer) {return 0;}

    /**
     * @param listIndex    the index of the squad member in the list
     * @param netInfo      the NetworkPlayerInfo of the squad member on this line
     * @param entityPlayer the corresponding EntityPlayer if loaded in the world, may be null
     * @return the width in pixels needed to render this info for the given player,
     * or 0 if nothing should be rendered for this player
     */
    default int getWidth(int listIndex, @NotNull NetworkPlayerInfo netInfo, @Nullable EntityPlayer entityPlayer) {return getWidth(netInfo, entityPlayer);}

    /** @deprecated Use {@link #render(int, NetworkPlayerInfo, EntityPlayer, int, int, int, int)} */
    @Deprecated
    default void render(@NotNull NetworkPlayerInfo netInfo, @Nullable EntityPlayer entityPlayer, int x, int y, int reservedWidth) {}

    /**
     * Renders the extra info. Implementations should stay within [x, x + reservedWidth] and [y, y + reservedHeight].
     *
     * @param listIndex      the index of the squad member in the list
     * @param netInfo        the NetworkPlayerInfo of the squad member on this line
     * @param entityPlayer   the corresponding EntityPlayer if loaded in the world, may be null
     * @param x              left edge of the column reserved for this renderer
     * @param y              y position of the line
     * @param reservedWidth  the max width reserved for this renderer across all displayed players,
     *                       as previously returned by {@link #getWidth(int, NetworkPlayerInfo, EntityPlayer)}
     * @param reservedHeight the max height reserved for this row
     */
    default void render(int listIndex, @NotNull NetworkPlayerInfo netInfo, @Nullable EntityPlayer entityPlayer, int x, int y, int reservedWidth, int reservedHeight) {render(netInfo, entityPlayer, x, y, reservedWidth);}

    /**
     * Use this to clean information created in {@link #processData(int, List, List)}
     */
    default void clearData() {}

    /**
     * @param listSize         the size of the list ({@code 0} if empty)
     * @param netInfoList      the NetworkPlayerInfo of each player
     * @param entityPlayerList a list containing either the corresponding EntityPlayer if loaded in the world or null
     */
    default void processData(int listSize, @Unmodifiable List<NetworkPlayerInfo> netInfoList, @Unmodifiable List<EntityPlayer> entityPlayerList) {}

}
