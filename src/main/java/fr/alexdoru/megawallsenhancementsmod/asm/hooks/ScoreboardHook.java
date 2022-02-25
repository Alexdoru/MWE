package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.network.NetworkPlayerInfo;

public class ScoreboardHook {

    public static void transformNameTablist(String playername) {
        NetworkPlayerInfo networkPlayerInfo = NetHandlerPlayClientHook.playerInfoMap.get(playername);
        if (networkPlayerInfo != null) {
            NameUtil.transformGameProfile(networkPlayerInfo.getGameProfile(), true);
            networkPlayerInfo.setDisplayName(NameUtil.getTransformedDisplayName(networkPlayerInfo.getGameProfile()));
        }
    }

}
