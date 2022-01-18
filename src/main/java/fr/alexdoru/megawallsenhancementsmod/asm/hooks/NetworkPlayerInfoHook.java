package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.util.IChatComponent;

public class NetworkPlayerInfoHook {

    public static IChatComponent getDisplayName(IChatComponent displayNameIn, GameProfile gameProfileIn) {
        if (displayNameIn == null) {
            return NameUtil.getTransformedDisplayName(gameProfileIn);
        }
        return null;
    }

    public static int getPlayersFinals(String playername) {
        if(!FKCounterMod.isInMwGame()){
            return 0;
        }
        Integer finals = KillCounter.allPlayerKills.get(playername);
        return finals == null ? 0 : finals;
    }

}
