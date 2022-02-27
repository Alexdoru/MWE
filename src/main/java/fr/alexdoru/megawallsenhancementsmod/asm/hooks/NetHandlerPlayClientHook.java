package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import com.google.common.collect.EvictingQueue;
import fr.alexdoru.megawallsenhancementsmod.data.MWPlayerData;
import fr.alexdoru.megawallsenhancementsmod.data.StringLong;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.HashMap;

public class NetHandlerPlayClientHook {

    public static final HashMap<String, NetworkPlayerInfo> playerInfoMap = new HashMap<>();
    @SuppressWarnings("UnstableApiUsage")
    private static final EvictingQueue<StringLong> latestDisconnected = EvictingQueue.create(10);

    public static void putPlayerInMap(String playerName, NetworkPlayerInfo networkplayerinfo) {
        if (playerName != null && !NameUtil.filterNPC(networkplayerinfo.getGameProfile().getId())) {
            playerInfoMap.put(playerName, networkplayerinfo);
        }
    }

    public static void removePlayerFromMap(Object o) {
        if (o instanceof NetworkPlayerInfo) {
            String playerName = ((NetworkPlayerInfo) o).getGameProfile().getName();
            if (playerName != null) {
                playerInfoMap.remove(playerName);
                latestDisconnected.add(new StringLong(System.currentTimeMillis(), playerName));
            }
            MWPlayerData.dataCache.remove(((NetworkPlayerInfo) o).getGameProfile().getId());
        }
    }

    public static void clearPlayerMap() {
        playerInfoMap.clear();
        latestDisconnected.clear();
        MWPlayerData.dataCache.clear();
    }

    public static String getRecentlyDisconnectedPlayers() {
        String str = "";
        long timenow = System.currentTimeMillis();
        for (StringLong stringLong : latestDisconnected) {
            if (timenow - stringLong.timestamp <= 2000) {
                str = str + " " + stringLong.message;
            }
        }
        return str;
    }

}
