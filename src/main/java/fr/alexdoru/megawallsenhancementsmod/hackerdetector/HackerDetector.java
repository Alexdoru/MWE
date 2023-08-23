package fr.alexdoru.megawallsenhancementsmod.hackerdetector;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.AutoblockCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.FastbreakCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.ICheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.SprintCheck;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.BrokenBlock;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.utils.Vector3D;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class HackerDetector {

    public static final HackerDetector INSTANCE = new HackerDetector();
    public static final Logger logger = LogManager.getLogger("HackerDetector");
    /** Field stolen from EntityLivingBase */
    public static final UUID sprintingUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final List<ICheck> checkList = new ArrayList<>();
    private long timeElapsedTemp = 0L;
    private long timeElapsed = 0L;
    private int playersChecked = 0;
    private int playersCheckedTemp = 0;
    /** Data about blocks broken during this tick */
    public List<BrokenBlock> brokenBlocksList = new ArrayList<>();
    public HashSet<String> playersToLog = new HashSet<>();
    private final Queue<Runnable> scheduledTasks = new ArrayDeque<>();

    static {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    private HackerDetector() {
        checkList.add(new AutoblockCheck());
        checkList.add(FastbreakCheck.INSTANCE);
        checkList.add(new SprintCheck());
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
            final long timeStart = System.nanoTime();
            this.onTickStart();
            timeElapsedTemp += System.nanoTime() - timeStart;
        } else if (event.phase == TickEvent.Phase.END) {
            final long timeStart = System.nanoTime();
            this.onTickEnd();
            timeElapsedTemp += System.nanoTime() - timeStart;
            if (mc.thePlayer != null && mc.thePlayer.ticksExisted % 20 == 0) {
                timeElapsed = timeElapsedTemp;
                timeElapsedTemp = 0L;
            }
            playersChecked = playersCheckedTemp;
        }
    }

    private void onTickStart() {

        if (!ConfigHandler.hackerDetector) return;

        if (mc.theWorld != null) {
            for (final EntityPlayer player : mc.theWorld.playerEntities) {
                final PlayerDataSamples data = ((EntityPlayerAccessor) player).getPlayerDataSamples();
                data.updatedThisTick = false;
                data.customSwing = false;
                data.armorDamaged = false;
                data.hasAttacked = false;
                data.targetedPlayer = null;
                data.hasBeenAttacked = false;
            }
        }

        synchronized (this.scheduledTasks) {
            while (!this.scheduledTasks.isEmpty()) {
                this.scheduledTasks.poll().run();
            }
        }

    }

    private void onTickEnd() {
        if (!ConfigHandler.hackerDetector) return;
        FastbreakCheck.INSTANCE.onTickEnd();
    }

    /**
     * This gets called once per entity per tick.
     * Only gets called when the client plays on a server.
     * Hook is injected at end of {@link net.minecraft.world.World#updateEntityWithOptionalForce}
     */
    public void performChecksOnPlayer(EntityPlayer player) {
        if (mc.thePlayer == null ||
                player.ticksExisted < 20 ||
                player.isDead ||
                player.capabilities.isFlying ||
                player.capabilities.isCreativeMode ||
                player.isInvisible() ||
                ScoreboardTracker.isInSkyblock ||
                (!ScoreboardTracker.isReplayMode && NameUtil.filterNPC(player.getUniqueID()))) {
            return;
        }
        final long timeStart = System.nanoTime();
        if (player == mc.thePlayer) {
            FastbreakCheck.INSTANCE.checkPlayerSP(player);
            timeElapsedTemp += System.nanoTime() - timeStart;
            return;
        }
        final PlayerDataSamples data = ((EntityPlayerAccessor) player).getPlayerDataSamples();
        playersCheckedTemp++;
        if (data.updatedThisTick) return;
        data.onTick(player);
        if (ConfigHandler.debugLogging && playersToLog.contains(player.getName())) log(player, data);
        checkList.forEach(check -> check.performCheck(player, data));
        timeElapsedTemp += System.nanoTime() - timeStart;
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

    private void log(EntityPlayer player, PlayerDataSamples data) {
        logger.info(player.getName()
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

    public static void addScheduledTask(Runnable runnable) {
        if (runnable == null) return;
        synchronized (INSTANCE.scheduledTasks) {
            INSTANCE.scheduledTasks.add(runnable);
        }
    }

    public static void onEntitySwing(int attackerID) {
        HackerDetector.addScheduledTask(() -> {
            if (mc.theWorld == null) return;
            final Entity attacker = mc.theWorld.getEntityByID(attackerID);
            if (attacker instanceof EntityPlayerAccessor) {
                final PlayerDataSamples data = ((EntityPlayerAccessor) attacker).getPlayerDataSamples();
                data.customSwing = true;
                data.lastCustomSwingTime = -1;
            }
        });
    }

    public static void checkPlayerAttack(int attackerID, int targetId, int attackType) {
        HackerDetector.addScheduledTask(() -> {
            if (mc.theWorld == null) return;
            final Entity attacker = mc.theWorld.getEntityByID(attackerID);
            final Entity target = mc.theWorld.getEntityByID(targetId);
            if (!(attacker instanceof EntityPlayer) || !(target instanceof EntityPlayer) || attacker == target) {
                return;
            }
            if (attacker.getDistanceSqToEntity(target) > 64d) {
                return;
            }
            if (ScoreboardTracker.isInMwGame && ((EntityPlayerAccessor) attacker).getPlayerTeamColor() != '\0' && ((EntityPlayerAccessor) attacker).getPlayerTeamColor() == ((EntityPlayerAccessor) target).getPlayerTeamColor()) {
                return;
            }
            if (attackType == 1) { // swing and hurt packet received consecutively
                onPlayerAttack(((EntityPlayer) attacker), (EntityPlayer) target);
            } else if (attackType == 2) { // target hurt
                if (((EntityPlayer) attacker).swingProgressInt == -1 && ((EntityPlayer) target).hurtTime == 10) {
                    onPlayerAttack(((EntityPlayer) attacker), (EntityPlayer) target);
                }
            } else if (attackType == 4) { // target has crit particles
                if (((EntityPlayer) attacker).swingProgressInt == -1 && !attacker.onGround && attacker.ridingEntity == null) {
                    onPlayerAttack(((EntityPlayer) attacker), (EntityPlayer) target);
                }
            } else if (attackType == 5) { // target has sharp particles
                if (((EntityPlayer) attacker).swingProgressInt == -1) {
                    final ItemStack heldItem = ((EntityPlayer) attacker).getHeldItem();
                    if (heldItem != null) {
                        final Item item = heldItem.getItem();
                        if ((item instanceof ItemSword || item instanceof ItemTool) && heldItem.isItemEnchanted()) {
                            onPlayerAttack(((EntityPlayer) attacker), (EntityPlayer) target);
                        }
                    }
                }
            }
        });
    }

    private static void onPlayerAttack(EntityPlayer attacker, EntityPlayer target) {
        final PlayerDataSamples dataAttacked = ((EntityPlayerAccessor) attacker).getPlayerDataSamples();
        dataAttacked.hasAttacked = true;
        dataAttacked.targetedPlayer = target;
        ((EntityPlayerAccessor) target).getPlayerDataSamples().hasBeenAttacked = true;
    }

    public static void onEquipmentPacket(EntityPlayer player, S04PacketEntityEquipment packet) {
        final long timeStart = System.nanoTime();
        final ItemStack currentItemStack = player.inventory.armorInventory[packet.getEquipmentSlot() - 1];
        final ItemStack newItemStack = packet.getItemStack();
        if (currentItemStack != null && newItemStack != null) {
            if (currentItemStack.getItem() == newItemStack.getItem()) {
                final int newItemDamage = newItemStack.getItemDamage();
                final int currentItemDamage = currentItemStack.getItemDamage();
                if (newItemDamage > currentItemDamage || (newItemDamage == 0 && newItemDamage == currentItemDamage)) {
                    HackerDetector.addScheduledTask(() -> ((EntityPlayerAccessor) player).getPlayerDataSamples().armorDamaged = true);
                }
            }
        }
        INSTANCE.timeElapsedTemp += System.nanoTime() - timeStart;
    }

}
