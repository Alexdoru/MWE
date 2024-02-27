package fr.alexdoru.megawallsenhancementsmod.hackerdetector;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.EntityPlayerAccessor;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.checks.*;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.BrokenBlock;
import fr.alexdoru.megawallsenhancementsmod.hackerdetector.data.PlayerDataSamples;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.utils.FileLogger;
import fr.alexdoru.megawallsenhancementsmod.utils.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class HackerDetector {

    public static final HackerDetector INSTANCE = new HackerDetector();
    private static final FileLogger logger = new FileLogger("HackerDetector.log", "HH:mm:ss.SSS");
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

    public static void addScheduledTask(Runnable runnable) {
        if (runnable == null) return;
        synchronized (INSTANCE.scheduledTasks) {
            INSTANCE.scheduledTasks.add(runnable);
        }
    }

    public static void log(String message) {
        logger.log(message);
    }

}
