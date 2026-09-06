package fr.alexdoru.mwe.api;

import com.mojang.authlib.GameProfile;

/**
 * Implementing this interface and registering it via
 * {@link fr.alexdoru.mwe.api.MWEApi.Names#registerTabNameModifier(ITabNameModifier)}
 * allows you to modify the name shown in the tablist.
 */
public interface ITabNameModifier {

    /**
     * The priority of this modifier
     */
    int getPriority();

    /**
     * Return true if this modifier will change the tablist name of the player owning this gameProfile.
     */
    boolean shouldModifyName(GameProfile gameProfile);

    /**
     * Modify the name in the tablist
     *
     * @param gameProfile - the gameprofile of the player
     * @param prefix      - the extra prefix that might have been added to this player
     * @param suffix      - the extra suffix that might have been added to this player
     */
    void modifyTabname(GameProfile gameProfile, StringBuilder prefix, StringBuilder suffix);

}
