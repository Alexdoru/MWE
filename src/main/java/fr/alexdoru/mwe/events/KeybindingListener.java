package fr.alexdoru.mwe.events;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.api.IScoreboardParser;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import org.lwjgl.input.Mouse;

public final class KeybindingListener {

    private final IScoreboardParser parser = ScoreboardTracker.getParser();

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent e) {
        this.handleKeybinds();
    }

    @SubscribeEvent
    public void onMouseInput(MouseInputEvent e) {
        if (Mouse.getEventButtonState()) {
            this.handleKeybinds();
        }
    }

    private void handleKeybinds() {

        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (MWEConfig.killKey.isPressed() && this.parser.isInMwGame() && !this.parser.isPrepPhase() && !(mc.currentScreen instanceof GuiGameOver)) {
            mc.thePlayer.sendChatMessage("/kill");
        } else if (MWEConfig.surfaceKey.isPressed() && this.parser.isPrepPhase()) {
            mc.thePlayer.sendChatMessage("/surface");
        } else if (MWEConfig.echestKey.isPressed() && this.parser.isInMwGame()) {
            mc.thePlayer.sendChatMessage("/enderchest");
        } else if (MWEConfig.teamchestKey.isPressed() && this.parser.isInMwGame()) {
            mc.thePlayer.sendChatMessage("/teamchest");
        } else if (MWEConfig.warcryKey.isPressed() && this.parser.isInMwGame() && !this.parser.isPrepPhase()) {
            mc.thePlayer.sendChatMessage("/warcry");
        } else if (MWEConfig.toggleSprintKey.isPressed()) {
            this.toggleSprint();
        } else if (MWEConfig.toggleDroppedItemLimit.isPressed()) {
            this.toggleDroppedItemLimit();
        } else if (MWEConfig.playerHitboxes.isPressed()) {
            this.togglePlayerHitboxes();
        }

    }

    private void toggleSprint() {
        MWEConfig.toggleSprint = !MWEConfig.toggleSprint;
        MWE.INSTANCE().getConfigHandler().saveConfig();
        if (MWEConfig.toggleSprint) {
            ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Toggle Sprint On");
        } else {
            ChatUtil.addChatMessage(EnumChatFormatting.RED + "Toggle Sprint Off");
        }
    }

    private void toggleDroppedItemLimit() {
        MWEConfig.limitDroppedEntityRendered = !MWEConfig.limitDroppedEntityRendered;
        MWE.INSTANCE().getConfigHandler().saveConfig();
        if (MWEConfig.limitDroppedEntityRendered) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.YELLOW + "Limit dropped items rendered: " + EnumChatFormatting.GREEN + "On");
        } else {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.YELLOW + "Limit dropped items rendered: " + EnumChatFormatting.RED + "Off");
        }
    }

    private void togglePlayerHitboxes() {
        MWEConfig.drawHitboxForPlayers = !MWEConfig.drawHitboxForPlayers;
        MWE.INSTANCE().getConfigHandler().saveConfig();
        if (MWEConfig.drawHitboxForPlayers) {
            ChatUtil.addChatMessage(ChatUtil.getTagHitboxes() + EnumChatFormatting.WHITE + "Draw hitboxes for players: " + EnumChatFormatting.GREEN + "On");
        } else {
            ChatUtil.addChatMessage(ChatUtil.getTagHitboxes() + EnumChatFormatting.WHITE + "Draw hitboxes for players: " + EnumChatFormatting.RED + "Off");
        }
    }

}
