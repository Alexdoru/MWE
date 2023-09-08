package fr.alexdoru.megawallsenhancementsmod.hackerdetector;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.*;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.BrokenBlock;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class HackerDetector {

    public static final HackerDetector INSTANCE = new HackerDetector();
    private static PrintStream printStream;
    /** Field stolen from EntityLivingBase */
    public static final UUID sprintingUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final List<ICheck> checkList = new ArrayList<>();
    private long timeElapsedTemp = 0L;
    private long timeElapsed = 0L;
    private int playersChecked = 0;
    private int playersCheckedTemp = 0;
    /** Data about blocks broken during this tick */
    public final List<BrokenBlock> brokenBlocksList = new ArrayList<>();
    public final HashSet<String> playersToLog = new HashSet<>();
    private final Queue<Runnable> scheduledTasks = new ArrayDeque<>();

    static {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        initPrintStream();
    }

    private final FastbreakCheck fastbreakCheck;

    private HackerDetector() {
        this.checkList.add(new AutoblockCheck());
        this.checkList.add(this.fastbreakCheck = new FastbreakCheck());
        this.checkList.add(new KeepsprintCheck());
        this.checkList.add(new KillAuraCheck());
        this.checkList.add(new NoSlowdownCheck());
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
            this.playersCheckedTemp = 0;
            final long timeStart = System.nanoTime();
            this.onTickStart();
            this.timeElapsedTemp += System.nanoTime() - timeStart;
            return;
        }
        if (event.phase == TickEvent.Phase.END) {
            final long timeStart = System.nanoTime();
            this.onTickEnd();
            this.timeElapsedTemp += System.nanoTime() - timeStart;
            if (mc.thePlayer != null && mc.thePlayer.ticksExisted % 20 == 0) {
                this.timeElapsed = this.timeElapsedTemp;
                this.timeElapsedTemp = 0L;
            }
            this.playersChecked = this.playersCheckedTemp;
        }
    }

    private void onTickStart() {

        if (!ConfigHandler.hackerDetector || ScoreboardTracker.isInSkyblock || mc.theWorld == null || mc.thePlayer == null || !mc.theWorld.isRemote) {
            synchronized (this.scheduledTasks) {
                this.scheduledTasks.clear();
            }
            return;
        }

        final List<EntityPlayer> playerList = new ArrayList<>(mc.theWorld.playerEntities.size());

        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (player.ticksExisted >= 20 &&
                    !player.isDead &&
                    !player.capabilities.isFlying &&
                    !player.capabilities.isCreativeMode &&
                    !player.isInvisible() &&
                    (ScoreboardTracker.isReplayMode || !NameUtil.filterNPC(player.getUniqueID()))) {
                playerList.add(player);
                ((EntityPlayerAccessor) player).getPlayerDataSamples().onTickStart();
            }
        }

        synchronized (this.scheduledTasks) {
            while (!this.scheduledTasks.isEmpty()) {
                this.scheduledTasks.poll().run();
            }
        }

        for (final EntityPlayer player : playerList) {
            this.performChecksOnPlayer(player);
        }

    }

    private void onTickEnd() {
        if (ConfigHandler.hackerDetector) {
            this.fastbreakCheck.onTickEnd();
        }
    }

    private void performChecksOnPlayer(EntityPlayer player) {
        if (player == mc.thePlayer) {
            this.fastbreakCheck.checkPlayerSP(player);
            return;
        }
        final PlayerDataSamples data = ((EntityPlayerAccessor) player).getPlayerDataSamples();
        data.onTick(player);
        for (final ICheck check : this.checkList) {
            check.performCheck(player, data);
        }
        this.playersCheckedTemp++;
    }

    private static void addScheduledTask(Runnable runnable) {
        if (runnable == null) return;
        synchronized (INSTANCE.scheduledTasks) {
            INSTANCE.scheduledTasks.add(runnable);
        }
    }

    public static void onEntitySwing(int attackerID) {
        HackerDetector.addScheduledTask(() -> {
            final Entity attacker = mc.theWorld.getEntityByID(attackerID);
            if (attacker instanceof EntityPlayerAccessor) {
                final PlayerDataSamples data = ((EntityPlayerAccessor) attacker).getPlayerDataSamples();
                data.hasSwung = true;
                data.lastSwingTime = -1;
            }
        });
    }

    public static void checkPlayerAttack(int attackerID, int targetId, int attackType) {
        HackerDetector.addScheduledTask(() -> {
            final Entity attacker = mc.theWorld.getEntityByID(attackerID);
            final Entity target = mc.theWorld.getEntityByID(targetId);
            if (!(attacker instanceof EntityPlayer) || !(target instanceof EntityPlayer) || attacker == target) {
                return;
            }
            // discard attacks when the target is near the
            // entity render distance since the attacker might
            // not be loaded on my client
            final double xDiff = mc.thePlayer.posX - target.posX;
            final double zDiff = mc.thePlayer.posZ - target.posZ;
            if (xDiff < -56D || xDiff > 56D || zDiff < -56D || zDiff > 56D) return;
            if (attacker.getDistanceSqToEntity(target) > 64d) {
                return;
            }
            if (ScoreboardTracker.isInMwGame && ((EntityPlayerAccessor) attacker).getPlayerTeamColor() != '\0' && ((EntityPlayerAccessor) attacker).getPlayerTeamColor() == ((EntityPlayerAccessor) target).getPlayerTeamColor()) {
                return;
            }
            switch (attackType) {
                case 0:  // velocity packet
                    if (mc.thePlayer == target) {
                        onPlayerAttack((EntityPlayer) attacker, mc.thePlayer, "velocity");
                    }
                    break;
                case 1:  // swing and hurt packet received consecutively
                    onPlayerAttack((EntityPlayer) attacker, (EntityPlayer) target, "attack");
                    break;
                case 2:  // target hurt
                    // when an ability does damage to multiple players, this can fire multiple times
                    // on different players for the same attacker
                    if (((EntityPlayer) attacker).swingProgressInt == -1 && ((EntityPlayer) target).hurtTime == 10) {
                        onPlayerAttack((EntityPlayer) attacker, (EntityPlayer) target, "hurt");
                    }
                    break;
                case 4:  // target has crit particles
                    if (((EntityPlayer) attacker).swingProgressInt == -1 && !attacker.onGround && attacker.ridingEntity == null) {
                        onPlayerAttack((EntityPlayer) attacker, (EntityPlayer) target, "critical");
                    }
                    break;
                case 5:  // target has sharp particles
                    if (((EntityPlayer) attacker).swingProgressInt == -1) {
                        final ItemStack heldItem = ((EntityPlayer) attacker).getHeldItem();
                        if (heldItem != null) {
                            final Item item = heldItem.getItem();
                            if ((item instanceof ItemSword || item instanceof ItemTool) && heldItem.isItemEnchanted()) {
                                onPlayerAttack((EntityPlayer) attacker, (EntityPlayer) target, "sharpness");
                            }
                        }
                    }
                    break;
            }
        });
    }

    private static void onPlayerAttack(EntityPlayer attacker, EntityPlayer target, String attackType) {
        final PlayerDataSamples dataAttacker = ((EntityPlayerAccessor) attacker).getPlayerDataSamples();
        if (dataAttacker.hasAttackedMultiTarget) {
            return;
        }
        if (dataAttacker.targetedPlayer != null && dataAttacker.targetedPlayer != target) {
            dataAttacker.hasAttackedMultiTarget = true;
            dataAttacker.hasAttacked = false;
            dataAttacker.targetedPlayer = null;
            ((EntityPlayerAccessor) target).getPlayerDataSamples().hasBeenAttacked = false;
            return;
        }
        dataAttacker.hasAttacked = true;
        dataAttacker.targetedPlayer = target;
        ((EntityPlayerAccessor) target).getPlayerDataSamples().hasBeenAttacked = true;
        if (ConfigHandler.debugLogging) {
            log(attacker.getName() + " attacked " + target.getName() + " [" + attackType + "]");
        }
    }

    private static void initPrintStream() {
        final File logsFolder = new File(Minecraft.getMinecraft().mcDataDir, "logs");
        //noinspection ResultOfMethodCallIgnored
        logsFolder.mkdirs();
        final File logFile = new File(logsFolder, "HackerDetector.log");
        if (logFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            logFile.delete();
        }
        if (!logFile.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            printStream = new PrintStream(new FileOutputStream(logFile, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void log(String message) {
        if (printStream == null) return;
        final String time = new SimpleDateFormat("HH:mm:ss.SSS").format(System.currentTimeMillis());
        printStream.println("[" + time + "] " + message);
    }

}
