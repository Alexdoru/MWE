package fr.alexdoru.megawallsenhancementsmod.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

public class TabCompletionUtil {
	
	public static List<String> getOnlinePlayersByName() {
		
        	ArrayList<String> players = new ArrayList<String>();
	        Collection<NetworkPlayerInfo> playerCollection = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
	
	        for (NetworkPlayerInfo networkPlayerInfo : playerCollection) {
	            String playerName = networkPlayerInfo.getGameProfile().getName();
	            if (playerName != null) {
	            	players.add(playerName);
	            }
	        }
	
	        return players;
    }

}
