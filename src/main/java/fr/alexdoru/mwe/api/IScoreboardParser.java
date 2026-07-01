package fr.alexdoru.mwe.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface IScoreboardParser {

    /**
     * Turns true from the moment the player gets tp to the cage to the end of the game
     * False in the pre game lobby
     */
    boolean isInMwGame();

    /** True in the pre game lobby in mega walls */
    boolean isPreGameLobby();

    /** True during the preparation phase of a mega walls game */
    boolean isPrepPhase();

    /** True in mega walls lobbys, games etc */
    boolean isMWEnvironement();

    /** True when in the Replay Mode, including Atlas */
    boolean isReplayMode();

    /** True when in the Atlas Mode */
    boolean isAtlasMode();

    /** True when in a mega walls replay **/
    boolean isMWReplay();

    /** True when is Skyblock */
    boolean isInSkyblock();

    /** Returns the ID of the current server */
    String getServerID();

    /** Returns the list of alive withers in a Mega Walls game, each element is a color character */
    @NotNull
    @UnmodifiableView
    List<String> getAliveWithers();

    /** Returns the amount of alive withers in a Mega Walls game */
    int getWitherCount();

    /** Returns true during Mega walls deathmatch */
    boolean isDeathmatch();

}
