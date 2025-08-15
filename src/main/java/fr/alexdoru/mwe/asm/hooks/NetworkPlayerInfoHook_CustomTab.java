package fr.alexdoru.mwe.asm.hooks;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.utils.NameUtil;
import net.minecraft.util.IChatComponent;

public class NetworkPlayerInfoHook_CustomTab {

    public static IChatComponent getDisplayName(GameProfile gameProfileIn) {
        return NameUtil.getMWPlayerData(gameProfileIn, false).displayName;
    }

    public static int getPlayersFinals(String playername) {
        return FinalKillCounter.getPlayersFinals(playername);
    }

    public static IChatComponent changeDisplayName(IChatComponent original, IChatComponent customName) {
        return original != null ? original : customName;
    }

}
