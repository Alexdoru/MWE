package fr.alexdoru.megawallsenhancementsmod.api.apikey;

import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.util.EnumChatFormatting;

public class HypixelApiKeyUtil {

    public static String getApiKey() {
        return ConfigHandler.APIKey;
    }

    public static void setApiKey(String key) {
        ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Api key set successfully");
        ConfigHandler.APIKey = key;
        ConfigHandler.saveConfig();
    }

    public static boolean apiKeyIsNotSetup() {
        return ConfigHandler.APIKey == null || ConfigHandler.APIKey.isEmpty();
    }

}
