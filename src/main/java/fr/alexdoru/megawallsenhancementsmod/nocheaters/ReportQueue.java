package fr.alexdoru.megawallsenhancementsmod.nocheaters;

import fr.alexdoru.megawallsenhancementsmod.asm.accessors.GuiChatAccessor;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatHandler;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.events.MegaWallsGameEvent;
import fr.alexdoru.megawallsenhancementsmod.features.PartyDetection;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class ReportQueue {

    public static ReportQueue INSTANCE;
    private static final Minecraft mc = Minecraft.getMinecraft();

    private int standStillCounter;
    private int standStillLimit = 22;
    private int movingCounter;
    private int betweenReportCounter;
    public final List<ReportInQueue> queueList = new ArrayList<>();
    private final Set<String> playersReportedThisGame = new HashSet<>();
    private final Random random = new Random();

    private static final long TIME_ABORPT_REPORT = 30_000L;

    public ReportQueue() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.START || queueList.isEmpty() || mc.thePlayer == null) {
            return;
        }

        if (queueList.get(0).time + TIME_ABORPT_REPORT < System.currentTimeMillis()) {
            queueList.remove(0);
            if (queueList.isEmpty()) return;
        }

        betweenReportCounter--;
        if (isPlayerStandingStill()) {
            standStillCounter++;
            if (standStillCounter >= getStandStillLimit()) {
                movingCounter = 0;
                final String playername = queueList.remove(0).name;
                final String msg = "/wdr " + playername;
                playersReportedThisGame.add(playername);
                ChatUtil.sendChatMessage(msg, true);
                standStillLimit = 20 + random.nextInt(11);
                standStillCounter = 0;
                betweenReportCounter = 50;
                ChatHandler.deleteStopMovingInstruction();
            }
        } else {
            standStillCounter = 0;
            if (movingCounter % 1800 == 0) {
                ChatHandler.printStopMovingInstruction();
            }
            movingCounter++;
        }

    }

    public int getStandStillCounter() {
        return standStillCounter;
    }

    public int getStandStillLimit() {
        return standStillLimit + Math.max(0, betweenReportCounter);
    }

    @SubscribeEvent
    public void onMegaWallsGameEvent(MegaWallsGameEvent event) {
        if (event.getType() == MegaWallsGameEvent.EventType.GAME_START || event.getType() == MegaWallsGameEvent.EventType.GAME_END) {
            playersReportedThisGame.clear();
        }
    }

    public void addReportToQueue(String playername) {
        if (!playersReportedThisGame.contains(playername)) {
            for (int i = 0; i < queueList.size(); i++) {
                final ReportInQueue report = queueList.get(i);
                if (report.name.equalsIgnoreCase(playername)) {
                    report.time = System.currentTimeMillis();
                    queueList.sort(Comparator.comparingLong(r -> r.time));
                    return;
                }
            }
            PartyDetection.printBoostingReportAdvice(playername);
            queueList.add(new ReportInQueue(playername));
        }
    }

    public void addPlayerReportedThisGame(String playername) {
        playersReportedThisGame.add(playername);
        removePlayerFromReportQueue(playername);
    }

    public void removePlayerFromReportQueue(String playername) {
        queueList.removeIf(report -> (report.name.equalsIgnoreCase(playername)));
    }

    private int prevItemHeld;

    private boolean isPlayerStandingStill() {
        final boolean sameItem = mc.thePlayer.inventory.currentItem == prevItemHeld;
        prevItemHeld = mc.thePlayer.inventory.currentItem;
        return (mc.inGameHasFocus || mc.currentScreen instanceof GuiChatAccessor && ((GuiChatAccessor) mc.currentScreen).getInputField().getText().isEmpty())
                && mc.thePlayer.movementInput.moveForward == 0.0F
                && mc.thePlayer.movementInput.moveStrafe == 0.0F
                && !mc.thePlayer.movementInput.jump
                && !mc.thePlayer.movementInput.sneak
                && !mc.gameSettings.keyBindAttack.isKeyDown()
                && !mc.gameSettings.keyBindUseItem.isKeyDown()
                && mc.thePlayer.prevRotationYawHead == mc.thePlayer.rotationYawHead
                && sameItem;
    }

    public static class ReportInQueue {
        public long time = System.currentTimeMillis();
        public final String name;

        public ReportInQueue(String name) {
            this.name = name;
        }
    }

}
