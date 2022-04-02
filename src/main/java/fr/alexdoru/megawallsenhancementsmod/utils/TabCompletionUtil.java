package fr.alexdoru.megawallsenhancementsmod.utils;

import fr.alexdoru.fkcountermod.FKCounterMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.ArrayList;
import java.util.List;

public class TabCompletionUtil {

    public static List<String> getOnlinePlayersByName() {
        List<String> players = new ArrayList<>();
        if (FKCounterMod.isitPrepPhase) {
            return players;
        }
        final NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();
        if (netHandler != null) {
            for (NetworkPlayerInfo networkPlayerInfo : netHandler.getPlayerInfoMap()) {
                String playerName = networkPlayerInfo.getGameProfile().getName();
                if (playerName != null) {
                    players.add(playerName);
                }
            }
        }
        return players;
    }

}
