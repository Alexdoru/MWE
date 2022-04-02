package fr.alexdoru.megawallsenhancementsmod.events;

import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.nocheatersmod.NoCheatersMod;
import fr.alexdoru.nocheatersmod.commands.CommandWDR;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeybindingsEvent {

    private static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void key(KeyInputEvent e) {

        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (NoCheatersMod.addtimemark_key.isPressed()) {
            CommandWDR.addTimeMark();
        } else if (MegaWallsEnhancementsMod.toggleDroppedItemLimit.isPressed()) {
            ConfigHandler.limitDroppedEntityRendered = !ConfigHandler.limitDroppedEntityRendered;
            ConfigHandler.saveConfig();
            if (ConfigHandler.limitDroppedEntityRendered) {
                ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.WHITE + "Limiting dropped items rendered : " + EnumChatFormatting.GREEN + "On"));
            } else {
                ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.WHITE + "Limiting dropped items rendered : " + EnumChatFormatting.RED + "Off"));
            }
        }

    }

}
