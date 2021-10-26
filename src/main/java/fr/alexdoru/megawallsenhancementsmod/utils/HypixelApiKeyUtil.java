package fr.alexdoru.megawallsenhancementsmod.utils;

import fr.alexdoru.megawallsenhancementsmod.config.MWEnConfigHandler;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

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

		return MWEnConfigHandler.APIKey;
	}

	public static void setApiKey(String key) {
		ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.GREEN + "Api key set successfully"));
		MWEnConfigHandler.APIKey = key;
		MWEnConfigHandler.saveConfig();
	}

	public static boolean isApiKeySetup() {
		return MWEnConfigHandler.APIKey != null && !MWEnConfigHandler.APIKey.equals("");
	}
	
//	/*
//	 * returns the amounts of requests send in the last minute
//	 */
//	public static int getRequestAmount() {
//		long time = System.currentTimeMillis();
//		request_times.removeIf(o -> (o.longValue() + 60000L < time));
//		return request_times.size();	
//	}
	
//	public static void saveApiKey() {
//		try {
//			BufferedWriter writer = new BufferedWriter(new FileWriter(apiFile));
//			writer.write(API_KEY);
//			writer.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void loadApiKey() {		
//		if (!apiFile.exists()) {
//			return; 
//		}
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(apiFile));
//			API_KEY = reader.readLine();
//			reader.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//	}

}
