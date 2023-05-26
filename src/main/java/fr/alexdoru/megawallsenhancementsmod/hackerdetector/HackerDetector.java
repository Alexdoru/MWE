package fr.alexdoru.megawallsenhancementsmod.hackerdetector;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.*;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.BrokenBlock;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector3D;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class HackerDetector {

    // TODO is it possible to know who is the attacker of an entity

    public static final HackerDetector INSTANCE = new HackerDetector();
    /** Field stolen from EntityLivingBase */
    private static final UUID sprintingUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private final Minecraft mc = Minecraft.getMinecraft();
    private final List<ICheck> checkList = new ArrayList<>();
    private long timeElapsedTemp = 0L;
    private long timeElapsed = 0L;
    private int playersChecked = 0;
    private int playersCheckedTemp = 0;
    /** Data about blocks broken during this tick */
    public List<BrokenBlock> brokenBlocksList = new LinkedList<>();
    public HashSet<String> playersToLog = new HashSet<>();

    static {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    public HackerDetector() {
        checkList.add(new AutoblockCheck());
        checkList.add(new FastbreakCheck());
        //checkList.add(new KillAuraSwitchCheck());
        checkList.add(new SprintCheck());
        //checkList.add(new OmniSprintCheck());
        // TODO add kill aura check if player tracks (the same ?) entity
        //  while hitting it with good tracking, look at the 3D angle diff > a certain value
    }

    @SubscribeEvent
    public void onDrawDebugText(RenderGameOverlayEvent.Text event) {
        if (mc.gameSettings.showDebugInfo && ConfigHandler.hackerDetector) {
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
            brokenBlocksList.clear();
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
                player == mc.thePlayer ||
                player.ticksExisted < 20 ||
                player.isDead ||
                player.capabilities.isFlying ||
                player.capabilities.isCreativeMode ||
                player.isInvisible() ||
                NameUtil.filterNPC(player.getUniqueID())) {
            return;
        }
        final long timeStart = System.nanoTime();
        playersCheckedTemp++;
        final PlayerDataSamples data = ((EntityPlayerAccessor) player).getPlayerDataSamples();
        updatePlayerDataSamples(player, data);
        if (ConfigHandler.debugLogging && playersToLog.contains(player.getName())) log(player, data);
        checkList.forEach(check -> check.performCheck(player, data));
        timeElapsedTemp += System.nanoTime() - timeStart;
    }

    private void updatePlayerDataSamples(EntityPlayer player, PlayerDataSamples data) {
        final ItemStack itemStack = player.getItemInUse();
        if (itemStack != null) {
            final Item item = itemStack.getItem();
            if (item instanceof ItemFood || item instanceof ItemPotion) {
                data.lastEatDrinkTime = 0;
            } else {
                data.lastEatDrinkTime += 1;
            }
        } else {
            data.lastEatDrinkTime += 1;
        }
        data.sprintTime = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getModifier(sprintingUUID) == null ? 0 : data.sprintTime + 1;
        data.useItemTime = player.isUsingItem() ? data.useItemTime + 1 : 0;
        data.lastHurtTime = player.hurtTime == 9 ? 0 : data.lastHurtTime + 1;
        data.lastSwingTime = player.isSwingInProgress && player.swingProgressInt == 0 ? 0 : data.lastSwingTime + 1;
        //data.positionSampleList.add(new Vector3D(player.posX, player.posY, player.posZ));
        data.dXdYdZVector3D = new Vector3D(
                player.posX - player.lastTickPosX,
                player.posY - player.lastTickPosY,
                player.posZ - player.lastTickPosZ
        );
        data.dXdZVector2D = data.dXdYdZVector3D.getProjectionInXZPlane();
        data.isNotMoving = data.dXdZVector2D.isZero();
        //if (!data.dXdYdZSampleList.isEmpty()) {
        //    final Vector3D lastdXdYdZ = data.dXdYdZSampleList.getFirst();
        //    data.directionDeltaXZList.add(data.dXdYdZVector3D.getXZAngleDiffWithVector(lastdXdYdZ));
        //}
        //data.dXdYdZSampleList.add(data.dXdYdZVector3D);
        //final Vector3D lookVector = Vector3D.getPlayersLookVec(player);
        //data.lookAngleDiff = lookVector.getAngleWithVector(data.lookVector);
        //data.lookVector = lookVector;
        //final double dYaw = player.rotationYawHead - player.prevRotationYawHead;
        //if (dYaw != 0 && data.dYaw != 0) {
        //    data.lastTime_dYawChangedSign = dYaw * data.dYaw > 0 ? data.lastTime_dYawChangedSign + 1 : 0;
        //} else if (dYaw != 0) {
        //    if (data.lastNonZerodYawPositive) {
        //        data.lastTime_dYawChangedSign = dYaw > 0 ? data.lastTime_dYawChangedSign + 1 : 0;
        //    } else {
        //        data.lastTime_dYawChangedSign = dYaw < 0 ? data.lastTime_dYawChangedSign + 1 : 0;
        //    }
        //} else {
        //    data.lastTime_dYawChangedSign = data.lastTime_dYawChangedSign + 1;
        //}
        //if (dYaw != 0) {
        //    data.lastNonZerodYawPositive = dYaw > 0;
        //}
        //data.dYaw = dYaw;
    }

    //private void checkForRubberBand(double directionDeltaXZ, PlayerDataSamples data, EntityPlayer player) {
    //    if (directionDeltaXZ > 170D && data.dXdZVector2D.norm() > 0.6D) {
    //        ChatUtil.debug("Detected Rubber band of " + player.getName());
    //        AbstractCheck.logger.info("Detected Rubber band of " + player.getName()
    //                + " | ticksExisted " + player.ticksExisted
    //                + " | lastHurtTime " + data.lastHurtTime
    //                + " | directionDeltaXZ " + String.format("%.4f", directionDeltaXZ)
    //                + " | onGround " + player.onGround
    //                + " | speedXZ (m/s) " + String.format("%.4f", data.dXdZVector2D.norm() * 20D)
    //                + " | prev positions " + data.positionSampleList
    //        );
    //    }
    //}

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

    private void log(EntityPlayer player, PlayerDataSamples data) {
        AbstractCheck.logger.info(player.getName()
                + " | onGround " + player.onGround
                + " | speedXZ (m/s) " + String.format("%.4f", data.dXdZVector2D.norm() * 20D)
                + " | speedXYZ (m/s) " + data.dXdYdZVector3D.mulitply(20)
                + " | position " + new Vector3D(player.posX, player.posY, player.posZ)
                + " | rotationPitch " + String.format("%.4f", player.rotationPitch)
                + " | rotationYawHead " + String.format("%.4f", player.rotationYawHead)
                //+ " | look Vector " + data.lookVector
                //+ " | lookAngleDiff " + String.format("%.4f", data.lookAngleDiff)
                //+ " | dYaw " + String.format("%.4f", data.dYaw)
                //+ " | lastTime_dYawChangedSign " + data.lastTime_dYawChangedSign
                + " | is sprinting " + (player.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getModifier(sprintingUUID) != null)
                + " | sprintTime " + data.sprintTime
                + " | lastHurtTime " + data.lastHurtTime
                + " | isSwingInProgress " + player.isSwingInProgress
                + " | useItemTime " + data.useItemTime
                + " | lastSwingTime " + data.lastSwingTime
                + " | lastEatDrinkTime " + data.lastEatDrinkTime
                + " | isUsingItem " + player.isUsingItem()
                + " | ticksExisted " + player.ticksExisted
                + " | isRidingEntity " + player.isRiding()
        );
    }

}
