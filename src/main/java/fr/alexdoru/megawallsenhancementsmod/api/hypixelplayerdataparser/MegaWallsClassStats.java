package fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.lang.reflect.Field;

public class MegaWallsClassStats {

	private String classname;
	private String classnameuppercase;
	private String chosen_skin_class;
	// in the mwdata
	private int classname_kills = 0;
	private int classname_deaths = 0;
	private int classname_wins = 0;
	private int classname_losses = 0;
	private int classname_final_kills = 0;
	private int classname_final_deaths = 0;
	private int classname_time_played = 0;
	// to compute
	private float kdr = 0;
	private float fkdr = 0;
	private float wlr = 0;	
	private int games_played = 0;
	private float fkpergame = 0;
	// to compute classpoints
	private int classname_final_kills_standard = 0;
	private int classname_final_assists_standard = 0;
	private int classname_wins_standard = 0;
	private int classpoints = 0;
	// in the mwdata -> classes -> classname
	private boolean unlocked = false;
	private int skill_level_a = 1; // ability
	private int skill_level_b = 1; // passive 1
	private int skill_level_c = 1; // passive 2
	private int skill_level_d = 1; // kit
	private int skill_level_g = 1; // gathering
	private int prestige = 0;
	private int enderchest_rows = 3;

	/*
	 * fields below are for the game stats
	 */

	private int classname_games_played = 0;
	private int classname_assists = 0;
	private int classname_wither_damage = 0;
	private int classname_defender_kills = 0;
	private int classname_defender_assists = 0;
	private int classname_a_activations = 0;
	private int classname_a_activations_deathmatch = 0;
	private int classname_a_damage_dealt = 0;
	private int classname_arrows_fired = 0;
	private int classname_arrows_hit = 0;
	private int classname_blocks_placed = 0;
	private int classname_meters_walked = 0;
	private int classname_self_healed = 0;
	private int classname_allies_healed = 0;

	public MegaWallsClassStats(JsonObject playerData, String classname) {

		if(playerData == null) {return;}	

		JsonObject statsdata = playerData.get("stats").getAsJsonObject();

		if(statsdata == null) {return;}

		JsonElement mwElem = statsdata.get("Walls3");

		if(mwElem == null) {return;}

		JsonObject mwdata = mwElem.getAsJsonObject();

		this.classname = classname.toLowerCase();
		this.classnameuppercase = classname;
		this.chosen_skin_class = JsonUtil.getString(mwdata,"chosen_skin_" + this.classnameuppercase);

		this.classname_kills = JsonUtil.getInt(mwdata, this.classname + "_kills");
		this.classname_deaths = JsonUtil.getInt(mwdata, this.classname + "_deaths");
		this.classname_wins = JsonUtil.getInt(mwdata, this.classname + "_wins");
		this.classname_losses = JsonUtil.getInt(mwdata, this.classname + "_losses");
		this.classname_final_kills = JsonUtil.getInt(mwdata, this.classname + "_final_kills");
		this.classname_final_deaths = JsonUtil.getInt(mwdata, this.classname + "_final_deaths");
		this.classname_time_played = JsonUtil.getInt(mwdata, this.classname + "_time_played");

		this.kdr = (float)this.classname_kills / (this.classname_deaths == 0 ? 1 : (float)this.classname_deaths);
		this.fkdr = (float)this.classname_final_kills / (this.classname_final_deaths == 0 ? 1 : (float)this.classname_final_deaths);
		this.wlr = (float)this.classname_wins / (this.classname_losses == 0 ? 1 : (float)this.classname_losses);

		this.classname_final_kills_standard = JsonUtil.getInt(mwdata, this.classname + "_final_kills_standard");
		this.classname_final_assists_standard = JsonUtil.getInt(mwdata, this.classname + "_final_assists_standard");
		this.classname_wins_standard = JsonUtil.getInt(mwdata, this.classname + "_wins_standard");
		this.classpoints = this.classname_final_kills_standard + this.classname_final_assists_standard + this.classname_wins_standard*10;

		this.games_played = this.classname_wins + this.classname_losses; // doesn't count the draws
		this.fkpergame = (float)this.classname_final_kills / (this.games_played == 0 ? 1 : (float)this.games_played);

		JsonObject classesdata = JsonUtil.getJsonObject(mwdata,"classes");
		if(classesdata == null)
			return;

		JsonObject classeobj = JsonUtil.getJsonObject(classesdata,this.classname);
		if(classeobj == null)
			return;

		JsonElement boolelem = classeobj.get("unlocked");

		if(boolelem!=null) {
			this.unlocked = boolelem.getAsBoolean();
		}
		this.skill_level_a = Math.max(JsonUtil.getInt(classeobj, "skill_level_a"),1);
		this.skill_level_b = Math.max(JsonUtil.getInt(classeobj, "skill_level_b"),1);
		this.skill_level_c = Math.max(JsonUtil.getInt(classeobj, "skill_level_c"),1);
		this.skill_level_d = Math.max(JsonUtil.getInt(classeobj, "skill_level_d"),1);
		this.skill_level_g = Math.max(JsonUtil.getInt(classeobj, "skill_level_g"),1);
		this.prestige = JsonUtil.getInt(classeobj, "prestige");
		this.enderchest_rows = Math.max(JsonUtil.getInt(classeobj, "enderchest_rows"),3);

		/*
		 * fields below are for the game stats
		 */
		this.classname_assists = JsonUtil.getInt(mwdata, this.classname + "_assists");
		this.classname_wither_damage = JsonUtil.getInt(mwdata,this.classname + "_wither_damage");		
		this.classname_defender_kills = JsonUtil.getInt(mwdata,this.classname + "_defender_kills");
		this.classname_defender_assists = JsonUtil.getInt(mwdata,this.classname + "_defender_assists");
		this.classname_a_activations = JsonUtil.getInt(mwdata,this.classname + "_a_activations");
		this.classname_a_activations_deathmatch = JsonUtil.getInt(mwdata,this.classname + "_a_activations_deathmatch");
		this.classname_a_damage_dealt = JsonUtil.getInt(mwdata,this.classname + "_a_damage_dealt");
		this.classname_arrows_fired = JsonUtil.getInt(mwdata,this.classname + "_arrows_fired");
		this.classname_arrows_hit = JsonUtil.getInt(mwdata,this.classname + "_arrows_hit");
		this.classname_blocks_placed = JsonUtil.getInt(mwdata,this.classname + "_blocks_placed");
		this.classname_meters_walked = JsonUtil.getInt(mwdata,this.classname + "_meters_walked");
		this.classname_self_healed = JsonUtil.getInt(mwdata,this.classname + "_self_healed");
		this.classname_allies_healed = JsonUtil.getInt(mwdata,this.classname + "_allies_healed");
	}

	/*
	 * Returns this minus the input
	 */
	public void minus(MegaWallsClassStats mwclassStats) throws IllegalArgumentException, IllegalAccessException {

		for (Field field : this.getClass().getDeclaredFields()) {
			if(field.getType() == int.class) {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}		
				field.setInt(this, Math.max(0, field.getInt(this) - field.getInt(mwclassStats)));
			}
		}
	}

	/*
	 * message used for the stats message at the end of a game
	 */
	public IChatComponent getGameStatMessage(String formattedname) {

		float arrowaccuracy = 100f*(float)this.classname_arrows_hit / (float)(this.classname_arrows_fired == 0 ? 1 : this.classname_arrows_fired);

		String[][] matrix1 = { 
				{
					EnumChatFormatting.GREEN + "Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_kills) + " ",
					EnumChatFormatting.GREEN + "Assists : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_assists)
				},

				{
					EnumChatFormatting.GREEN + "Final Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_final_kills) + " ",
					EnumChatFormatting.GREEN + "Final Assists : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_final_assists_standard),
				},

				{
					EnumChatFormatting.GREEN + "Ability activations : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_a_activations) + " ",
					EnumChatFormatting.GREEN + "Ability Damage : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_a_damage_dealt/2) + EnumChatFormatting.RED + "\u2764",
				},
				
				{
					EnumChatFormatting.GREEN + "Wither Damage : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_wither_damage) + " ",
					EnumChatFormatting.GREEN + "Defender kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_defender_kills),
				},

				{// TODO ya un pb avec zombie ca affiche le double du self healed
					EnumChatFormatting.GREEN + "Self healed : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_self_healed/2) + EnumChatFormatting.RED + "\u2764" + " ",
					EnumChatFormatting.GREEN + "Allies healed : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_allies_healed/2) + EnumChatFormatting.RED + "\u2764",
				},
				
				{
					EnumChatFormatting.GREEN + "Blocks placed : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_blocks_placed) + " ",
					EnumChatFormatting.GREEN + "Meters walked : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_meters_walked),
				}};

		IChatComponent imsg = new ChatComponentText(EnumChatFormatting.BLUE + ChatUtil.bar() + "\n")
				.appendSibling(new ChatComponentText(ChatUtil.centerLine(formattedname + EnumChatFormatting.GOLD + " - Game stats - " + this.classnameuppercase)));

		if(this.games_played != 1) {
			imsg.appendSibling(new ChatComponentText("\n" + ChatUtil.centerLine(EnumChatFormatting.DARK_RED + "These stats are for " + this.games_played + " games, not one.")));
		}

		imsg.appendSibling(new ChatComponentText("\n"+"\n" + ChatUtil.alignText(matrix1)))
		.appendSibling(new ChatComponentText(ChatUtil.centerLine(EnumChatFormatting.GREEN + "Arrows shot : " + EnumChatFormatting.GOLD + this.classname_arrows_fired 
				+ EnumChatFormatting.GREEN + " Arrows hits : " + EnumChatFormatting.GOLD + this.classname_arrows_hit
				+ EnumChatFormatting.GREEN + " Arrows accuracy : " + EnumChatFormatting.GOLD + arrowaccuracy + "%" + "\n" )))
		.appendSibling(new ChatComponentText(EnumChatFormatting.BLUE + ChatUtil.bar()));
		return imsg;		
	}

	public IChatComponent getFormattedMessage(String formattedname, String playername) {

		String[][] matrix1 = { 
				{
					EnumChatFormatting.AQUA + "Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_kills) + " ",
					EnumChatFormatting.AQUA + "Deaths : " + EnumChatFormatting.RED + ChatUtil.formatInt(this.classname_deaths) + " ",
					EnumChatFormatting.AQUA + "K/D Ratio : " + ( this.kdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED ) + String.format("%.3f", this.kdr)
				},

				{
					EnumChatFormatting.AQUA + "Final Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_final_kills) + " ",
					EnumChatFormatting.AQUA + "Final Deaths : " + EnumChatFormatting.RED + ChatUtil.formatInt(this.classname_final_deaths) + " ",
					EnumChatFormatting.AQUA + "FK/D Ratio : " + ( this.fkdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED ) + String.format("%.3f", this.fkdr)
				},

				{
					EnumChatFormatting.AQUA + "Wins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_wins) + " ",
					EnumChatFormatting.AQUA + "Losses : " + EnumChatFormatting.RED + ChatUtil.formatInt(this.classname_losses) + " ",
					EnumChatFormatting.AQUA + "W/L Ratio : " + ( this.wlr > 0.25f ? EnumChatFormatting.GOLD : EnumChatFormatting.RED ) + String.format("%.3f", this.wlr) + "\n" 
				}};
		
		String[][] matrix2 = {
				
				{
					EnumChatFormatting.AQUA + "Wither damage : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_wither_damage) + "     ",
					EnumChatFormatting.AQUA + "Defending Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.classname_defender_kills)
				},
				
				{
					EnumChatFormatting.AQUA + "Games played : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.games_played) + "     ",
					EnumChatFormatting.AQUA + "FK/game : " + ( this.fkpergame > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED ) + String.format("%.3f",this.fkpergame)
				},
		};
		
		
		IChatComponent msg;

		if(this.unlocked || this.classname.equals("hunter") || this.classname.equals("shark") || this.classname.equals("cow") ) {

			msg = new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n")

					.appendSibling(ChatUtil.PlanckeHeaderText(formattedname, playername, " - Mega Walls " + this.classnameuppercase + " stats"))	

					.appendSibling(new ChatComponentText("\n" + "\n" + ChatUtil.alignText(matrix1))
							.setChatStyle(new ChatStyle()
									.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(

											EnumChatFormatting.GRAY + "Disclaimer : Class Final K/D Ratio's are not accurate for some players, as final-deaths for classes weren't tracked prior to the Mythic update.")))))

					.appendSibling(new ChatComponentText(ChatUtil.alignText(matrix2) +				

				 ChatUtil.centerLine(EnumChatFormatting.GREEN + "Selected skin : " + EnumChatFormatting.GOLD +  (this.chosen_skin_class==null?(this.classnameuppercase==null?"None":this.classnameuppercase):this.chosen_skin_class)) + "\n" + "\n"

				+ ChatUtil.centerLine(EnumChatFormatting.GREEN + "Classpoints : " + EnumChatFormatting.GOLD + this.classpoints + " "
						+ EnumChatFormatting.GREEN + " Playtime (approx.) : " + EnumChatFormatting.GOLD + String.format("%.2f", this.classname_time_played/60f) + "h") + "\n"

				+ ChatUtil.centerLine(EnumChatFormatting.GREEN + "Kit : "
						+ (this.skill_level_d == 5 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(this.skill_level_d) + " "
						+ (this.skill_level_a == 5 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(this.skill_level_a) + " "
						+ (this.skill_level_b == 3 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(this.skill_level_b) + " "
						+ (this.skill_level_c == 3 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(this.skill_level_c) + " "
						+ (this.skill_level_g == 3 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(this.skill_level_g) + " "

				+ EnumChatFormatting.GREEN + "Prestige : " + (this.prestige == 0 ? EnumChatFormatting.DARK_GRAY : EnumChatFormatting.GOLD) + ChatUtil.intToRoman(this.prestige) + " "
				+ EnumChatFormatting.GREEN + "Echest rows : " + (this.enderchest_rows == 5 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + this.enderchest_rows) + "\n"

				+ EnumChatFormatting.AQUA + ChatUtil.bar()));

		} else { 

			msg = new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n"
					+ ChatUtil.centerLine(formattedname + EnumChatFormatting.GOLD + " - Mega Walls " + this.classnameuppercase + " stats") + "\n" + "\n"					
					+ ChatUtil.centerLine(formattedname + EnumChatFormatting.RED + " didn't purchased " + EnumChatFormatting.GOLD + this.classnameuppercase)	+"\n"
					+ EnumChatFormatting.AQUA + ChatUtil.bar());

		}

		return msg;

	} 

}
