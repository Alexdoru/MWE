package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.network.NetworkPlayerInfo;

@SuppressWarnings("unused")
public class ScoreboardHook {

    public static void removeTeamHook(String playername) {
        transformNameTablist(playername);
    }

    public static void addPlayerToTeamHook(String playername) {
        transformNameTablist(playername);
    }

    public static void removePlayerFromTeamHook(String playername) {
        transformNameTablist(playername);
    }

    private static void transformNameTablist(String playername) {
        final NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.playerInfoMap.get(playername);
        if (networkPlayerInfo != null) {
            NameUtil.transformGameProfile(networkPlayerInfo.getGameProfile(), true);
            networkPlayerInfo.setDisplayName(NameUtil.getTransformedDisplayName(networkPlayerInfo.getGameProfile()));
        }
    }

}
