package fr.alexdoru.mwe.utils;

import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.ArrayList;
import java.util.List;

public class TabCompletionUtil {

    public static List<String> getOnlinePlayersByName() {
        final Minecraft mc = Minecraft.getMinecraft();
        final List<String> players = new ArrayList<>();
        final NetHandlerPlayClient netHandler = mc.getNetHandler();
        if (netHandler != null) {
            for (final NetworkPlayerInfo netInfo : netHandler.getPlayerInfoMap()) {
                final String playerName = netInfo.getGameProfile().getName();
                final ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(playerName);
                if (team == null) {
                    if (playerName != null) {
                        players.add(playerName);
                    }
                } else {
                    if (playerName != null && (!team.getColorPrefix().contains("§k") || MWEConfig.deobfNamesInTab)) {
                        players.add(playerName);
                    }
                }
            }
        }
        return players;
    }

}
