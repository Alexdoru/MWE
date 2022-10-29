package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.KillCounter;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.util.IChatComponent;

@SuppressWarnings("unused")
public class NetworkPlayerInfoHook {

    public static IChatComponent getDisplayName(GameProfile gameProfileIn) {
        NameUtil.transformGameProfile(gameProfileIn, false);
        return NameUtil.getTransformedDisplayName(gameProfileIn);
    }

    public static int getPlayersFinals(String playername) {
        return KillCounter.getPlayersFinals(playername);
    }

}
