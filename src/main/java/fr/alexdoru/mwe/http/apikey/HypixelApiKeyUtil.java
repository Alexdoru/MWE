package fr.alexdoru.mwe.http.apikey;

import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.util.EnumChatFormatting;

public class HypixelApiKeyUtil {

    public static String getApiKey() {
        return MWEConfig.APIKey;
    }

    public static void setApiKey(String key) {
        ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Api key set successfully");
        MWEConfig.APIKey = key;
        MWEConfig.saveConfig();
    }

    public static boolean apiKeyIsNotSetup() {
        return MWEConfig.APIKey == null || MWEConfig.APIKey.isEmpty();
    }

}
