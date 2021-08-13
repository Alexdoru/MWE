package fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser;

import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class MegaWallsStats {

	private String chosen_class;
	private String chosen_skin_class;

	private int coins = 0;
	private int mythic_favor = 0;
	private int wither_damage = 0;
	private int wins = 0;
	private int wins_face_off = 0;
	private int wins_practice = 0;
	private int losses = 0;
	private int kills = 0;
	private int defender_kills = 0;
	//private int assists = 0; // useless
	private int deaths = 0;
	private int final_kills = 0;
	//private int final_kills_standard = 0; // useless
	//private int final_assists = 0; // useless
	//private int final_assists_standard = 0; // useless
	private int final_deaths = 0;

	private float kdr = 0;
	private float fkdr = 0;
	private float wlr = 0;

	private int time_played = 0;
	private int nbprestiges = 0;

	private int games_played = 0;
	private float fkpergame = 0;

	private JsonObject classesdata = null;

	public MegaWallsStats(JsonObject playerData) {

		if(playerData == null) 
			return;	

		JsonObject statsdata = playerData.get("stats").getAsJsonObject();

		if(statsdata == null)
			return;

		JsonObject mwdata = JsonUtil.getJsonObject(statsdata,"Walls3");

		if(mwdata == null)
			return;

		this.chosen_class = JsonUtil.getString(mwdata,"chosen_class");
		this.chosen_skin_class = JsonUtil.getString(mwdata,"chosen_skin_" + this.chosen_class);
		this.coins = JsonUtil.getInt(mwdata,"coins");
		this.mythic_favor = JsonUtil.getInt(mwdata,"mythic_favor");
		this.wither_damage = JsonUtil.getInt(mwdata,"wither_damage") + JsonUtil.getInt(mwdata,"witherDamage"); // add both to get wither damage
		this.wins = JsonUtil.getInt(mwdata,"wins");
		this.wins_face_off = JsonUtil.getInt(mwdata,"wins_face_off");
		this.wins_practice = JsonUtil.getInt(mwdata,"wins_practice");
		this.losses = JsonUtil.getInt(mwdata,"losses");
		this.kills = JsonUtil.getInt(mwdata,"kills");
		this.defender_kills = JsonUtil.getInt(mwdata,"defender_kills");
		//this.assists = JsonUtil.getInt(mwdata,"assists");
		this.deaths = JsonUtil.getInt(mwdata,"deaths");
		this.final_kills = JsonUtil.getInt(mwdata,"final_kills");
		//this.final_kills_standard = JsonUtil.getInt(mwdata,"final_kills_standard");
		//this.final_assists = JsonUtil.getInt(mwdata,"final_assists");
		//this.final_assists_standard = JsonUtil.getInt(mwdata,"final_assists_standard");
		this.final_deaths = JsonUtil.getInt(mwdata,"final_deaths") + JsonUtil.getInt(mwdata,"finalDeaths");

		this.kdr = (float)this.kills / (this.deaths == 0 ? 1 : (float)this.deaths);
		this.fkdr = (float)this.final_kills / (this.final_deaths == 0 ? 1 : (float)this.final_deaths);
		this.wlr = (float)this.wins / (this.losses == 0 ? 1 : (float)this.losses);

		this.time_played = JsonUtil.getInt(mwdata,"time_played") + JsonUtil.getInt(mwdata,"time_played_standard"); // additionner les deux / en minutes

		// computes the number of prestiges
		this.classesdata = JsonUtil.getJsonObject(mwdata,"classes");

		if(this.classesdata == null)
			return;

		for(Map.Entry<String, JsonElement> entry : classesdata.entrySet()) {			
			if(entry.getValue() != null && entry.getValue().isJsonObject()) {				
				JsonObject entryclassobj = entry.getValue().getAsJsonObject();
				this.nbprestiges = this.nbprestiges + JsonUtil.getInt(entryclassobj,"prestige");				
			}
		}

		this.games_played = this.wins + this.losses; // doesn't count the draws
		this.fkpergame = (float)this.final_kills / (this.games_played == 0 ? 1 : (float)this.games_played);

	}

	public float getFkdr() {
		return this.fkdr;
	}

	public float getWlr() {
		return this.wlr;
	}

	public float getFkpergame() {
		return this.fkpergame;
	}

	public int getGames_played() {
		return this.games_played;
	}

	public JsonObject getClassesdata() {
		return this.classesdata;
	}

	public IChatComponent getFormattedMessage(String formattedname, String playername, boolean a) {

		String[][] matrix1 = { 
				{
					EnumChatFormatting.AQUA + "Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.kills) + " ",
					EnumChatFormatting.AQUA + "Deaths : " + EnumChatFormatting.RED + ChatUtil.formatInt(this.deaths) + " ",
					EnumChatFormatting.AQUA + "K/D Ratio : " + ( this.kdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED ) + String.format("%.3f", this.kdr)
				},

				{
					EnumChatFormatting.AQUA + "Final Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.final_kills) + " ",
					EnumChatFormatting.AQUA + "Final Deaths : " + EnumChatFormatting.RED + ChatUtil.formatInt(this.final_deaths) + " ",
					EnumChatFormatting.AQUA + "FK/D Ratio : " + ( this.fkdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED ) + String.format("%.3f", this.fkdr)
				},

				{
					EnumChatFormatting.AQUA + "Wins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.wins) + " ",
					EnumChatFormatting.AQUA + "Losses : " + EnumChatFormatting.RED + ChatUtil.formatInt(this.losses) + " ",
					EnumChatFormatting.AQUA + "W/L Ratio : " + ( this.wlr > 0.25f ? EnumChatFormatting.GOLD : EnumChatFormatting.RED ) + String.format("%.3f", this.wlr) + "\n" 
				}};

		String[][] matrix2 = { 
				{
					EnumChatFormatting.AQUA + "Games played : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.games_played) + "     ",
					EnumChatFormatting.AQUA + "FK/game : " + ( this.fkpergame > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED ) + String.format("%.3f",this.fkpergame)
				},

				{
					EnumChatFormatting.AQUA + "Wither damage : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.wither_damage) + "     ",
					EnumChatFormatting.AQUA + "Defending Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.defender_kills)
				},

				{
					EnumChatFormatting.AQUA + "Faceoff wins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.wins_face_off) + "     ",
					EnumChatFormatting.AQUA + "Practice wins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.wins_practice)
				},

				{
					EnumChatFormatting.AQUA + "Coins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.coins) + "     ",
					EnumChatFormatting.AQUA + "Mythic favors : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.mythic_favor) + "\n"
				}};

		IChatComponent imsg = new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n")

				.appendSibling(ChatUtil.PlanckeHeaderText(formattedname, playername, " - Mega Walls stats"))

				.appendSibling(new ChatComponentText("\n" + "\n" +
						ChatUtil.centerLine(EnumChatFormatting.GREEN + "Prestiges : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.nbprestiges) + " " 
								+ EnumChatFormatting.GREEN + " Playtime (approx.) : " + EnumChatFormatting.GOLD + String.format("%.2f", this.time_played/60f) + "h") + "\n"));

		if(!a)imsg.appendSibling(new ChatComponentText(
				ChatUtil.centerLine(EnumChatFormatting.GREEN + "Selected class : " + EnumChatFormatting.GOLD + (this.chosen_class==null?"None":this.chosen_class) + " " 
						+ EnumChatFormatting.GREEN + " Selected skin : " + EnumChatFormatting.GOLD +  (this.chosen_skin_class==null?(this.chosen_class==null?"None":this.chosen_class):this.chosen_skin_class)) + "\n" + "\n")
				.setChatStyle(new ChatStyle()
						.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click for this class' stats")))
						.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plancke " + playername + " mw " + (this.chosen_class==null?"None":this.chosen_class)))));

		imsg.appendSibling(new ChatComponentText(ChatUtil.alignText(matrix2) + ChatUtil.alignText(matrix1)))

		.appendSibling(new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar()));

		return imsg;

	}

}
