package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.data.NetPlayerInfoTracker;
import net.minecraft.client.network.NetworkPlayerInfo;

public class NetHandlerPlayClientHook_PlayerMapTracker {

    public static void onInit() {
        NetPlayerInfoTracker.clearData();
    }

    public static void onAddPlayerPacket(NetworkPlayerInfo netInfo) {
        NetPlayerInfoTracker.addPlayer(netInfo);
    }

    public static Object onRemovePlayerPacket(Object o) {
        return NetPlayerInfoTracker.removePlayer(o);
    }

}
