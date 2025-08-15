package fr.alexdoru.mwe.asm.hooks;

import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class MinecraftHook_DebugMessages {

    public static void onSettingChange(Minecraft mc, boolean settingIn, String settingName) {
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[Debug]: " + EnumChatFormatting.WHITE + settingName + ":" + (settingIn ? EnumChatFormatting.GREEN + " On" : EnumChatFormatting.RED + " Off")));
        if ("Hitboxes".equals(settingName)) {
            MWEConfig.isDebugHitboxOn = settingIn;
            MWEConfig.saveConfig();
        }
    }

    public static void onReloadChunks(Minecraft mc) {
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[Debug]: " + EnumChatFormatting.WHITE + "Reloading all chunks"));
    }

}
