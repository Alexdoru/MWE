package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandWDR;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeybindingListener {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final KeyBinding newNickKey = new KeyBinding("New Random Nick", 0, "MegaWallsEnhancements");
    private static final KeyBinding playerHitboxes = new KeyBinding("Toggle player hitboxes", 0, "Hitboxes");
    private static final KeyBinding addTimestampKey = new KeyBinding("Add Timestamp", 0, "NoCheaters");
    private static final KeyBinding toggleDroppedItemLimit = new KeyBinding("Toggle dropped item limit", 0, "MegaWallsEnhancements");

    public KeybindingListener() {
        ClientRegistry.registerKeyBinding(newNickKey);
        ClientRegistry.registerKeyBinding(playerHitboxes);
        ClientRegistry.registerKeyBinding(addTimestampKey);
        ClientRegistry.registerKeyBinding(toggleDroppedItemLimit);
    }

    @SubscribeEvent
    public void key(KeyInputEvent e) {

        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (addTimestampKey.isPressed()) {
            CommandWDR.addTimeMark();
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
