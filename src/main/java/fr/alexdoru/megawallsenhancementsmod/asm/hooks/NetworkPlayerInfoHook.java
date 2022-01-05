package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.util.IChatComponent;

public class NetworkPlayerInfoHook {

    public static IChatComponent getDisplayName(IChatComponent displayNameIn, GameProfile gameProfileIn) {
        if (displayNameIn == null) {
            return NameUtil.getTransformedDisplayName(gameProfileIn);
        }
        return null;
    }

}
