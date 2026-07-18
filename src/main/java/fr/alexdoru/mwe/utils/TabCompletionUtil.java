package fr.alexdoru.mwe.utils;

import fr.alexdoru.mwe.data.AliasData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.ArrayList;
import java.util.List;

public class TabCompletionUtil {

    public static List<String> getPlayers() {
        return collectPlayers(false);
    }

    public static List<String> getPlayersAndAlias() {
        return collectPlayers(true);
    }

    private static List<String> collectPlayers(boolean collectAlias) {
        final List<String> players = new ArrayList<>();
        final NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();
        if (netHandler != null) {
            for (final NetworkPlayerInfo netInfo : netHandler.getPlayerInfoMap()) {
                final String playerName = netInfo.getGameProfile().getName();
                if (playerName != null) {
                    players.add(playerName);
                    if (collectAlias) {
                        final String alias = AliasData.getAlias(netInfo.getGameProfile().getId(), playerName);
                        if (alias != null) players.add(alias);
                    }
                }
            }
        }
        return players;
    }

}
