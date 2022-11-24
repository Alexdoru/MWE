package fr.alexdoru.megawallsenhancementsmod.hackerdetector.data;

import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.AutoblockCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.KillAuraHeadsnapCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.NoSlowdownCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.OmniSprintCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector3D;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;

public class PlayerDataSamples {

    // TODO add time falling ? in ticks

    /** Amount of ticks since the player started sprinting */
    public int sprintTime = 0;
    /** Amount of ticks since the player has been using an item */
    public int useItemTime = 0;
    /** Amount of ticks since the player took a hit */
    public int lastHurtTime = 0;// TODO comment out
    ///**
    // * Holds the distance covered by the player in the XZ plane during the last tick
    // * This is proportional to the velocity in the XZ plane of the entity
    // * To get the actual velocity one whould have to multiply the value by 20(tick/sec)
    // */
    //public double positionDiffXZ = 0D;
    //public double prevPositionDiffXZ = 0D;
    /**
     * Holds the 'pos - prevPos' of the player for the last 20 ticks along the 3 axis
     * This is proportional to the velocity of the entity
     * To get the actual velocity one whould have to multiply the values by 20(tick/sec)
     */
    public final SampleList<Vector3D> dXdYdZSampleList = new SampleList<>(20);
    /**
     * Holds the changes in the direction of the player in the XZ plane
     * This is the angle in between the player's velocity vector and the one from the previous tick
     * Expressed in degres
     */
    public final SampleList<Double> directionDeltaXZList = new SampleList<>(20);
    public final ViolationLevelTracker autoblockVL = AutoblockCheck.getViolationTracker();
    public final ViolationLevelTracker killauraHeadsnapVL = KillAuraHeadsnapCheck.getViolationTracker();
    public final ViolationLevelTracker noslowdownVL = NoSlowdownCheck.getViolationTracker();
    public final ViolationLevelTracker omnisprintVL = OmniSprintCheck.getViolationTracker();

}
