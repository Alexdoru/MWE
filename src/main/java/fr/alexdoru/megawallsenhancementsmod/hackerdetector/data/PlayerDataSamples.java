package fr.alexdoru.megawallsenhancementsmod.hackerdetector.data;

import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.AutoblockCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.FastbreakCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.SprintCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector2D;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector3D;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;

public class PlayerDataSamples {

    /** Used to update the data only once per tick, since the world.entity lists might contain duplicate items */
    public boolean updatedThisTick = false;
    /** True if the player's position in the XZ plane is identical to the last tick */
    public boolean isNotMoving = true;
    /** Amount of ticks since the player started sprinting */
    public int sprintTime = 0;
    /** Amount of ticks since the player has been using an item */
    public int useItemTime = 0;
    /** Amount of ticks since the player took a hit */
    public int lastHurtTime = 0;
    /** Amount of ticks since the last sword swing started */
    public int lastSwingTime = 0;
    /** True when we receive a swing packet from this entity during the last tick */
    public boolean customSwing = false;
    public int lastCustomSwingTime = -1;
    /** Amount of ticks since the list time the player eat or drank something */
    public int lastEatDrinkTime = 40;
    /**
     * True if the player's armor has been damaged during this tick, for instance when being attacked
     * Disabled as of right now, always false
     */
    public boolean armorDamaged = false;
    /** Used to filter hurt events from an ability damaging multiple players at
     * the same time and mistaken for a player attacking multiple eneities */
    public boolean hasAttackedMultiTarget = false;
    /** True if the player has attacked another player during this tick */
    public boolean hasAttacked = false;
    /** Player attacked during this tick if any */
    public EntityPlayer targetedPlayer;
    /** True if the player has beend attacked by another player during this tick */
    public boolean hasBeenAttacked = false;
    public boolean disabledAutoblockCheck = false;
    ///** Holds the position XYZ of the player over time */
    //public SampleList<Vector3D> positionSampleList = new SampleList<>(40);
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
    ///**
    // * Holds the changes in the direction of the player in the XZ plane
    // * This is the angle in between the player's velocity vector and the one from the previous tick
    // * Expressed in degres
    // */
    //public final SampleList<Double> directionDeltaXZList = new SampleList<>(20);
    ///**
    // * Holds the 'pos - prevPos' of the player for the last 20 ticks along the 3 axis
    // * This is proportional to the velocity of the entity
    // * To get the actual velocity one whould have to multiply the values by 20(tick/sec)
    // */
    //public final SampleList<Vector3D> dXdYdZSampleList = new SampleList<>(20);
    ///** The player's look */
    //public Vector3D lookVector = new Vector3D();
    ///** The angle difference in 3D space of the player's look since last tick */
    //public double lookAngleDiff = 0D;
    ///** Variation of Yaw since last tick (which is in XZ plane) */
    //public double dYaw = 0D;
    //public boolean lastNonZerodYawPositive = true;
    ///** Amount of ticks since dYaw changed sign */
    //public int lastTime_dYawChangedSign = 0;
    /** Last time the player broke a block */
    public long lastBreakBlockTime = System.currentTimeMillis();
    public final SampleList<Float> breakTimeRatio = new SampleList<>(8);
    public final ViolationLevelTracker autoblockVL = AutoblockCheck.newViolationTracker();
    public final ViolationLevelTracker fastbreakVL = FastbreakCheck.newViolationTracker();
    //public final ViolationLevelTracker killauraSwitchVL = KillAuraSwitchCheck.newViolationTracker();
    public final ViolationLevelTracker noslowdownVL = SprintCheck.newNoslowdownViolationTracker();
    public final ViolationLevelTracker keepsprintUseItemVL = SprintCheck.newKeepsprintViolationTracker();
    //public final ViolationLevelTracker omnisprintVL = OmniSprintCheck.newViolationTracker();

    public void ontickStart() {
        this.updatedThisTick = false;
        this.customSwing = false;
        this.armorDamaged = false;
        this.hasAttackedMultiTarget = false;
        this.hasAttacked = false;
        this.targetedPlayer = null;
        this.hasBeenAttacked = false;
    }

    public void onTick(EntityPlayer player) {
        final ItemStack itemStack = player.getItemInUse();
        if (itemStack != null) {
            final Item item = itemStack.getItem();
            if (item instanceof ItemFood || item instanceof ItemPotion) {
                this.lastEatDrinkTime = 0;
            } else {
                this.lastEatDrinkTime += 1;
            }
        } else {
            this.lastEatDrinkTime += 1;
        }
        this.sprintTime = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getModifier(HackerDetector.sprintingUUID) == null ? 0 : this.sprintTime + 1;
        this.useItemTime = player.isUsingItem() ? this.useItemTime + 1 : 0;
        this.lastHurtTime = player.hurtTime == 9 ? 0 : this.lastHurtTime + 1;
        this.lastSwingTime = player.isSwingInProgress && player.swingProgressInt == 0 ? 0 : this.lastSwingTime + 1;
        this.lastCustomSwingTime++;
        //this.positionSampleList.add(new Vector3D(player.posX, player.posY, player.posZ));
        this.dXdYdZVector3D = new Vector3D(
                player.posX - player.lastTickPosX,
                player.posY - player.lastTickPosY,
                player.posZ - player.lastTickPosZ
        );
        this.dXdZVector2D = this.dXdYdZVector3D.getProjectionInXZPlane();
        this.isNotMoving = this.dXdZVector2D.isZero();
        //if (!this.dXdYdZSampleList.isEmpty()) {
        //    final Vector3D lastdXdYdZ = this.dXdYdZSampleList.getFirst();
        //    this.directionDeltaXZList.add(this.dXdYdZVector3D.getXZAngleDiffWithVector(lastdXdYdZ));
        //}
        //this.dXdYdZSampleList.add(this.dXdYdZVector3D);
        //final Vector3D lookVector = Vector3D.getPlayersLookVec(player);
        //this.lookAngleDiff = lookVector.getAngleWithVector(this.lookVector);
        //this.lookVector = lookVector;
        //final double dYaw = player.rotationYawHead - player.prevRotationYawHead;
        //if (dYaw != 0 && this.dYaw != 0) {
        //    this.lastTime_dYawChangedSign = dYaw * this.dYaw > 0 ? this.lastTime_dYawChangedSign + 1 : 0;
        //} else if (dYaw != 0) {
        //    if (this.lastNonZerodYawPositive) {
        //        this.lastTime_dYawChangedSign = dYaw > 0 ? this.lastTime_dYawChangedSign + 1 : 0;
        //    } else {
        //        this.lastTime_dYawChangedSign = dYaw < 0 ? this.lastTime_dYawChangedSign + 1 : 0;
        //    }
        //} else {
        //    this.lastTime_dYawChangedSign = this.lastTime_dYawChangedSign + 1;
        //}
        //if (dYaw != 0) {
        //    this.lastNonZerodYawPositive = dYaw > 0;
        //}
        //this.dYaw = dYaw;
    }

}
