package fr.alexdoru.mwe.http.apikey;

import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import net.minecraft.util.EnumChatFormatting;

public final class HypixelApiKeyUtil {

    private HypixelApiKeyUtil() {}

    public static String getApiKey() {
        return MWEConfig.APIKey;
    }

    public static void setApiKey(String key) {
        ChatUtil.addChatMessage(EnumChatFormatting.GREEN + "Api key set successfully");
        MWEConfig.APIKey = key;
        MWE.INSTANCE().getConfigHandler().saveConfig();
    }

    public static boolean apiKeyIsNotSetup() {
        return MWEConfig.APIKey == null || MWEConfig.APIKey.isEmpty();
    }

}
