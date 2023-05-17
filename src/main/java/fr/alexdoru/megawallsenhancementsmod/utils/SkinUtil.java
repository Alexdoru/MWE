package fr.alexdoru.megawallsenhancementsmod.utils;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;

public class SkinUtil {

    // Fields stolen from net.minecraft.client.resources.DefaultPlayerSkin
    /** The default skin for the Steve model. */
    private static final ResourceLocation TEXTURE_STEVE = new ResourceLocation("textures/entity/steve.png");
    /** The default skin for the Alex model. */
    private static final ResourceLocation TEXTURE_ALEX = new ResourceLocation("textures/entity/alex.png");

    public static ResourceLocation getSkin(NetworkPlayerInfo networkPlayerInfo) {
        if (networkPlayerInfo == null) {
            return TEXTURE_STEVE;
        }
        return networkPlayerInfo.getLocationSkin();
    }

}
