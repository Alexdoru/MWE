package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.megawallsenhancementsmod.asm.accessor.GameProfileAccessor;
import fr.alexdoru.megawallsenhancementsmod.data.MWPlayerData;

@SuppressWarnings("unused")
public class EntityPlayerHook {

    public static String getTransformedDisplayName(String displayNameIn, GameProfile gameProfile) {
        MWPlayerData mwPlayerData = ((GameProfileAccessor) gameProfile).getMWPlayerData();
        if (mwPlayerData == null) {
            return displayNameIn;
        } else {
            if (mwPlayerData.P5Tag != null && mwPlayerData.originalP4Tag != null) {
                return displayNameIn.replace(mwPlayerData.originalP4Tag, mwPlayerData.P5Tag);
            } else {
                return displayNameIn;
            }
        }
    }

}
