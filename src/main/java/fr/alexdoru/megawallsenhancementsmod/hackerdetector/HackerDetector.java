package fr.alexdoru.megawallsenhancementsmod.hackerdetector;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.*;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.BrokenBlock;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.TickingBlockMap;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.FileLogger;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class HackerDetector {

    private static final Minecraft mc = Minecraft.getMinecraft();
    public static HackerDetector INSTANCE;
    private static FileLogger logger;
    private final List<ICheck> checkList = new ArrayList<>();
    private long timeElapsedTemp = 0L;
    private long timeElapsed = 0L;
    private int playersChecked = 0;
    private int playersCheckedTemp = 0;
    /** Data about blocks broken during this tick */
    private final List<BrokenBlock> brokenBlocksList = new ArrayList<>();
    private final TickingBlockMap recentPlacedBlocks = new TickingBlockMap();
    private final Queue<Runnable> scheduledTasks = new ArrayDeque<>();
    public final Set<String> playersToLog = new HashSet<>();

    private final FastbreakCheck fastbreakCheck;

    static {
        if (ConfigHandler.debugLogging) initLogger();
    }

    public HackerDetector() {
        INSTANCE = this;
        this.checkList.add(new AutoblockCheck());
        this.checkList.add(this.fastbreakCheck = new FastbreakCheck(brokenBlocksList));
        this.checkList.add(new GhosthandCheck());
        this.checkList.add(new KeepSprintACheck());
        this.checkList.add(new KeepSprintBCheck());
        this.checkList.add(new KillAuraACheck(recentPlacedBlocks));
        this.checkList.add(new KillAuraBCheck());
        this.checkList.add(new NoSlowdownCheck());
        this.checkList.add(new ScaffoldCheck());
    }

    @SubscribeEvent
    public void onDrawDebugText(RenderGameOverlayEvent.Text event) {
        if (mc.gameSettings.showDebugInfo && ConfigHandler.hackerDetector) {
            event.left.add("");
            event.left.add("Hacker Detector:");
            event.left.add("Block Cache: " + brokenBlocksList.size() + "/" + recentPlacedBlocks.size());
            event.left.add("Player" + (playersChecked > 1 ? "s" : "") + " checked: " + playersChecked);
            event.left.add("Time elapsed (ns/s): " + ChatUtil.formatLong(timeElapsed));
            final double fpsLost = (timeElapsed / (10e9d - timeElapsed)) * Minecraft.getDebugFPS();
            event.left.add("Impact on performance: -" + String.format("%.2f", fpsLost) + "fps");
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
            if (player.ticksExisted >= 20 && !player.isDead && isValidPlayer(player.getUniqueID())) {
                // this includes the watchdog bot above the player
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

    public static boolean isValidPlayer(UUID uuid) {
        final int v = uuid.version();
        if (ScoreboardTracker.isReplayMode) {
            return v == 2;
        }
        return v == 1 || v == 4;
    }

    private void onTickEnd() {
        if (ConfigHandler.hackerDetector) {
            this.fastbreakCheck.onTickEnd();
            this.brokenBlocksList.clear();
            this.recentPlacedBlocks.onTick();
        }
    }

    private void performChecksOnPlayer(EntityPlayer player) {
        if (player == mc.thePlayer) {
            this.fastbreakCheck.checkPlayerSP(player);
            return;
        }
        final PlayerDataSamples data = ((EntityPlayerAccessor) player).getPlayerDataSamples();
        if (data.checkedThisTick) return;
        data.onTick(player);
        for (final ICheck check : this.checkList) {
            check.performCheck(player, data);
        }
        data.onPostChecks();
        this.playersCheckedTemp++;
    }

    public static void addScheduledTask(Runnable runnable) {
        if (runnable == null) return;
        synchronized (INSTANCE.scheduledTasks) {
            INSTANCE.scheduledTasks.add(runnable);
        }
    }

    public static void log(String message) {
        if (logger == null) initLogger();
        logger.log(message);
    }

    private static void initLogger() {
        logger = new FileLogger("HackerDetector.log", "HH:mm:ss.SSS");
    }

    public static void addBrokenBlock(Block block, BlockPos blockPos, String tool) {
        HackerDetector.INSTANCE.brokenBlocksList.add(new BrokenBlock(block, blockPos, tool));
    }

    public static void addPlacedBlock(BlockPos pos, IBlockState state) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        final double xDiff = Math.abs(mc.thePlayer.posX - pos.getX());
        final double zDiff = Math.abs(mc.thePlayer.posZ - pos.getZ());
        if (xDiff > 70D || zDiff > 70D) {
            return;
        }
        if (!state.getBlock().isFullBlock() || !state.getBlock().canCollideCheck(state, false)) {
            return;
        }
        if (mc.theWorld.getBlockState(pos).getBlock().getMaterial() == Material.air) {
            INSTANCE.recentPlacedBlocks.add(pos);
        }
    }

    public static void onPlayerBlockPacket(BlockPos pos, int placedBlockDirectionIn, Block block) {
        if (block == null || !block.isFullBlock() || !block.canCollideCheck(block.getDefaultState(), false)) {
            return;
        }
        final EnumFacing enumfacing = EnumFacing.getFront(placedBlockDirectionIn);
        if (enumfacing == null) return;
        INSTANCE.recentPlacedBlocks.add(pos.add(enumfacing.getDirectionVec()));
    }

}
