package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@SuppressWarnings("unused")
public class MinecraftHook_DebugMessages {

    public static void onSettingChange(Minecraft mc, boolean settingIn, String settingName) {
        if (mc.theWorld != null && mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[Debug]: " + EnumChatFormatting.WHITE + settingName + ":" + (settingIn ? EnumChatFormatting.GREEN + " On" : EnumChatFormatting.RED + " Off")));
        }
        if ("Hitboxes".equals(settingName)) {
            ConfigHandler.isDebugHitboxOn = settingIn;
            ConfigHandler.saveConfig();
        }
    }

    public static void onReloadChunks(Minecraft mc) {
        if (mc.theWorld != null && mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[Debug]: " + EnumChatFormatting.WHITE + "Reloading all chunks"));
        }
    }

}
