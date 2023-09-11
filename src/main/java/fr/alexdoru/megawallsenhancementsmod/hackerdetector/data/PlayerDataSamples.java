package fr.alexdoru.megawallsenhancementsmod.hackerdetector.data;

import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.*;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerDataSamples {

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
    /** Used to filter hurt events from an ability damaging multiple players at
     * the same time and mistaken for a player attacking multiple entities */
    public boolean hasAttackedMultiTarget = false;
    /** True if the player has attacked another player during this tick */
    public boolean hasAttacked = false;
    /** Player attacked during this tick if any */
    public EntityPlayer targetedPlayer;
    public SampleListZ attackList = new SampleListZ(20);
    /** True if the player has been attacked by another player during this tick */
    public boolean hasBeenAttacked = false;

    /* ----- Samples of rotations/positions "emulated/delayed" by the client ----- */
    public SampleListD posXList = new SampleListD(5);
    public SampleListD posYList = new SampleListD(5);
    public SampleListD posZList = new SampleListD(5);
    public SampleListD speedXList = new SampleListD(5);
    public SampleListD speedYList = new SampleListD(5);
    public SampleListD speedZList = new SampleListD(5);
    /* ----- Client samples end ----- */

    /* ----- Samples of rotations/positions received from the server ----- */
    /** True if we received a position/rotation packet for this player during this tick */
    public int updatedServerData = 0;
    public SampleListI updatedServerDataList = new SampleListI(5);
    /** True if we received a S19PacketEntityHeadLook packet for this player during this tick */
    public int updatedYawHead = 0;
    public SampleListI updatedYawHeadList = new SampleListI(5);
    public double serverPosX;
    public double serverPosY;
    public double serverPosZ;
    /** Pitch of the player's head */
    public float serverRotationPitch;
    /** Yaw of the player's body */
    public float serverRotationYaw;
    /** Yaw of the player's head */
    public float serverRotationYawHead;
    public SampleListD serverPosXList = new SampleListD(5);
    public SampleListD serverPosYList = new SampleListD(5);
    public SampleListD serverPosZList = new SampleListD(5);
    public SampleListF serverRotationPitchList = new SampleListF(5);
    public SampleListF serverRotationYawList = new SampleListF(5);
    public SampleListF serverRotationYawHeadList = new SampleListF(5); // values are directly equals to player.rotationYawHead
    /* ----- Server samples end ----- */

    /** Last time the player broke a block */
    public long lastBreakBlockTime = System.currentTimeMillis();
    public final SampleListF breakTimeRatio = new SampleListF(8);
    public final ViolationLevelTracker autoblockVL = AutoblockCheck.newViolationTracker();
    public final ViolationLevelTracker fastbreakVL = FastbreakCheck.newViolationTracker();
    public final ViolationLevelTracker keepsprintVL = KeepsprintCheck.newViolationTracker();
    public final ViolationLevelTracker killAuraVL = KillAuraCheck.newViolationTracker();
    public final ViolationLevelTracker noSlowdownVL = NoSlowdownCheck.newViolationTracker();

    public void onTickStart() {
        this.hasSwung = false;
        this.hasAttackedMultiTarget = false;
        this.hasAttacked = false;
        this.targetedPlayer = null;
        this.hasBeenAttacked = false;
        this.updatedServerData = 0;
        this.updatedYawHead = 0;
    }

    public void onTick(EntityPlayer player) {
        this.sprintTime = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getModifier(HackerDetector.sprintingUUID) == null ? 0 : this.sprintTime + 1;
        this.useItemTime = player.isUsingItem() ? this.useItemTime + 1 : 0;
        this.lastHurtTime = player.hurtTime == 10 ? 0 : this.lastHurtTime + 1;
        this.lastSwingTime++;
        this.swingList.add(this.hasSwung);
        this.attackList.add(this.hasAttacked);
        this.posXList.add(player.posX);
        this.posYList.add(player.posY);
        this.posZList.add(player.posZ);
        this.speedXList.add((player.posX - player.lastTickPosX) * 20D);
        this.speedYList.add((player.posY - player.lastTickPosY) * 20D);
        this.speedZList.add((player.posZ - player.lastTickPosZ) * 20D);
        this.updatedServerDataList.add(this.updatedServerData);
        this.updatedYawHeadList.add(this.updatedYawHead);
        this.serverPosXList.add(this.serverPosX);
        this.serverPosYList.add(this.serverPosY);
        this.serverPosZList.add(this.serverPosZ);
        this.serverRotationPitchList.add(this.serverRotationPitch);
        this.serverRotationYawList.add(this.serverRotationYaw);
        this.serverRotationYawHeadList.add(this.serverRotationYawHead);
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

    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        this.updatedServerData++;
        this.serverPosX = x;
        this.serverPosY = y;
        this.serverPosZ = z;
        this.serverRotationPitch = yaw % 360.0F;
        this.serverRotationYaw = pitch % 360.0F;
    }

    public void setRotationYawHead(float yawHead) {
        this.updatedYawHead++;
        this.serverRotationYawHead = yawHead;
    }

}
