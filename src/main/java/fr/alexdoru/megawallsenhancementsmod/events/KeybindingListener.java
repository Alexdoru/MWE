package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandWDR;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeybindingListener {

    private static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void key(KeyInputEvent e) {

        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (MegaWallsEnhancementsMod.addTimestampKey.isPressed()) {
            CommandWDR.addTimeMark();
        } else if (MegaWallsEnhancementsMod.toggleDroppedItemLimit.isPressed()) {
            ConfigHandler.limitDroppedEntityRendered = !ConfigHandler.limitDroppedEntityRendered;
            ConfigHandler.saveConfig();
            if (ConfigHandler.limitDroppedEntityRendered) {
                ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.WHITE + "Limit dropped items rendered: " + EnumChatFormatting.GREEN + "On");
            } else {
                ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.WHITE + "Limit dropped items rendered: " + EnumChatFormatting.RED + "Off");
            }
        }

    }

}
