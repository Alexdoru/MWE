package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.megawallsenhancementsmod.features.FinalKillCounter;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.util.IChatComponent;

@SuppressWarnings("unused")
public class NetworkPlayerInfoHook {

    public static IChatComponent getDisplayName(GameProfile gameProfileIn) {
        return NameUtil.getMWPlayerData(gameProfileIn, false).displayName;
    }

    public static int getPlayersFinals(String playername) {
        return FinalKillCounter.getPlayersFinals(playername);
    }

}
