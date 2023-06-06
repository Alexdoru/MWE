package fr.alexdoru.megawallsenhancementsmod.utils;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

public class SkinUtil {

    public static ResourceLocation getSkin(NetworkPlayerInfo networkPlayerInfo) {
        if (networkPlayerInfo == null) {
            return DefaultPlayerSkin.getDefaultSkinLegacy();
        }
        return networkPlayerInfo.getLocationSkin();
    }

}
