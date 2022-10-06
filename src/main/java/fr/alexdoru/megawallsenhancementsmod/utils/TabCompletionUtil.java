package fr.alexdoru.megawallsenhancementsmod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.ArrayList;
import java.util.List;

public class TabCompletionUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static List<String> getOnlinePlayersByName() {
        final List<String> players = new ArrayList<>();
        final NetHandlerPlayClient netHandler = mc.getNetHandler();
        if (netHandler != null) {
            for (final NetworkPlayerInfo networkPlayerInfo : netHandler.getPlayerInfoMap()) {
                final String playerName = networkPlayerInfo.getGameProfile().getName();
                ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(playerName);
                if (team == null) {
                    if (playerName != null) {
                        players.add(playerName);
                    }
                } else {
                    if (playerName != null && !team.getColorPrefix().contains("\u00a7k")) {
                        players.add(playerName);
                    }
                }
            }
        }
        return players;
    }

}
