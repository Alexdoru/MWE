package fr.alexdoru.megawallsenhancementsmod.hackerdetector;

import fr.alexdoru.megawallsenhancementsmod.asm.accessor.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.AutoblockCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.ICheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.OmniSprintCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.SprintCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector3D;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HackerDetector {

    public static final HackerDetector INSTANCE = new HackerDetector();
    /** Field stolen from EntityLivingBase */
    private static final UUID sprintingUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private final Minecraft mc = Minecraft.getMinecraft();
    private final List<ICheck> checkList = new ArrayList<>();
    private long timeElapsedTemp = 0L;
    private long timeElapsed = 0L;
    private int playersChecked = 0;
    private int playersCheckedTemp = 0;

    static {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    public HackerDetector() {
        checkList.add(new AutoblockCheck());
        //checkList.add(new KillAuraSwitchCheck());// TODO remove debug
        checkList.add(new SprintCheck());
        checkList.add(new OmniSprintCheck());
        // TODO add kill aura check if player tracks entity while hitting it with good tracking, look at the 3D angle diff > a certain value
    }

    @SubscribeEvent
    public void onDrawDebugText(RenderGameOverlayEvent.Text event) {
        if (mc.gameSettings.showDebugInfo) {
            event.left.add("");
            event.left.add("Hacker Detector:");
            event.left.add("Player" + (playersChecked > 1 ? "s" : "") + " checked: " + playersChecked);
            event.left.add("Time elapsed (ns/s): " + ChatUtil.formatLong(timeElapsed));
            final double fpsLost = (timeElapsed / (10e9d - timeElapsed)) * Minecraft.getDebugFPS();
            event.left.add("Impact on performance : -" + String.format("%.2f", fpsLost) + "fps");
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            playersCheckedTemp = 0;
        } else if (event.phase == TickEvent.Phase.END) {
            if (mc.thePlayer != null && mc.thePlayer.ticksExisted % 20 == 0) {
                timeElapsed = timeElapsedTemp;
                timeElapsedTemp = 0L;
            }
            playersChecked = playersCheckedTemp;
        }
    }

    /**
     * This gets called once per entity per tick
     * only gets called when the client plays on a server
     * Hook is injected at end of {@link net.minecraft.world.World#updateEntityWithOptionalForce}
     */
    public void performChecksOnPlayer(EntityPlayer player) {
        if (mc.thePlayer == null ||
                player == mc.thePlayer ||// TODO remove debug
                //!player.getName().equals("MacronDemlssion") ||
                player.ticksExisted < 20 ||
                player.isDead ||
                player.capabilities.isFlying ||
                player.capabilities.isCreativeMode ||
                NameUtil.filterNPC(player.getUniqueID())) {
            return;
        }
        final long timeStart = System.nanoTime();
        playersCheckedTemp++;
        final PlayerDataSamples data = ((EntityPlayerAccessor) player).getPlayerDataSamples();
        updateEntityData(player, data);
        checkList.forEach(check -> check.performCheck(player, data));
        timeElapsedTemp += System.nanoTime() - timeStart;
    }

    private void updateEntityData(EntityPlayer player, PlayerDataSamples data) {
        data.sprintTime = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getModifier(sprintingUUID) == null ? 0 : data.sprintTime + 1;
        data.useItemTime = player.isUsingItem() ? data.useItemTime + 1 : 0;
        data.lastHurtTime = player.hurtTime == 9 ? 0 : data.lastHurtTime + 1;
        data.dXdYdZVector3D = new Vector3D(
                player.posX - player.lastTickPosX,
                player.posY - player.lastTickPosY,
                player.posZ - player.lastTickPosZ
        );
        data.dXdZVector2D = data.dXdYdZVector3D.getProjectionInXZPlane();
        data.isNotMoving = data.dXdYdZVector3D.isZero();
        if (!data.dXdYdZSampleList.isEmpty()) {
            final Vector3D lastdXdYdZ = data.dXdYdZSampleList.getFirst();
            data.directionDeltaXZList.add(data.dXdYdZVector3D.getXZAngleDiffWithVector(lastdXdYdZ));
        }
        data.dXdYdZSampleList.add(data.dXdYdZVector3D);
        final Vector3D lookVector = Vector3D.getVectorFromRotation(player.rotationPitch, player.rotationYaw);
        data.lookAngleDiff = lookVector.getAngleWithVector(data.lookVector);
        data.lookVector = lookVector;
        data.dYaw = player.rotationYaw - player.prevRotationYaw;
        if (data.wasLastdYawPositive) {
            data.lastTime_dYawChangedSign = data.dYaw < 0D ? 0 : data.lastTime_dYawChangedSign + 1;
        } else {
            data.lastTime_dYawChangedSign = data.dYaw >= 0D ? 0 : data.lastTime_dYawChangedSign + 1;
        }
        data.wasLastdYawPositive = data.dYaw >= 0D;
        data.lastSwingTime = player.isSwingInProgress && player.swingProgressInt == 0 ? 0 : data.lastSwingTime + 1;
    }

    /**
     * Used for debuging and testing
     */
    @SuppressWarnings("unused")
    private EntityPlayer getClosestPlayer() {
        EntityPlayer closestPlayer = null;
        double distance = 1000D;
        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (player instanceof EntityPlayerSP || player.ticksExisted < 60 || player.capabilities.isFlying || player.capabilities.isCreativeMode || NameUtil.filterNPC(player.getUniqueID())) {
                continue;
            }
            final float distanceToEntity = mc.thePlayer.getDistanceToEntity(player);
            if (distanceToEntity < distance) {
                closestPlayer = player;
                distance = distanceToEntity;
            }
        }
        return closestPlayer;
    }

}
