package fr.alexdoru.mwe.asm.hooks;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.utils.NameUtil;
import net.minecraft.util.IChatComponent;

public class NetworkPlayerInfoHook_CustomTab {

    public static IChatComponent getDisplayName(GameProfile gameProfileIn) {
        return NameUtil.getMWPlayerData(gameProfileIn, false).displayName;
    }

    public static int getPlayersFinals(String playername) {
        final FinalKillCounter fkCounter = MWE.INSTANCE().getFinalKillCounter();
        if (fkCounter == null) return 0;
        return fkCounter.getFinalKillsOfPlayer(playername);
    }

    public static IChatComponent changeDisplayName(IChatComponent original, IChatComponent customName) {
        return original != null ? original : customName;
    }

}
