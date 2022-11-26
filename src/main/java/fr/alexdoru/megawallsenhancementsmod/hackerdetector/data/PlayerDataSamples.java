package fr.alexdoru.megawallsenhancementsmod.hackerdetector.data;

import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.AutoblockCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.KillAuraSwitchCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.OmniSprintCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.SprintCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector2D;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector3D;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;

public class PlayerDataSamples {

    /** True if the player's position in 3D space is identical to the last tick */
    public boolean isNotMoving = true;
    /** Amount of ticks since the player started sprinting */
    public int sprintTime = 0;
    /** Amount of ticks since the player has been using an item */
    public int useItemTime = 0;
    /** Amount of ticks since the player took a hit */
    public int lastHurtTime = 0;// TODO comment out
    /**
     * Holds the distance covered by the player in space during the last tick
     * This is proportional to the velocity of the entity
     * To get the actual velocity one whould have to multiply the value by 20(tick/sec)
     */
    public Vector3D dXdYdZVector3D = new Vector3D();
    /**
     * Holds the distance covered by the player in the XZ plane during the last tick
     * This is proportional to the velocity in the XZ plane of the entity
     * To get the actual velocity one whould have to multiply the value by 20(tick/sec)
     */
    public Vector2D dXdZVector2D = new Vector2D();
    /**
     * Holds the 'pos - prevPos' of the player for the last 20 ticks along the 3 axis
     * This is proportional to the velocity of the entity
     * To get the actual velocity one whould have to multiply the values by 20(tick/sec)
     */
    public final SampleList<Vector3D> dXdYdZSampleList = new SampleList<>(20);// TODO inutile
    /** The player's look */
    public Vector3D lookVector = new Vector3D();
    /** The angle difference in 3D space of the player's look since last tick */
    public double lookAngleDiff = 0D;
    /** Variation of Yaw since last tick (which is in XZ plane) */
    public double dYaw = 0D;
    /** Amount of ticks since dYaw changed sign */
    public int lastTime_dYawChangedSign = 0;
    public boolean wasLastdYawPositive = true;
    /** Amount of ticks since the last sword swing started, used to check if the player is looking at an entity */
    // TODO test if entity gets hurt for through blocks check
    public int lastSwingTime = 0;
    /**
     * Holds the changes in the direction of the player in the XZ plane
     * This is the angle in between the player's velocity vector and the one from the previous tick
     * Expressed in degres
     */
    public final SampleList<Double> directionDeltaXZList = new SampleList<>(20);
    public final ViolationLevelTracker autoblockVL = AutoblockCheck.newViolationTracker();
    public final ViolationLevelTracker killauraHeadsnapVL = KillAuraSwitchCheck.newViolationTracker();
    public final ViolationLevelTracker noslowdownVL = SprintCheck.newNoslowdownViolationTracker();
    public final ViolationLevelTracker keepsprintUseItemVL = SprintCheck.newKeepsprintViolationTracker();
    public final ViolationLevelTracker omnisprintVL = OmniSprintCheck.newViolationTracker();

}
