package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeybindingListener {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final KeyBinding killKey = new KeyBinding("/Kill", 0, "MegaWallsEnhancements");
    private static final KeyBinding surfaceKey = new KeyBinding("/Surface", 0, "MegaWallsEnhancements");
    private static final KeyBinding echestKey = new KeyBinding("/enderchest", 0, "MegaWallsEnhancements");
    private static final KeyBinding teamchestKey = new KeyBinding("/teamchest", 0, "MegaWallsEnhancements");
    private static final KeyBinding newNickKey = new KeyBinding("New Random Nick", 0, "MegaWallsEnhancements");
    private static final KeyBinding playerHitboxes = new KeyBinding("Toggle player hitboxes", 0, "Hitboxes");
    private static final KeyBinding toggleDroppedItemLimit = new KeyBinding("Toggle dropped item limit", 0, "MegaWallsEnhancements");

    public KeybindingListener() {
        ClientRegistry.registerKeyBinding(killKey);
        ClientRegistry.registerKeyBinding(surfaceKey);
        ClientRegistry.registerKeyBinding(echestKey);
        ClientRegistry.registerKeyBinding(teamchestKey);
        ClientRegistry.registerKeyBinding(newNickKey);
        ClientRegistry.registerKeyBinding(playerHitboxes);
        ClientRegistry.registerKeyBinding(toggleDroppedItemLimit);
    }

    @SubscribeEvent
    public void key(KeyInputEvent e) {

        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (killKey.isPressed() && !(mc.currentScreen instanceof GuiGameOver)) {
            mc.thePlayer.sendChatMessage("/kill");
        } else if (ScoreboardTracker.isPrepPhase && surfaceKey.isPressed()) {
            mc.thePlayer.sendChatMessage("/surface");
        } else if (ScoreboardTracker.isInMwGame && echestKey.isPressed()) {
            mc.thePlayer.sendChatMessage("/enderchest");
        } else if (ScoreboardTracker.isInMwGame && teamchestKey.isPressed()) {
            mc.thePlayer.sendChatMessage("/teamchest");
        } else if (toggleDroppedItemLimit.isPressed()) {
            ConfigHandler.limitDroppedEntityRendered = !ConfigHandler.limitDroppedEntityRendered;
            ConfigHandler.saveConfig();
            if (ConfigHandler.limitDroppedEntityRendered) {
                ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.YELLOW + "Limit dropped items rendered: " + EnumChatFormatting.GREEN + "On");
            } else {
                ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.YELLOW + "Limit dropped items rendered: " + EnumChatFormatting.RED + "Off");
            }
        } else if (playerHitboxes.isPressed()) {
            ConfigHandler.drawHitboxForPlayers = !ConfigHandler.drawHitboxForPlayers;
            ConfigHandler.saveConfig();
            if (ConfigHandler.drawHitboxForPlayers) {
                ChatUtil.addChatMessage(ChatUtil.getTagHitboxes() + EnumChatFormatting.WHITE + "Draw hitboxes for players: " + EnumChatFormatting.GREEN + "On");
            } else {
                ChatUtil.addChatMessage(ChatUtil.getTagHitboxes() + EnumChatFormatting.WHITE + "Draw hitboxes for players: " + EnumChatFormatting.RED + "Off");
            }
        }

    }

    public static KeyBinding getNewNickKey() {
        return newNickKey;
    }

}
