package fr.alexdoru.mwe.hackerdetector.checks;

import fr.alexdoru.mwe.hackerdetector.data.PlayerDataSamples;
import net.minecraft.entity.player.EntityPlayer;

public interface ICheck {

    /** Returns the name of the cheat that this check is for */
    String getCheatName();

    /** Returns a descritpion of the cheat that this check is for */
    String getCheatDescription();

    default String getFlagType() {
        return "";
    }

    /** Wether or not this check should send a report to the server */
    boolean canSendReport();

    /** Performs the check on the player and prints a message if the player flags */
    void performCheck(EntityPlayer player, PlayerDataSamples data);

    /**
     * Performs the check on the player and returns true if the player fails the check
     * If you want to handle your violation levels manually, you have to update them in
     * this method
     */
    boolean check(EntityPlayer player, PlayerDataSamples data);

}
