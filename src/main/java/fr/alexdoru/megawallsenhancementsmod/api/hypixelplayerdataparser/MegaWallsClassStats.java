package fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.lang.reflect.Field;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.*;

public class MegaWallsClassStats {

    private String classname;
    private String classnameuppercase;
    private String chosen_skin_class;
    // in the mwdata
    private int coins;
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
    private float wither_damage_game = 0;
    private float def_kill_game = 0;
    private int games_played = 0;
    private float fkpergame = 0;
    private int classname_final_assists_standard = 0;
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

    //private int classname_games_played = 0;
    private int classname_assists = 0;
    private int classname_wither_damage = 0;
    private int classname_defender_kills = 0;
    //private int classname_defender_assists = 0;
    private int classname_a_activations = 0;
    //private int classname_a_activations_deathmatch = 0;
    private int classname_a_damage_dealt = 0;
    private int classname_arrows_fired = 0;
    private int classname_arrows_hit = 0;
    private int classname_blocks_placed = 0;
    private int classname_meters_walked = 0;
    private int classname_self_healed = 0;
    private int classname_allies_healed = 0;

    public MegaWallsClassStats(JsonObject playerData, String classname) {

        if (playerData == null) {
            return;
        }

        JsonObject statsdata = playerData.get("stats").getAsJsonObject();

        if (statsdata == null) {
            return;
        }

        JsonElement mwElem = statsdata.get("Walls3");

        if (mwElem == null) {
            return;
        }

        JsonObject mwdata = mwElem.getAsJsonObject();

        classname = classname.toLowerCase();
        classnameuppercase = classname;
        chosen_skin_class = JsonUtil.getString(mwdata, "chosen_skin_" + classnameuppercase);

        coins = JsonUtil.getInt(mwdata, "coins");

        classname_kills = JsonUtil.getInt(mwdata, classname + "_kills");
        classname_deaths = JsonUtil.getInt(mwdata, classname + "_deaths");
        classname_wins = JsonUtil.getInt(mwdata, classname + "_wins");
        classname_losses = JsonUtil.getInt(mwdata, classname + "_losses");
        classname_final_kills = JsonUtil.getInt(mwdata, classname + "_final_kills");
        classname_final_deaths = JsonUtil.getInt(mwdata, classname + "_final_deaths");
        classname_time_played = JsonUtil.getInt(mwdata, classname + "_time_played");

        kdr = (float) classname_kills / (classname_deaths == 0 ? 1 : (float) classname_deaths);
        fkdr = (float) classname_final_kills / (classname_final_deaths == 0 ? 1 : (float) classname_final_deaths);
        wlr = (float) classname_wins / (classname_losses == 0 ? 1 : (float) classname_losses);

        // to compute classpoints
        int classname_final_kills_standard = JsonUtil.getInt(mwdata, classname + "_final_kills_standard");
        classname_final_assists_standard = JsonUtil.getInt(mwdata, classname + "_final_assists_standard");
        int classname_wins_standard = JsonUtil.getInt(mwdata, classname + "_wins_standard");
        classpoints = classname_final_kills_standard + classname_final_assists_standard + classname_wins_standard * 10;

        games_played = classname_wins + classname_losses; // doesn't count the draws
        fkpergame = (float) classname_final_kills / (games_played == 0 ? 1 : (float) games_played);

        JsonObject classesdata = JsonUtil.getJsonObject(mwdata, "classes");
        if (classesdata == null)
            return;

        JsonObject classeobj = JsonUtil.getJsonObject(classesdata, classname);
        if (classeobj == null)
            return;

        JsonElement boolelem = classeobj.get("unlocked");

        if (boolelem != null) {
            unlocked = boolelem.getAsBoolean();
        }
        skill_level_a = Math.max(JsonUtil.getInt(classeobj, "skill_level_a"), 1);
        skill_level_b = Math.max(JsonUtil.getInt(classeobj, "skill_level_b"), 1);
        skill_level_c = Math.max(JsonUtil.getInt(classeobj, "skill_level_c"), 1);
        skill_level_d = Math.max(JsonUtil.getInt(classeobj, "skill_level_d"), 1);
        skill_level_g = Math.max(JsonUtil.getInt(classeobj, "skill_level_g"), 1);
        prestige = JsonUtil.getInt(classeobj, "prestige");
        enderchest_rows = Math.max(JsonUtil.getInt(classeobj, "enderchest_rows"), 3);

        /*
         * fields below are for the game stats
         */
        classname_assists = JsonUtil.getInt(mwdata, classname + "_assists");
        classname_wither_damage = JsonUtil.getInt(mwdata, classname + "_wither_damage");
        classname_defender_kills = JsonUtil.getInt(mwdata, classname + "_defender_kills");
        //classname_defender_assists = JsonUtil.getInt(mwdata,classname + "_defender_assists");
        classname_a_activations = JsonUtil.getInt(mwdata, classname + "_a_activations");
        //classname_a_activations_deathmatch = JsonUtil.getInt(mwdata,classname + "_a_activations_deathmatch");
        classname_a_damage_dealt = JsonUtil.getInt(mwdata, classname + "_a_damage_dealt");
        classname_arrows_fired = JsonUtil.getInt(mwdata, classname + "_arrows_fired");
        classname_arrows_hit = JsonUtil.getInt(mwdata, classname + "_arrows_hit");
        classname_blocks_placed = JsonUtil.getInt(mwdata, classname + "_blocks_placed");
        classname_meters_walked = JsonUtil.getInt(mwdata, classname + "_meters_walked");
        classname_self_healed = JsonUtil.getInt(mwdata, classname + "_self_healed");
        classname_allies_healed = JsonUtil.getInt(mwdata, classname + "_allies_healed");
        wither_damage_game = classname_wither_damage / (games_played == 0 ? 1 : (float) games_played);
        def_kill_game = classname_defender_kills / (games_played == 0 ? 1 : (float) games_played);
    }

    /*
     * Returns this minus the input
     */
    public void minus(MegaWallsClassStats mwclassStats) throws IllegalArgumentException, IllegalAccessException {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getType() == int.class) {
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

        float arrowaccuracy = 100f * (float) classname_arrows_hit / (float) (classname_arrows_fired == 0 ? 1 : classname_arrows_fired);

        String[][] matrix1 = {
                {
                        EnumChatFormatting.GREEN + "Kills : " + EnumChatFormatting.GOLD + formatInt(classname_kills) + " ",
                        EnumChatFormatting.GREEN + "Assists : " + EnumChatFormatting.GOLD + formatInt(classname_assists)
                },

                {
                        EnumChatFormatting.GREEN + "Final Kills : " + EnumChatFormatting.GOLD + formatInt(classname_final_kills) + " ",
                        EnumChatFormatting.GREEN + "Final Assists : " + EnumChatFormatting.GOLD + formatInt(classname_final_assists_standard),
                },

                {
                        EnumChatFormatting.GREEN + "Deaths : " + EnumChatFormatting.GOLD + formatInt(classname_deaths) + " ",
                        EnumChatFormatting.GREEN + "Final Death : " + EnumChatFormatting.GOLD + formatInt(classname_final_deaths),
                },

                {
                        EnumChatFormatting.GREEN + "Ability activations : " + EnumChatFormatting.GOLD + formatInt(classname_a_activations) + " ",
                        EnumChatFormatting.GREEN + "Ability Damage : " + EnumChatFormatting.GOLD + formatInt(classname_a_damage_dealt / 2) + EnumChatFormatting.RED + "\u2764",
                },

                {
                        EnumChatFormatting.GREEN + "Wither Damage : " + EnumChatFormatting.GOLD + formatInt(classname_wither_damage) + " ",
                        EnumChatFormatting.GREEN + "Defender kills : " + EnumChatFormatting.GOLD + formatInt(classname_defender_kills),
                },

                {
                        EnumChatFormatting.GREEN + "Self healed : " + EnumChatFormatting.GOLD + formatInt(classname_self_healed / 2) + EnumChatFormatting.RED + "\u2764" + " ",
                        EnumChatFormatting.GREEN + "Allies healed : " + EnumChatFormatting.GOLD + formatInt(classname_allies_healed / 2) + EnumChatFormatting.RED + "\u2764",
                },

                {
                        EnumChatFormatting.GREEN + "Blocks placed : " + EnumChatFormatting.GOLD + formatInt(classname_blocks_placed) + " ",
                        EnumChatFormatting.GREEN + "Meters walked : " + EnumChatFormatting.GOLD + formatInt(classname_meters_walked),
                }};

        IChatComponent imsg = new ChatComponentText(EnumChatFormatting.BLUE + bar() + "\n")
                .appendSibling(new ChatComponentText(centerLine(formattedname + EnumChatFormatting.GOLD + " - Game stats - " + classnameuppercase)));

        if (games_played != 1) {
            imsg.appendSibling(new ChatComponentText("\n" + centerLine(EnumChatFormatting.DARK_RED + "These stats are for " + games_played + " games, not one.")));
        }

        imsg.appendSibling(new ChatComponentText("\n" + "\n" + alignText(matrix1)))
                .appendSibling(new ChatComponentText(centerLine(EnumChatFormatting.GREEN + "Arrows shot : " + EnumChatFormatting.GOLD + classname_arrows_fired
                        + EnumChatFormatting.GREEN + " Arrows hits : " + EnumChatFormatting.GOLD + classname_arrows_hit
                        + EnumChatFormatting.GREEN + " Arrows accuracy : " + EnumChatFormatting.GOLD + String.format("%.2f", arrowaccuracy) + "%" + "\n")))
                .appendSibling(new ChatComponentText(EnumChatFormatting.BLUE + bar()));
        return imsg;
    }

    public IChatComponent getFormattedMessage(String formattedname, String playername) {

        String[][] matrix1 = {
                {
                        EnumChatFormatting.AQUA + "Kills : " + EnumChatFormatting.GOLD + formatInt(classname_kills) + " ",
                        EnumChatFormatting.AQUA + "Deaths : " + EnumChatFormatting.RED + formatInt(classname_deaths) + " ",
                        EnumChatFormatting.AQUA + "K/D Ratio : " + (kdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.3f", kdr)
                },

                {
                        EnumChatFormatting.AQUA + "Final Kills : " + EnumChatFormatting.GOLD + formatInt(classname_final_kills) + " ",
                        EnumChatFormatting.AQUA + "Final Deaths : " + EnumChatFormatting.RED + formatInt(classname_final_deaths) + " ",
                        EnumChatFormatting.AQUA + "FK/D Ratio : " + (fkdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.3f", fkdr)
                },

                {
                        EnumChatFormatting.AQUA + "Wins : " + EnumChatFormatting.GOLD + formatInt(classname_wins) + " ",
                        EnumChatFormatting.AQUA + "Losses : " + EnumChatFormatting.RED + formatInt(classname_losses) + " ",
                        EnumChatFormatting.AQUA + "W/L Ratio : " + (wlr > 0.25f ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.3f", wlr) + "\n"
                }};

        String[][] matrix2 = {

                {
                        EnumChatFormatting.AQUA + "Wither damage : " + EnumChatFormatting.GOLD + formatInt(classname_wither_damage) + "     ",
                        EnumChatFormatting.AQUA + "Defending Kills : " + EnumChatFormatting.GOLD + formatInt(classname_defender_kills)
                },

                {
                        EnumChatFormatting.AQUA + "Wither dmg/game : " + EnumChatFormatting.GOLD + ((int) wither_damage_game) + "     ",
                        EnumChatFormatting.AQUA + "Def Kills/game : " + EnumChatFormatting.GOLD + String.format("%.2f", def_kill_game)
                },

                {
                        EnumChatFormatting.AQUA + "Games played : " + EnumChatFormatting.GOLD + formatInt(games_played) + "     ",
                        EnumChatFormatting.AQUA + "FK/game : " + (fkpergame > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.3f", fkpergame)
                },
        };


        IChatComponent msg;

        if (unlocked || classname.equals("hunter") || classname.equals("shark") || classname.equals("cow")) {

            msg = new ChatComponentText(EnumChatFormatting.AQUA + bar() + "\n")

                    .appendSibling(PlanckeHeaderText(formattedname, playername, " - Mega Walls " + classnameuppercase + " stats"))

                    .appendSibling(new ChatComponentText("\n" + "\n" + alignText(matrix1))
                            .setChatStyle(new ChatStyle()
                                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(

                                            EnumChatFormatting.GRAY + "Disclaimer : Class Final K/D Ratio's are not accurate for some players, as final-deaths for classes weren't tracked prior to the Mythic update.")))))

                    .appendSibling(new ChatComponentText(alignText(matrix2) +

                            centerLine(EnumChatFormatting.GREEN + "Selected skin : " + EnumChatFormatting.GOLD + (chosen_skin_class == null ? (classnameuppercase == null ? "None" : classnameuppercase) : chosen_skin_class)) + "\n" + "\n"

                            + centerLine(EnumChatFormatting.GREEN + "Classpoints : " + EnumChatFormatting.GOLD + classpoints + " "
                            + EnumChatFormatting.GREEN + " Playtime (approx.) : " + EnumChatFormatting.GOLD + String.format("%.2f", classname_time_played / 60f) + "h") + "\n"

                            + centerLine(EnumChatFormatting.GREEN + "Kit : "
                            + (skill_level_d == 5 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + intToRoman(skill_level_d) + " "
                            + (skill_level_a == 5 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + intToRoman(skill_level_a) + " "
                            + (skill_level_b == 3 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + intToRoman(skill_level_b) + " "
                            + (skill_level_c == 3 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + intToRoman(skill_level_c) + " "
                            + (skill_level_g == 3 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + intToRoman(skill_level_g) + " "

                            + EnumChatFormatting.GREEN + "Prestige : " + (prestige == 0 ? EnumChatFormatting.DARK_GRAY : EnumChatFormatting.GOLD) + intToRoman(prestige) + " "
                            + EnumChatFormatting.GREEN + "Echest rows : " + (enderchest_rows == 5 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + enderchest_rows) + "\n"

                            + EnumChatFormatting.AQUA + bar()));

        } else {

            msg = new ChatComponentText(EnumChatFormatting.AQUA + bar() + "\n"
                    + centerLine(formattedname + EnumChatFormatting.GOLD + " - Mega Walls " + classnameuppercase + " stats") + "\n" + "\n"
                    + centerLine(formattedname + EnumChatFormatting.RED + " didn't purchased " + EnumChatFormatting.GOLD + classnameuppercase) + "\n"
                    + EnumChatFormatting.AQUA + bar());

        }

        return msg;

    }

    public int getClasspoints() {
        return classpoints;
    }

    public int getCoins() {
        return coins;
    }

}
