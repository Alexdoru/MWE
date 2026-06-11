package fr.alexdoru.mwe.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.ArrayList;
import java.util.List;

public class TabCompletionUtil {

    public static List<String> getOnlinePlayersByName() {
        final List<String> players = new ArrayList<>();
        final NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();
        if (netHandler != null) {
            for (final NetworkPlayerInfo networkPlayerInfo : netHandler.getPlayerInfoMap()) {
                final String playerName = networkPlayerInfo.getGameProfile().getName();
                if (playerName != null) {
                    players.add(playerName);
                }
            }
        }
        return players;
    }

}
