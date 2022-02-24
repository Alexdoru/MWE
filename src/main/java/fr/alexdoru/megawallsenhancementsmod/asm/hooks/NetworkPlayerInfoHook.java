package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.util.IChatComponent;

public class NetworkPlayerInfoHook {

    public static IChatComponent getDisplayName(IChatComponent displayNameIn, GameProfile gameProfileIn) {
        NameUtil.transformGameProfile(gameProfileIn, false);
        return displayNameIn == null ? NameUtil.getTransformedDisplayName(gameProfileIn) : displayNameIn;
    }

}
