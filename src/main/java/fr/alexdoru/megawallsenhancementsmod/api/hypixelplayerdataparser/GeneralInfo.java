package fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.DateUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.ILeveling;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class GeneralInfo extends LoginData {

	private long networkExp = 0;
	private int achievementPoints = 0;
	private int karma = 0;
	private String mcVersionRp;

	private int completedQuests = 0;

	private String DISCORD;
	private String TWITCH;
	private String TWITTER;
	private String YOUTUBE;

	public GeneralInfo(JsonObject playerData) {
		super(playerData);

		this.networkExp = JsonUtil.getLong(playerData, "networkExp");
		this.achievementPoints = JsonUtil.getInt(playerData, "achievementPoints");
		this.karma = JsonUtil.getInt(playerData, "karma");
		this.mcVersionRp = JsonUtil.getString(playerData, "mcVersionRp");

		JsonObject questsdata = JsonUtil.getJsonObject(playerData,"quests");

		if(questsdata != null) {

			for(Map.Entry<String, JsonElement> entry : questsdata.entrySet()) {			
				if(entry.getValue() != null && entry.getValue().isJsonObject()) {	

					JsonObject entryobj = entry.getValue().getAsJsonObject();
					JsonElement completionsElem = entryobj.get("completions");

					if(completionsElem == null) 
						continue;

					JsonArray completionsArray = completionsElem.getAsJsonArray();

					if(completionsArray == null) 
						continue;

					for(JsonElement elem : completionsArray) {
						this.completedQuests++;

					}
				}
			}
		}

		JsonObject socialMediaObj = JsonUtil.getJsonObject(playerData,"socialMedia");

		if(socialMediaObj!=null) {

			JsonObject linksObj = JsonUtil.getJsonObject(socialMediaObj,"links");

			if(linksObj!=null) {
				this.DISCORD = JsonUtil.getString(linksObj, "DISCORD");
				this.TWITCH = JsonUtil.getString(linksObj, "TWITCH");
				this.TWITTER = JsonUtil.getString(linksObj, "TWITTER");
				this.YOUTUBE = JsonUtil.getString(linksObj, "YOUTUBE");						
			}

		}

	}

	public IChatComponent getFormattedMessage(String formattedname) {
		
		IChatComponent msg = new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n")

				.appendSibling(ChatUtil.PlanckeHeaderText(formattedname, this.getdisplayname(), " - General info"))		

				.appendSibling(new ChatComponentText("\n" + "\n"
				
				+ ChatUtil.centerLine(EnumChatFormatting.GREEN + "Network level : " + EnumChatFormatting.GOLD + String.format("%.2f", (float)ILeveling.getExactLevel((double)networkExp))) + "\n"

				+ ChatUtil.centerLine(EnumChatFormatting.GREEN + "Karma : " + EnumChatFormatting.LIGHT_PURPLE + ChatUtil.formatInt(this.karma) ) + "\n"

				+ ChatUtil.centerLine(EnumChatFormatting.GREEN + "Achievement : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.achievementPoints) + " "
						+ EnumChatFormatting.GREEN + "Quests : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.completedQuests)) + "\n"

				+ ChatUtil.centerLine(EnumChatFormatting.GREEN + "Firstlogin : " + EnumChatFormatting.GOLD + DateUtil.localformatTimestampday(this.getFirstLogin()) + " "

				+ (this.isStaffonHypixel() || this.isHidingFromAPI() ? "" : EnumChatFormatting.GREEN + "Status : " + (this.isOnline() ? EnumChatFormatting.DARK_GREEN + "Online" :
					EnumChatFormatting.RED + "Offline " + EnumChatFormatting.DARK_GRAY + "for " + EnumChatFormatting.YELLOW + DateUtil.timeSince(this.getLastLogout())))) + "\n"

				+ (this.mcVersionRp==null ? "" :  ChatUtil.centerLine(EnumChatFormatting.GREEN + "Minecraft version : " + EnumChatFormatting.GOLD + this.mcVersionRp) + "\n")));
		
		String str = "";

		str+= (this.DISCORD!=null ? "Discord " : "") + (this.TWITCH!=null ? "Twitch " : "") + (this.TWITTER!=null ? "Twitter " : "") + (this.YOUTUBE!=null ? "Youtube" : "");
		
		msg.appendSibling(new ChatComponentText(ChatUtil.getSeparatorToCenter(str)));

		if(this.DISCORD!=null) {

			msg.appendSibling(new ChatComponentText(EnumChatFormatting.BLUE + "Discord ")
					.setChatStyle(new ChatStyle()
							.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to copy " + EnumChatFormatting.BLUE + this.DISCORD)))
							.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/copytoclipboard " + this.DISCORD))
							));

		}

		if(this.TWITCH!=null) {

			msg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "Twitch ")
					.setChatStyle(new ChatStyle()
							.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to open Twitch in browser")))
							.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.TWITCH))
							));

		}

		if(this.TWITTER!=null) {

			msg.appendSibling(new ChatComponentText(EnumChatFormatting.AQUA + "Twitter ")
					.setChatStyle(new ChatStyle()
							.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.AQUA + "Click to open Twitter in browser")))
							.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.TWITTER))
							));

		}
		
		if(this.YOUTUBE!=null) {

			msg.appendSibling(new ChatComponentText(EnumChatFormatting.RED + "Youtube")
					.setChatStyle(new ChatStyle()
							.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.RED + "Click to open Youtube in browser")))
							.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.YOUTUBE))
							));

		}

		msg.appendSibling(new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar()   )) ;



		return msg;
	}

}
