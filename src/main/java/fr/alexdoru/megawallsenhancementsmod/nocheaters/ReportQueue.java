package fr.alexdoru.megawallsenhancementsmod.nocheaters;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatHandler;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.events.MegaWallsGameEvent;
import fr.alexdoru.megawallsenhancementsmod.features.PartyDetection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class ReportQueue {

    public static ReportQueue INSTANCE;
    private static final Minecraft mc = Minecraft.getMinecraft();

    private int standStillCounter;
    private int standStillLimit = 25;
    private int movingCounter;
    private int betweenReportCounter;
    public final List<String> queueList = new ArrayList<>();
    private final Set<String> playersReportedThisGame = new HashSet<>();
    private final Random random = new Random();

    public ReportQueue() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.START || queueList.isEmpty() || mc.thePlayer == null) {
            return;
        }

        betweenReportCounter--;
        if (isPlayerStandingStill()) {
            standStillCounter++;
            if (standStillCounter >= getStandStillLimit()) {
                movingCounter = 0;
                final String playername = queueList.remove(0);
                final String msg = "/wdr " + playername;
                ChatUtil.sendChatMessage(msg, true);
                standStillLimit = 22 + random.nextInt(12);
                standStillCounter = 0;
                betweenReportCounter = 50;
                ChatHandler.deleteStopMovingInstruction();
            }
        } else {
            standStillCounter = 0;
            incrementMovingCounter();
        }

    }

    public int getStandStillCounter() {
        return standStillCounter;
    }

    public int getStandStillLimit() {
        return standStillLimit + Math.max(0, betweenReportCounter);
    }

    private void incrementMovingCounter() {
        if (movingCounter % 1800 == 0) {
            ChatHandler.printStopMovingInstruction();
        }
        movingCounter++;
    }

    @SubscribeEvent
    public void onMegaWallsGameEvent(MegaWallsGameEvent event) {
        if (event.getType() == MegaWallsGameEvent.EventType.GAME_START || event.getType() == MegaWallsGameEvent.EventType.GAME_END) {
            playersReportedThisGame.clear();
        }
    }

    public void addReportToQueue(String playername) {
        if (canReportPlayerThisGame(playername)) {
            PartyDetection.printBoostingReportAdvice(playername);
            queueList.add(playername);
        }
    }

    public void addPlayerReportedThisGame(String playername) {
        playersReportedThisGame.add(playername);
        removePlayerFromReportQueue(playername);
    }

    public void removePlayerFromReportQueue(String playername) {
        queueList.removeIf(p -> (p.equalsIgnoreCase(playername)));
    }

    /**
     * Check if a report was already sent for this player
     */
    private boolean canReportPlayerThisGame(String playername) {
        return playersReportedThisGame.add(playername);
    }

    private int prevItemHeld;

    private boolean isPlayerStandingStill() {
        final boolean sameItem = mc.thePlayer.inventory.currentItem == prevItemHeld;
        prevItemHeld = mc.thePlayer.inventory.currentItem;
        return (mc.inGameHasFocus || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiIngameMenu)
                && mc.thePlayer.movementInput.moveForward == 0.0F
                && mc.thePlayer.movementInput.moveStrafe == 0.0F
                && !mc.thePlayer.movementInput.jump
                && !mc.thePlayer.movementInput.sneak
                && !mc.gameSettings.keyBindAttack.isKeyDown()
                && !mc.gameSettings.keyBindUseItem.isKeyDown()
                && mc.thePlayer.prevRotationYawHead == mc.thePlayer.rotationYawHead
                && sameItem;
    }

}
