package fr.alexdoru.megawallsenhancementsmod.asm.hooks;

import com.mojang.authlib.GameProfile;
import fr.alexdoru.megawallsenhancementsmod.asm.accessor.EntityPlayerAccessor;
import net.minecraft.entity.player.EntityPlayer;

@SuppressWarnings("unused")
public class EntityPlayerHook {

    public static String getTransformedDisplayName(String displayNameIn, EntityPlayer entityIn, GameProfile gameProfile) {
        final String prestige4Tag = ((EntityPlayerAccessor) entityIn).getPrestige4Tag();
        final String prestige5Tag = ((EntityPlayerAccessor) entityIn).getPrestige5Tag();
        if (prestige5Tag != null && prestige4Tag != null) {
            return displayNameIn.replace(prestige4Tag, prestige5Tag);
        } else {
            return displayNameIn;
        }
    }

}
