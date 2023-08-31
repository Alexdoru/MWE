package fr.alexdoru.megawallsenhancementsmod.hackerdetector.data;

import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.*;
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
    /** Amount of ticks since the player started sprinting */
    public int sprintTime = 0;
    /** Amount of ticks since the player has been using an item */
    public int useItemTime = 0;
    /** Amount of ticks since the player took a hit */
    public int lastHurtTime = 0;
    /** Amount of ticks since the last sword swing started */
    public int lastSwingTime = -1;
    /** True when we receive a swing packet from this entity during the last tick */
    public boolean hasSwung = false;
    public final SampleListZ swingList = new SampleListZ(20);
    /** Amount of ticks since the list time the player ate or drank something */
    public int lastEatDrinkTime = 40;
    /** True if the player's armor has been damaged during this tick, for instance when being attacked
     * Disabled as of right now, always false */
    public boolean armorDamaged = false;
    /** Used to filter hurt events from an ability damaging multiple players at
     * the same time and mistaken for a player attacking multiple entities */
    public boolean hasAttackedMultiTarget = false;
    /** True if the player has attacked another player during this tick */
    public boolean hasAttacked = false;
    /** Player attacked during this tick if any */
    public EntityPlayer targetedPlayer;
    public SampleListZ attackList = new SampleListZ(20);
    /** True if the player has beend attacked by another player during this tick */
    public boolean hasBeenAttacked = false;
    public SampleListD speedXList = new SampleListD(20);
    public SampleListD speedYList = new SampleListD(20);
    public SampleListD speedZList = new SampleListD(20);
    /** Last time the player broke a block */
    public long lastBreakBlockTime = System.currentTimeMillis();
    public final SampleListF breakTimeRatio = new SampleListF(8);
    public final ViolationLevelTracker autoblockVL = AutoblockCheck.newViolationTracker();
    public final ViolationLevelTracker fastbreakVL = FastbreakCheck.newViolationTracker();
    public final ViolationLevelTracker keepsprintVL = KeepsprintCheck.newViolationTracker();
    public final ViolationLevelTracker killAuraVL = KillAuraCheck.newViolationTracker();
    public final ViolationLevelTracker noSlowdownVL = NoSlowdownCheck.newViolationTracker();
    ///** The player's look */
    //public Vector3D lookVector = new Vector3D();
    ///** The angle difference in 3D space of the player's look since last tick */
    //public double lookAngleDiff = 0D;
    ///** Variation of Yaw since last tick (which is in XZ plane) */
    //public double dYaw = 0D;
    //public boolean lastNonZerodYawPositive = true;
    ///** Amount of ticks since dYaw changed sign */
    //public int lastTime_dYawChangedSign = 0;

    public void ontickStart() {
        this.updatedThisTick = false;
        this.hasSwung = false;
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
            this.lastEatDrinkTime = item instanceof ItemFood || item instanceof ItemPotion ? 0 : this.lastEatDrinkTime + 1;
        } else {
            this.lastEatDrinkTime += 1;
        }
        this.sprintTime = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getModifier(HackerDetector.sprintingUUID) == null ? 0 : this.sprintTime + 1;
        this.useItemTime = player.isUsingItem() ? this.useItemTime + 1 : 0;
        this.lastHurtTime = player.hurtTime == 9 ? 0 : this.lastHurtTime + 1;
        this.lastSwingTime++;
        this.swingList.add(this.hasSwung);
        this.attackList.add(this.hasAttacked);
        this.speedXList.add((player.posX - player.lastTickPosX) * 20D);
        this.speedYList.add((player.posY - player.lastTickPosY) * 20D);
        this.speedZList.add((player.posZ - player.lastTickPosZ) * 20D);
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

    /** True if the player's position in the XZ plane is identical to the last tick */
    public boolean isNotMovingXZ() {
        return this.speedXList.get(0) == 0D && this.speedZList.get(0) == 0D;
    }

    public double getSpeedXZ() {
        final double vx = this.speedXList.get(0);
        final double vz = this.speedZList.get(0);
        return Math.sqrt(vx * vx + vz * vz);
    }

    public double getSpeedXZ(int index) {
        final double vx = this.speedXList.get(index);
        final double vz = this.speedZList.get(index);
        return Math.sqrt(vx * vx + vz * vz);
    }

    public String speedToString() {
        return "{x=" + String.format("%.4f", this.speedXList.get(0)) +
                ", y=" + String.format("%.4f", this.speedYList.get(0)) +
                ", z=" + String.format("%.4f", this.speedZList.get(0)) +
                '}';
    }

}
