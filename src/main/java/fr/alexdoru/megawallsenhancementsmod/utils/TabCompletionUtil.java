package fr.alexdoru.megawallsenhancementsmod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TabCompletionUtil {

    public static List<String> getOnlinePlayersByName() {

        ArrayList<String> players = new ArrayList<>();
        Collection<NetworkPlayerInfo> playerCollection = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();

        for (NetworkPlayerInfo networkPlayerInfo : playerCollection) {
            String playerName = networkPlayerInfo.getGameProfile().getName();
            if (playerName != null) {
                players.add(playerName);
            }
        }

        return players;
    }

}
