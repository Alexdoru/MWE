package fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks;

import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.player.EntityPlayer;

public interface ICheck {

    /** Returns the name of the cheat that this check is for */
    String getCheatName();

    /** Returns a descritpion of the cheat that this check is for */
    String getCheatDescription();

    /** Performs the check on the player and prints a message if the player flags */
    void performCheck(EntityPlayer player, PlayerDataSamples data);

    /** Performs the check on the player and returns true if the player fails the check */
    boolean check(EntityPlayer player, PlayerDataSamples data);

    /** Checks the violation level and prints chat output if the player exceeds the violation level */
    void checkViolationLevel(EntityPlayer player, ViolationLevelTracker tracker, boolean failedCheck);

}
