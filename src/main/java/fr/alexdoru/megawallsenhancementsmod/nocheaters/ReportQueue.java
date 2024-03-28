package fr.alexdoru.megawallsenhancementsmod.nocheaters;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatHandler;
import fr.alexdoru.megawallsenhancementsmod.events.MegaWallsGameEvent;
import fr.alexdoru.megawallsenhancementsmod.features.PartyDetection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class ReportQueue {

    public static ReportQueue INSTANCE;
    private static final Minecraft mc = Minecraft.getMinecraft();

    public int standingStillCounter;
    public int standingStillLimit = 55;
    private int movingCounter;
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

        if (isPlayerStandingStill(mc.thePlayer)) {
            standingStillCounter++;
            if (standingStillCounter >= standingStillLimit) {
                movingCounter = 0;
                final String playername = queueList.remove(0);
                final String msg = "/wdr " + playername;
                mc.ingameGUI.getChatGUI().addToSentMessages(msg);
                mc.thePlayer.sendChatMessage(msg);
                standingStillLimit = 50 + random.nextInt(15);
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
        if (playersReportedThisGame.contains(playername)) {
            return false;
        }
        playersReportedThisGame.add(playername);
        return true;
    }

    private int prevItemHeld;

    private boolean isPlayerStandingStill(EntityPlayerSP thePlayer) {
        final boolean sameItem = mc.thePlayer.inventory.currentItem == prevItemHeld;
        prevItemHeld = mc.thePlayer.inventory.currentItem;
        return (mc.inGameHasFocus || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiIngameMenu)
                && thePlayer.movementInput.moveForward == 0.0F
                && thePlayer.movementInput.moveStrafe == 0.0F
                && !thePlayer.movementInput.jump
                && !thePlayer.movementInput.sneak
                && !mc.gameSettings.keyBindAttack.isKeyDown()
                && !mc.gameSettings.keyBindUseItem.isKeyDown()
                && mc.thePlayer.prevRotationYawHead == mc.thePlayer.rotationYawHead
                && sameItem;
    }

}
