package fr.alexdoru.mwe.utils;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.world.WorldSettings;

import java.util.Collection;
import java.util.List;

public final class NetInfoOrdering {

    private NetInfoOrdering() {}

    private static final Ordering<NetworkPlayerInfo> VANILLA_ORDERING = Ordering.from((netInfo1, netInfo2) -> {
        final ScorePlayerTeam team1 = netInfo1.getPlayerTeam();
        final ScorePlayerTeam team2 = netInfo2.getPlayerTeam();
        return ComparisonChain.start()
                .compareTrueFirst(netInfo1.getGameType() != WorldSettings.GameType.SPECTATOR, netInfo2.getGameType() != WorldSettings.GameType.SPECTATOR)
                .compare(team1 != null ? team1.getRegisteredName() : "", team2 != null ? team2.getRegisteredName() : "")
                .compare(netInfo1.getGameProfile().getName(), netInfo2.getGameProfile().getName())
                .result();
    });

    public static List<NetworkPlayerInfo> vanillaSortingCopyOf(Collection<NetworkPlayerInfo> list) {
        return VANILLA_ORDERING.sortedCopy(list);
    }

}
