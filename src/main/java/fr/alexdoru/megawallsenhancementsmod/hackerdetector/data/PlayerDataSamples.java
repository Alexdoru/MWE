package fr.alexdoru.megawallsenhancementsmod.hackerdetector.data;

import fr.alexdoru.megawallsenhancementsmod.hackerdetector.HackerDetector;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.*;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.ViolationLevelTracker;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

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
    public final SampleListZ attackList = new SampleListZ(20);

    /* ----- Samples of rotations/positions "emulated/delayed" by the client ----- */
    public final SampleListD posXList = new SampleListD(5);
    public final SampleListD posYList = new SampleListD(5);
    public final SampleListD posZList = new SampleListD(5);
    public final SampleListD speedXList = new SampleListD(5);
    public final SampleListD speedYList = new SampleListD(5);
    public final SampleListD speedZList = new SampleListD(5);
    /* ----- Client samples end ----- */

    /* ----- Samples of rotations/positions received from the server ----- */
    /** Not 0 if we received a position/rotation packet for this player during this tick */
    public int updatedServerData = 0;
    public final SampleListI updatedServerDataList = new SampleListI(5);
    /** Not 0 if we received a S19PacketEntityHeadLook packet for this player during this tick */
    public int updatedYawHead = 0;
    public final SampleListI updatedYawHeadList = new SampleListI(5);
    public double serverPosX;
    public double serverPosY;
    public double serverPosZ;
    /** Pitch of the player's head [-90, 90] */
    public float serverRotationPitch;
    /** Yaw of the player's body [-180, 180] */
    public float serverRotationYaw;
    /** Yaw of the player's head [-180, 180] */
    public float serverRotationYawHead;
    public final SampleListD serverPosXList = new SampleListD(5);
    public final SampleListD serverPosYList = new SampleListD(5);
    public final SampleListD serverPosZList = new SampleListD(5);
    public final SampleListF serverRotationPitchList = new SampleListF(5); // Pitch of the head
    public final SampleListF serverRotationYawList = new SampleListF(5);// Yaw of the body
    public final SampleListF serverRotationYawHeadList = new SampleListF(5); // Yaw of the head, directly equals to player.rotationYawHead
    /* ----- Server samples end ----- */

    /** Last time the player broke a block */
    public long lastBreakBlockTime = System.currentTimeMillis();
    public final SampleListF breakTimeRatio = new SampleListF(8);
    public final ViolationLevelTracker autoblockAVL = AutoblockCheckA.newViolationTracker();
    public final ViolationLevelTracker autoblockBVL = AutoblockCheckB.newViolationTracker();
    public final ViolationLevelTracker fastbreakVL = FastbreakCheck.newViolationTracker();
    public final ViolationLevelTracker keepsprintVL = KeepsprintCheck.newViolationTracker();
    public final ViolationLevelTracker killAuraVL = KillAuraCheck.newViolationTracker();
    public final ViolationLevelTracker noSlowdownVL = NoSlowdownCheck.newViolationTracker();

    public void onTickStart() {
        this.hasSwung = false;
        this.hasAttackedMultiTarget = false;
        this.hasAttacked = false;
        this.targetedPlayer = null;
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

    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        this.updatedServerData++;
        this.serverPosX = x;
        this.serverPosY = y;
        this.serverPosZ = z;
        this.serverRotationYaw = yaw;
        this.serverRotationPitch = pitch;
    }

    public void setRotationYawHead(float yawHead) {
        this.updatedYawHead++;
        this.serverRotationYawHead = yawHead;
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

    public Vec3 getPositionEyesServer(EntityPlayer player) {
        return new Vec3(this.serverPosX, this.serverPosY + (double) player.getEyeHeight(), this.serverPosZ);
    }

    public Vec3 getLookServer() {
        return getVectorForRotation(this.serverRotationPitch, this.serverRotationYawHead);
    }

    /**
     * Creates a Vec3 using the pitch and yaw of the entities' rotation.
     */
    private static Vec3 getVectorForRotation(float pitch, float yaw) {
        final float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        final float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        final float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        final float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }

}
