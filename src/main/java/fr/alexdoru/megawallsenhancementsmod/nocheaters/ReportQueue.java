package fr.alexdoru.megawallsenhancementsmod.nocheaters;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatHandler;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.data.WDR;
import fr.alexdoru.megawallsenhancementsmod.events.MegaWallsGameEvent;
import fr.alexdoru.megawallsenhancementsmod.features.PartyDetection;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.PositionEditGuiScreen;
import fr.alexdoru.megawallsenhancementsmod.gui.huds.PendingReportHUD;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class ReportQueue {

    public static final ReportQueue INSTANCE = new ReportQueue();
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final int TIME_BETWEEN_REPORTS_MAX = 5 * 60 * 20;
    private static final int TIME_BETWEEN_REPORTS_MIN = 3 * 60 * 20;
    private static final int AUTOREPORT_PER_GAME = 2;

    private int counter;
    public int standingStillCounter;
    public int standingStillLimit = 35;
    private int movingCounter;
    private int autoReportSent;
    public final List<ReportInQueue> queueList = new ArrayList<>();
    private final List<Long> timestampsLastReports = new ArrayList<>();
    private final Set<String> playersReportedThisGame = new HashSet<>();
    private final Random random = new Random();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        if (counter <= 0 && !queueList.isEmpty() && mc.thePlayer != null) {
            if (isPlayerStandingStill(mc.thePlayer)) {
                standingStillCounter++;
                if (standingStillCounter >= standingStillLimit) {
                    movingCounter = 0;
                    final int index = getIndexOfNextReportToSend();
                    final ReportInQueue reportInQueue = queueList.remove(index == -1 ? 0 : index);
                    final String playername = reportInQueue.reportedPlayer;
                    if (reportInQueue.isReportSuggestion || reportInQueue.isReportFromHackerDetector || ScoreboardTracker.isInMwGame) {
                        final String msg = "/wdr " + playername;
                        mc.thePlayer.sendChatMessage(msg);
                    }
                    counter = getNextCounterDelay();
                    standingStillLimit = 30 + random.nextInt(12);
                    standingStillCounter = 0;
                    ChatHandler.deleteStopMovingInstruction();
                } else {
                    incrementMovingCounter();
                }
            } else {
                standingStillCounter = 0;
                incrementMovingCounter();
            }
        }

        if (isReportQueueInactive()) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }

        counter--;

    }

    private void incrementMovingCounter() {
        if (movingCounter % 1800 == 0) {
            ChatHandler.printStopMovingInstruction();
        }
        movingCounter++;
    }

    private int getNextCounterDelay() {
        if (doesQueueHaveSpecialReport() || queueList.isEmpty()) {
            return 5 + random.nextInt(5);
        } else {
            final int i = TIME_BETWEEN_REPORTS_MAX - 12 * 20 * (queueList.size() - 1);
            return (int) ((10d * random.nextGaussian() / 6d) + Math.max(i, TIME_BETWEEN_REPORTS_MIN));
        }
    }

    public int getIndexOfNextReportToSend() {
        for (int i = 0; i < queueList.size(); i++) {
            final ReportInQueue reportInQueue = queueList.get(i);
            if (reportInQueue.isReportSuggestion || reportInQueue.isReportFromHackerDetector) {
                return i;
            }
        }
        return -1;
    }

    private boolean doesQueueHaveSpecialReport() {
        for (final ReportInQueue reportInQueue : queueList) {
            if (reportInQueue.isReportSuggestion || reportInQueue.isReportFromHackerDetector) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT && !(mc.currentScreen instanceof PositionEditGuiScreen)) {
            mc.mcProfiler.startSection("MWE HUD");
            if (PendingReportHUD.instance.isEnabled(0L)) {
                PendingReportHUD.instance.render(event.resolution);
            }
            mc.mcProfiler.endSection();
        }
    }

    @SubscribeEvent
    public void onGameEnd(MegaWallsGameEvent event) {
        if (event.getType() == MegaWallsGameEvent.EventType.GAME_END) {
            queueList.removeIf(reportInQueue -> !reportInQueue.isReportSuggestion && !reportInQueue.isReportFromHackerDetector);
        }
    }

    /**
     * Gaussian
     * Min value : 5 seconds
     * Avg value : 11 seconds
     */
    private int getTickDelay() {
        return (int) (100d + Math.abs(50d * random.nextGaussian() + 120d));
    }

    /**
     * Called from the auto-report suggestions
     * if an auto-report for that player is already in the queue, it prioritizes it
     * Returns true if it adds the players to the report queue
     */
    public boolean addReportSuggestionToQueue(String suggestionSender, String reportedPlayer) {
        if (canReportPlayerThisGame(reportedPlayer)) {
            queueList.removeIf(reportInQueue -> (reportInQueue.reportedPlayer.equalsIgnoreCase(reportedPlayer)));
            if (isReportQueueInactive()) {
                MinecraftForge.EVENT_BUS.register(this);
            }
            counter = getTickDelay();
            queueList.add(new ReportInQueue(suggestionSender, reportedPlayer, true));
            return true;
        }
        return false;
    }

    /**
     * Handles the auto report feature
     *
     * @return true if it sends a report
     */
    public boolean addAutoReportToQueue(long datenow, String playername, WDR wdr) {
        if (autoReportSent < AUTOREPORT_PER_GAME && wdr.canBeAutoreported(datenow) && wdr.hasValidCheats()) {
            if (canReportPlayerThisGame(playername)) {
                wdr.timestamp = datenow;
                if (isReportQueueInactive()) {
                    MinecraftForge.EVENT_BUS.register(this);
                    counter = random.nextInt(TIME_BETWEEN_REPORTS_MAX);
                }
                queueList.add(new ReportInQueue(playername));
                autoReportSent++;
                return true;
            }
        }
        return false;
    }

    public void addReportFromHackerDetector(String playername, String cheat) {
        if (canReportPlayerThisGame(playername)) {
            PartyDetection.printBoostingReportAdvice(playername);
            if (isReportQueueInactive()) {
                MinecraftForge.EVENT_BUS.register(this);
                counter = 0;
            }
            queueList.add(new ReportInQueue(playername, cheat));
        }
    }

    private boolean isReportQueueInactive() {
        return counter <= 0 && queueList.isEmpty();
    }

    public void clearReportsSentBy(String playername) {
        final StringBuilder stringBuilder = new StringBuilder();
        final Iterator<ReportInQueue> iterator = queueList.iterator();
        while (iterator.hasNext()) {
            final ReportInQueue reportInQueue = iterator.next();
            if (reportInQueue.messageSender != null && reportInQueue.messageSender.equals(playername)) {
                stringBuilder.append(" ").append(reportInQueue.reportedPlayer);
                iterator.remove();
            }
        }
        final String msg = stringBuilder.toString();
        if (!msg.equals("")) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "Removed reports targeting :" + EnumChatFormatting.GOLD + msg);
        }
    }

    public void clearSuggestionsInReportQueue() {
        final StringBuilder stringBuilder = new StringBuilder();
        final Iterator<ReportInQueue> iterator = queueList.iterator();
        while (iterator.hasNext()) {
            final ReportInQueue reportInQueue = iterator.next();
            if (reportInQueue.messageSender != null) {
                stringBuilder.append(" ").append(reportInQueue.reportedPlayer);
                iterator.remove();
            }
        }
        final String msg = stringBuilder.toString();
        if (!msg.equals("")) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "Removed reports targeting :" + EnumChatFormatting.GOLD + msg);
        }
    }

    public void clearReportsFor(String reportedPlayer) {
        final StringBuilder stringBuilder = new StringBuilder();
        final Iterator<ReportInQueue> iterator = queueList.iterator();
        while (iterator.hasNext()) {
            final ReportInQueue reportInQueue = iterator.next();
            if (reportInQueue.reportedPlayer != null && reportInQueue.reportedPlayer.equals(reportedPlayer)) {
                stringBuilder.append(" ").append(reportInQueue.reportedPlayer);
                iterator.remove();
            }
        }
        final String msg = stringBuilder.toString();
        if (!msg.equals("")) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.GREEN + "Removed all reports targeting :" + EnumChatFormatting.GOLD + msg);
        }
    }

    /**
     * Called everytime a report is sent to check that the player doesn't send too many reports in a short time
     */
    public void addReportTimestamp(boolean manualReport) {
        final long l = System.currentTimeMillis();
        timestampsLastReports.removeIf(o -> (o + 2 * 60 * 1000L < l));
        if (manualReport && timestampsLastReports.size() > 4) {
            ChatUtil.addChatMessage(ChatUtil.getTagNoCheaters() + EnumChatFormatting.RED + "Don't report too many players at once or Hypixel will ignore your reports thinking you are a bot trying to flood their system");
        }
        timestampsLastReports.add(l);
    }

    public void clearPlayersReportedThisGame() {
        playersReportedThisGame.clear();
        autoReportSent = 0;
    }

    public void addPlayerReportedThisGame(String playername) {
        playersReportedThisGame.add(playername);
        queueList.removeIf(reportInQueue -> (reportInQueue.reportedPlayer.equalsIgnoreCase(playername)));
    }

    /**
     * Check if a report was already sent for this player
     */
    private boolean canReportPlayerThisGame(String playername) {
        if (playersReportedThisGame.contains(playername)) {
            return false;
        }
        playersReportedThisGame.add(playername);
        return true;
    }

    public boolean wasPlayerReportedThisGame(String playername) {
        return playersReportedThisGame.contains(playername);
    }

    private boolean isPlayerStandingStill(EntityPlayerSP thePlayer) {
        return (mc.inGameHasFocus || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiIngameMenu)
                && thePlayer.movementInput.moveForward == 0.0F
                && thePlayer.movementInput.moveStrafe == 0.0F
                && !thePlayer.movementInput.jump
                && !thePlayer.movementInput.sneak
                && !mc.gameSettings.keyBindAttack.isKeyDown()
                && !mc.gameSettings.keyBindUseItem.isKeyDown()
                && mc.thePlayer.prevRotationYaw == mc.thePlayer.rotationYaw
                && mc.thePlayer.prevRotationPitch == mc.thePlayer.rotationPitch;
    }

    public static class ReportInQueue {

        public final String messageSender;
        public final String reportedPlayer;
        public final boolean isReportSuggestion;
        public final boolean isReportFromHackerDetector;
        public final String cheat;

        public ReportInQueue(String playername) {
            this.messageSender = null;
            this.reportedPlayer = playername;
            this.isReportSuggestion = false;
            this.isReportFromHackerDetector = false;
            this.cheat = null;
        }

        public ReportInQueue(String playername, String cheat) {
            this.messageSender = null;
            this.reportedPlayer = playername;
            this.isReportSuggestion = false;
            this.isReportFromHackerDetector = true;
            this.cheat = cheat;
        }

        public ReportInQueue(String messageSender, String reportedPlayer, boolean isReportSuggestion) {
            this.messageSender = messageSender;
            this.reportedPlayer = reportedPlayer;
            this.isReportSuggestion = isReportSuggestion;
            this.isReportFromHackerDetector = false;
            this.cheat = null;
        }

    }

}
