package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import com.google.common.collect.EvictingQueue;
import fr.alexdoru.megawallsenhancementsmod.data.MWPlayerData;
import fr.alexdoru.megawallsenhancementsmod.data.StringLong;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.HashMap;

public class NetHandlerPlayClientHook {

    public static final HashMap<String, NetworkPlayerInfo> playerInfoMap = new HashMap<>();
    @SuppressWarnings("UnstableApiUsage")
    private static final EvictingQueue<StringLong> latestDisconnected = EvictingQueue.create(10);

    public static void putPlayerInMap(String playerName, NetworkPlayerInfo networkplayerinfo) {
        playerInfoMap.put(playerName, networkplayerinfo);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void removePlayerFromMap(Object o) {
        if (o instanceof NetworkPlayerInfo) {
            String playerName = ((NetworkPlayerInfo) o).getGameProfile().getName();
            playerInfoMap.remove(playerName);
            latestDisconnected.add(new StringLong(System.currentTimeMillis(), playerName));
            MWPlayerData.dataCache.remove(((NetworkPlayerInfo) o).getGameProfile().getId());
        }
    }

    public static void clearPlayerMap() {
        playerInfoMap.clear();
        latestDisconnected.clear();
        MWPlayerData.dataCache.clear();
    }

    public static String getRecentlyDisconnectedPlayers() {
        StringBuilder str = new StringBuilder();
        long timenow = System.currentTimeMillis();
        for (StringLong stringLong : latestDisconnected) {
            if (stringLong.message != null && timenow - stringLong.timestamp <= 2000) {
                str.append(" ").append(stringLong.message);
            }
        }
        return str.toString();
    }

}
