package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.HashMap;

public class NetHandlerPlayClientHook {

    public static final HashMap<String, NetworkPlayerInfo> playerInfoMap = new HashMap<>();

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
            }
        }
    }

    public static void clearPlayerMap() {
        playerInfoMap.clear();
    }

}
