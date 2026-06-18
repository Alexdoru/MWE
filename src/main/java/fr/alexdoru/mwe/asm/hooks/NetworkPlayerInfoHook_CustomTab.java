package fr.alexdoru.mwe.asm.hooks;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.features.NameFormatter;
import net.minecraft.util.IChatComponent;

public class NetworkPlayerInfoHook_CustomTab {

    public static IChatComponent getDisplayName(GameProfile gameProfileIn) {
        return NameFormatter.getDisplayName(gameProfileIn);
    }

    public static int getPlayersFinals(GameProfile gameProfileIn) {
        final FinalKillCounter fkCounter = MWE.INSTANCE().getFinalKillCounter();
        if (fkCounter == null) return 0;
        return fkCounter.getFinalKillsOfPlayer(gameProfileIn.getName());
    }

    public static IChatComponent changeDisplayName(IChatComponent original, IChatComponent customName) {
        return original != null ? original : customName;
    }

}
