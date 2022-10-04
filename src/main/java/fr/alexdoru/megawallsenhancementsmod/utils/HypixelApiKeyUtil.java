package fr.alexdoru.megawallsenhancementsmod.utils;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.util.EnumChatFormatting;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;
import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.getTagMW;

public class HypixelApiKeyUtil {

    public static String getApiKey() {
        return ConfigHandler.APIKey;
    }

    public static void setApiKey(String key) {
        addChatMessage(getTagMW() + EnumChatFormatting.GREEN + "Api key set successfully");
        ConfigHandler.APIKey = key;
        ConfigHandler.saveConfig();
    }

    public static boolean apiKeyIsNotSetup() {
        return ConfigHandler.APIKey == null || ConfigHandler.APIKey.equals("");
    }

}
