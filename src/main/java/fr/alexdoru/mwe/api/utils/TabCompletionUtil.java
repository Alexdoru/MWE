package fr.alexdoru.mwe.api.utils;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.data.AliasDataManager;
import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TabCompletionUtil {

    private TabCompletionUtil() {}

    public static List<String> getAlias() {
        return collectPlayers(true, false);
    }

    public static List<String> getPlayers() {
        return collectPlayers(false, true);
    }

    public static List<String> getPlayersAndAlias() {
        return collectPlayers(true, true);
    }

    /**
     * When in a Mega Walls game will return the names of the dead players found in the final killcounter
     */
    public static List<String> getPlayersAndAliasAndDeadPlayers() {
        if (!ScoreboardTracker.isInMwGame() || ScoreboardTracker.isPrepPhase() || MWE.INSTANCE().getFinalKillCounter() == null) {
            return getPlayersAndAlias();
        }
        final Set<String> players = new HashSet<>(getPlayersAndAlias());
        final FinalKillCounter fkCounter = MWE.INSTANCE().getFinalKillCounter();
        players.addAll(fkCounter.getPlayersInThisGame());
        return new ArrayList<>(players);
    }

    private static List<String> collectPlayers(boolean collectAlias, boolean collectPlayers) {
        final List<String> players = new ArrayList<>();
        final NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();
        if (netHandler != null) {
            for (final NetworkPlayerInfo netInfo : netHandler.getPlayerInfoMap()) {
                final String playerName = netInfo.getGameProfile().getName();
                if (playerName != null) {
                    if (collectPlayers) {
                        players.add(playerName);
                    }
                    if (collectAlias) {
                        final String alias = AliasDataManager.getAlias(netInfo.getGameProfile().getId(), playerName);
                        if (alias != null) players.add(alias);
                    }
                }
            }
        }
        return players;
    }

}
