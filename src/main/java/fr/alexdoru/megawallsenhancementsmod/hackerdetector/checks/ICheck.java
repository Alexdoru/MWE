package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;

public interface ICheck {

    /** Returns the name of the cheat that this check is for */
    String getCheatName();

    /** Returns a descritpion of the cheat that this check is for */
    String getCheatDescription();

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

    /**
     * Checks the violation level of each ViolationLevelTracker of this check
     * and prints chat output if the player exceeds one of the violation levels
     */
    void checkViolationLevel(EntityPlayer player, boolean failedCheck, ViolationLevelTracker... tracker);

}
