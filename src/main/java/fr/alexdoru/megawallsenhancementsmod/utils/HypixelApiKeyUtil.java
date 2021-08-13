package fr.alexdoru.megawallsenhancementsmod.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class HypixelApiKeyUtil {

	private static String API_KEY;
	public static File apiFile;
	//	private static List<Long> request_times = new ArrayList<Long>();
	//	private static final int NB_MAX_REQUESTS = 120;

	public static String getApiKey() throws ApiException {

		//		long time = System.currentTimeMillis();
		//		request_times.add(time);
		//		request_times.removeIf(o -> (o.longValue() + 60000L < time));
		//		
		//		if(request_times.size() > NB_MAX_REQUESTS) {
		//			throw new ApiException("Exceeding amount of requests per minute allowed by Hypixel");
		//		}

		return API_KEY;
	}

	public static void setApiKey(String key) {
		ChatUtil.addChatMessage((IChatComponent)new ChatComponentText(ChatUtil.getTag() + EnumChatFormatting.GREEN + "Api key set successfully"));
		API_KEY = key;
	}

	public static boolean isApiKeySetup() {
		return API_KEY != null;
	}

	public static void saveApiKey() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(apiFile));
			writer.write(API_KEY);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadApiKey() {		
		if (!apiFile.exists()) {
			return; 
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(apiFile));
			API_KEY = reader.readLine();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
