package fr.alexdoru.megawallsenhancementsmod.utils;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.addChatMessage;
import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.getTagMW;

public class HypixelApiKeyUtil {

//	private static List<Long> request_times = new ArrayList<Long>();
//	public static final int NB_MAX_REQUESTS = 120;

    public static String getApiKey() {

//		long time = System.currentTimeMillis();
//		request_times.add(time);
//		request_times.removeIf(o -> (o.longValue() + 60000L < time));
//
//		if(request_times.size() > NB_MAX_REQUESTS) {
//			throw new ApiException("Exceeding the limit of " + NB_MAX_REQUESTS + " requests per minute allowed by Hypixel");
//		}

        return ConfigHandler.APIKey;
    }

    public static void setApiKey(String key) {
        addChatMessage(new ChatComponentText(getTagMW() + EnumChatFormatting.GREEN + "Api key set successfully"));
        ConfigHandler.APIKey = key;
        ConfigHandler.saveConfig();
    }

    public static boolean apiKeyIsNotSetup() {
        return ConfigHandler.APIKey == null || ConfigHandler.APIKey.equals("");
    }

//	/*
//	 * returns the amounts of requests send in the last minute
//	 */
//	public static int getRequestAmount() {
//		long time = System.currentTimeMillis();
//		request_times.removeIf(o -> (o.longValue() + 60000L < time));
//		return request_times.size();	
//	}

}
