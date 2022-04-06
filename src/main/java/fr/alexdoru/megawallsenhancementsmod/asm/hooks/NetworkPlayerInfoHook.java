package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.util.IChatComponent;

@SuppressWarnings("unused")
public class NetworkPlayerInfoHook {

    public static IChatComponent getDisplayName(IChatComponent displayNameIn, GameProfile gameProfileIn) {
        NameUtil.transformGameProfile(gameProfileIn, false);
        return displayNameIn == null ? NameUtil.getTransformedDisplayName(gameProfileIn) : displayNameIn;
    }

    public static int getPlayersFinals(String playername) {
        return KillCounter.getPlayersFinals(playername);
    }

}
