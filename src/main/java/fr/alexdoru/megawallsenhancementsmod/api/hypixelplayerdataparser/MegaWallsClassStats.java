package fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
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
    private int classname_kills;
    private int classname_final_assists;
    private int classname_deaths;
    private int classname_wins;
    private int classname_wins_face_off;
    private int classname_losses;
    private int classname_final_kills;
    private int classname_final_deaths;
    private int classname_time_played;
    // to compute
    private float kdr;
    private float fkdr;
    private float wlr;
    private float fkadr;
    private float wither_damage_game;
    private float def_kill_game;
    private int games_played;
    private float fkpergame;
    private int classname_final_assists_standard;
    private int classpoints;
    // in the mwdata -> classes -> classname
    private boolean unlocked;
    private int skill_level_a = 1; // ability
    private int skill_level_b = 1; // passive 1
    private int skill_level_c = 1; // passive 2
    private int skill_level_d = 1; // kit
    private int skill_level_g = 1; // gathering
    private int prestige;
    private int enderchest_rows = 3;

    /*
     * fields below are for the game stats
     */

    //private int classname_games_played;
    private int classname_assists;
    private int classname_wither_damage;
    private int classname_wither_kills;
    private int classname_defender_kills;
    //private int classname_defender_assists;
    private int classname_a_activations;
    //private int classname_a_activations_deathmatch;
    private int classname_a_damage_dealt;
    private int classname_arrows_fired;
    private int classname_arrows_hit;
    private int classname_blocks_placed;
    private int classname_meters_walked;
    private int classname_self_healed;
    private int classname_allies_healed;

    public MegaWallsClassStats(JsonObject playerData, String classnameIn) {

        if (playerData == null) {
            return;
        }
        final JsonObject statsObj = JsonUtil.getJsonObject(playerData, "stats");
        if (statsObj == null) {
            return;
        }
        final JsonObject megaWallsStatsObj = JsonUtil.getJsonObject(statsObj, "Walls3");
        if (megaWallsStatsObj == null) {
            return;
        }

        classname = classnameIn.toLowerCase();
        classnameuppercase = classnameIn;
        chosen_skin_class = JsonUtil.getString(megaWallsStatsObj, "chosen_skin_" + classnameuppercase);

        classname_kills = JsonUtil.getInt(megaWallsStatsObj, classname + "_kills");
        classname_final_assists = JsonUtil.getInt(megaWallsStatsObj, classname + "_final_assists");
        classname_deaths = JsonUtil.getInt(megaWallsStatsObj, classname + "_deaths");
        classname_wins = JsonUtil.getInt(megaWallsStatsObj, classname + "_wins");
        classname_wins_face_off = JsonUtil.getInt(megaWallsStatsObj, classname + "_wins_face_off");
        classname_losses = JsonUtil.getInt(megaWallsStatsObj, classname + "_losses");
        classname_final_kills = JsonUtil.getInt(megaWallsStatsObj, classname + "_final_kills");
        classname_final_deaths = JsonUtil.getInt(megaWallsStatsObj, classname + "_final_deaths");
        classname_time_played = JsonUtil.getInt(megaWallsStatsObj, classname + "_time_played");

        kdr = (float) classname_kills / (classname_deaths == 0 ? 1 : (float) classname_deaths);
        fkdr = (float) classname_final_kills / (classname_final_deaths == 0 ? 1 : (float) classname_final_deaths);
        wlr = (float) classname_wins / (classname_losses == 0 ? 1 : (float) classname_losses);
        fkadr = (float) (classname_final_kills + classname_final_assists) / (classname_final_deaths == 0 ? 1 : (float) classname_final_deaths);

        classpoints = JsonUtil.getInt(megaWallsStatsObj, classname + "_class_points");

        games_played = classname_wins + classname_losses; // doesn't count the draws
        fkpergame = (float) classname_final_kills / (games_played == 0 ? 1 : (float) games_played);

        /*
         * fields below are for the game stats
         */
        classname_assists = JsonUtil.getInt(megaWallsStatsObj, classname + "_assists");
        classname_wither_damage = JsonUtil.getInt(megaWallsStatsObj, classname + "_wither_damage");
        classname_wither_kills = JsonUtil.getInt(megaWallsStatsObj, classname + "_wither_kills");
        classname_defender_kills = JsonUtil.getInt(megaWallsStatsObj, classname + "_defender_kills");
        //classname_defender_assists = JsonUtil.getInt(mwdata,classname + "_defender_assists");
        classname_a_activations = JsonUtil.getInt(megaWallsStatsObj, classname + "_a_activations");
        //classname_a_activations_deathmatch = JsonUtil.getInt(mwdata,classname + "_a_activations_deathmatch");
        classname_a_damage_dealt = JsonUtil.getInt(megaWallsStatsObj, classname + "_a_damage_dealt");
        classname_arrows_fired = JsonUtil.getInt(megaWallsStatsObj, classname + "_arrows_fired");
        classname_arrows_hit = JsonUtil.getInt(megaWallsStatsObj, classname + "_arrows_hit");
        classname_blocks_placed = JsonUtil.getInt(megaWallsStatsObj, classname + "_blocks_placed");
        classname_meters_walked = JsonUtil.getInt(megaWallsStatsObj, classname + "_meters_walked");
        classname_self_healed = JsonUtil.getInt(megaWallsStatsObj, classname + "_self_healed");
        classname_allies_healed = JsonUtil.getInt(megaWallsStatsObj, classname + "_allies_healed");
        wither_damage_game = classname_wither_damage / (games_played == 0 ? 1 : (float) games_played);
        def_kill_game = classname_defender_kills / (games_played == 0 ? 1 : (float) games_played);

        final JsonObject classesdata = JsonUtil.getJsonObject(megaWallsStatsObj, "classes");
        if (classesdata == null) {
            return;
        }

        final JsonObject classeobj = JsonUtil.getJsonObject(classesdata, classname);
        if (classeobj == null) {
            return;
        }

        unlocked = JsonUtil.getBoolean(classeobj, "unlocked");
        skill_level_a = Math.max(JsonUtil.getInt(classeobj, "skill_level_a"), 1);
        skill_level_b = Math.max(JsonUtil.getInt(classeobj, "skill_level_b"), 1);
        skill_level_c = Math.max(JsonUtil.getInt(classeobj, "skill_level_c"), 1);
        skill_level_d = Math.max(JsonUtil.getInt(classeobj, "skill_level_d"), 1);
        skill_level_g = Math.max(JsonUtil.getInt(classeobj, "skill_level_g"), 1);
        prestige = JsonUtil.getInt(classeobj, "prestige");
        enderchest_rows = Math.max(JsonUtil.getInt(classeobj, "enderchest_rows"), 3);

    }

    /*
     * Returns this minus the input
     */
    public void minus(MegaWallsClassStats mwclassStats) throws IllegalArgumentException, IllegalAccessException {
        for (final Field field : this.getClass().getDeclaredFields()) {
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
    public IChatComponent getGameStatMessage(String formattedname, String gameDuration) {

        final float arrowaccuracy = 100f * (float) classname_arrows_hit / (float) (classname_arrows_fired == 0 ? 1 : classname_arrows_fired);

        final String[][] matrix1 = {
                {
                        EnumChatFormatting.GREEN + "Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_kills) + " ",
                        EnumChatFormatting.GREEN + "Assists : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_assists)
                },

                {
                        EnumChatFormatting.GREEN + "Final Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_final_kills) + " ",
                        EnumChatFormatting.GREEN + "Final Assists : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_final_assists_standard),
                },

                {
                        EnumChatFormatting.GREEN + "Deaths : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_deaths) + " ",
                        EnumChatFormatting.GREEN + "Final Death : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_final_deaths),
                },

                {
                        EnumChatFormatting.GREEN + "Ability activations : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_a_activations) + " ",
                        EnumChatFormatting.GREEN + "Ability Damage : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_a_damage_dealt / 2) + EnumChatFormatting.RED + "❤",
                },

                {
                        EnumChatFormatting.GREEN + "Wither Damage : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_wither_damage) + " ",
                        EnumChatFormatting.GREEN + "Defender kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_defender_kills),
                },

                {
                        EnumChatFormatting.GREEN + "Self healed : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_self_healed / 2) + EnumChatFormatting.RED + "❤" + " ",
                        EnumChatFormatting.GREEN + "Allies healed : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_allies_healed / 2) + EnumChatFormatting.RED + "❤",
                },

                {
                        EnumChatFormatting.GREEN + "Blocks placed : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_blocks_placed) + " ",
                        EnumChatFormatting.GREEN + "Meters walked : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_meters_walked),
                },

                {
                        EnumChatFormatting.GREEN + "Arrow shots : " + EnumChatFormatting.GOLD + classname_arrows_fired + " ",
                        EnumChatFormatting.GREEN + "Arrow hits : " + EnumChatFormatting.GOLD + classname_arrows_hit,
                },

                {
                        EnumChatFormatting.GREEN + "Arrows accuracy : " + EnumChatFormatting.GOLD + String.format("%.2f", arrowaccuracy) + "%" + " ",
                        EnumChatFormatting.GREEN + "Wither Kills : " + EnumChatFormatting.GOLD + classname_wither_kills,
                }
        };

        final IChatComponent imsg = new ChatComponentText(EnumChatFormatting.BLUE + ChatUtil.bar() + "\n")
                .appendText(ChatUtil.centerLine(formattedname + EnumChatFormatting.GOLD + " - Game stats - " + classnameuppercase) + "\n")
                .appendText(ChatUtil.centerLine(EnumChatFormatting.GREEN + "Game Duration : " + EnumChatFormatting.GOLD + gameDuration));

        if (games_played != 1) {
            imsg.appendText("\n" + ChatUtil.centerLine(EnumChatFormatting.DARK_RED + "These stats are for " + games_played + " games, not one."));
        }

        imsg.appendText("\n" + "\n" + ChatUtil.alignText(matrix1));
        imsg.appendText(EnumChatFormatting.BLUE + ChatUtil.bar());
        return imsg;
    }

    public void printMessage(String formattedname, String playername) {

        final String[][] matrix1 = {
                {
                        EnumChatFormatting.AQUA + "Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_kills) + " ",
                        EnumChatFormatting.AQUA + "Deaths : " + EnumChatFormatting.RED + ChatUtil.formatInt(classname_deaths) + " ",
                        EnumChatFormatting.AQUA + "K/D Ratio : " + (kdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.3f", kdr)
                },

                {
                        EnumChatFormatting.AQUA + "Final Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_final_kills) + " ",
                        EnumChatFormatting.AQUA + "Final Deaths : " + EnumChatFormatting.RED + ChatUtil.formatInt(classname_final_deaths) + " ",
                        EnumChatFormatting.AQUA + "FK/D Ratio : " + (fkdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.3f", fkdr)
                },

                {
                        EnumChatFormatting.AQUA + "Wins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_wins) + " ",
                        EnumChatFormatting.AQUA + "Losses : " + EnumChatFormatting.RED + ChatUtil.formatInt(classname_losses) + " ",
                        EnumChatFormatting.AQUA + "W/L Ratio : " + (wlr > 0.33f ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.3f", wlr)
                },

                {
                        EnumChatFormatting.AQUA + "FA : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_final_assists) + " ",
                        EnumChatFormatting.AQUA + "FKA/D Ratio : " + (fkadr > 1f ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.1f", fkadr),
                        ""
                }
        };

        final String[][] matrix2 = {
                {
                        EnumChatFormatting.AQUA + "Games played : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(games_played) + "     ",
                        EnumChatFormatting.AQUA + "FK/game : " + (fkpergame > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.3f", fkpergame)
                },

                {
                        EnumChatFormatting.AQUA + "Wither damage : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_wither_damage) + "     ",
                        EnumChatFormatting.AQUA + "Defending Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_defender_kills)
                },

                {
                        EnumChatFormatting.AQUA + "Wither dmg/game : " + EnumChatFormatting.GOLD + ((int) wither_damage_game) + "     ",
                        EnumChatFormatting.AQUA + "Def Kills/game : " + EnumChatFormatting.GOLD + String.format("%.2f", def_kill_game)
                },

                {
                        EnumChatFormatting.AQUA + "Wither kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_wither_kills) + "     ",
                        EnumChatFormatting.AQUA + "Faceoff wins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(classname_wins_face_off)
                }
        };


        final IChatComponent msg;

        if (this.isUnlocked()) {

            msg = new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n")
                    .appendSibling(ChatUtil.PlanckeHeaderText(formattedname, playername, " - Mega Walls " + classnameuppercase + " stats"))
                    .appendSibling(new ChatComponentText("\n" + "\n" + ChatUtil.alignText(matrix1) + "\n")
                            .setChatStyle(new ChatStyle()
                                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.GRAY + "Disclaimer : Class Final K/D Ratio's are not accurate for some players, as final-deaths for classes weren't tracked prior to the Mythic update.")))))
                    .appendText(ChatUtil.alignText(matrix2) + "\n"
                            + ChatUtil.centerLine(EnumChatFormatting.GREEN + "Classpoints : " + EnumChatFormatting.GOLD + classpoints + " "
                            + EnumChatFormatting.GREEN + " Playtime (approx.) : " + EnumChatFormatting.GOLD + String.format("%.2f", classname_time_played / 60f) + "h") + "\n"
                            + ChatUtil.centerLine(EnumChatFormatting.GREEN + "Kit : " + this.getFormattedKitUpgrades() + " "
                            + EnumChatFormatting.GREEN + "Prestige : " + (prestige == 0 ? EnumChatFormatting.DARK_GRAY : EnumChatFormatting.GOLD) + ChatUtil.intToRoman(prestige) + " "
                            + EnumChatFormatting.GREEN + "Echest rows : " + (enderchest_rows == 5 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + enderchest_rows) + "\n"
                            + ChatUtil.centerLine(EnumChatFormatting.GREEN + "Selected skin : " + EnumChatFormatting.GOLD + (chosen_skin_class == null ? (classnameuppercase == null ? "None" : classnameuppercase) : chosen_skin_class)) + "\n"
                            + EnumChatFormatting.AQUA + ChatUtil.bar());

        } else {

            msg = new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n"
                    + ChatUtil.centerLine(formattedname + EnumChatFormatting.GOLD + " - Mega Walls " + classnameuppercase + " stats") + "\n" + "\n"
                    + ChatUtil.centerLine(formattedname + EnumChatFormatting.RED + " didn't purchased " + EnumChatFormatting.GOLD + classnameuppercase) + "\n"
                    + EnumChatFormatting.AQUA + ChatUtil.bar());

        }

        ChatUtil.addChatMessage(msg);

    }

    public boolean isUnlocked() {
        return unlocked || classname.equals("hunter") || classname.equals("shark") || classname.equals("cow");
    }

    public boolean isMythic() {
        return classname.equals("assassin") ||
                classname.equals("werewolf") ||
                classname.equals("phoenix") ||
                classname.equals("automaton") ||
                classname.equals("moleman") ||
                classname.equals("renegade") ||
                classname.equals("snowman");
    }

    /**
     * Returns an array with :
     * - kit level
     * - ability level
     * - passive 1 level
     * - passive 2 level
     * - gathering level
     */
    public int[] getKitUpgrades() {
        return new int[]{
                skill_level_d,
                skill_level_a,
                skill_level_b,
                skill_level_c,
                skill_level_g
        };
    }

    public String getFormattedKitUpgrades() {
        return (skill_level_d == 5 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(skill_level_d) + " "
                + (skill_level_a == 5 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(skill_level_a) + " "
                + (skill_level_b == 3 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(skill_level_b) + " "
                + (skill_level_c == 3 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(skill_level_c) + " "
                + (skill_level_g == 3 ? EnumChatFormatting.GOLD : EnumChatFormatting.DARK_GRAY) + ChatUtil.intToRoman(skill_level_g);
    }

    public String getClassname() {
        return classnameuppercase;
    }

    public int getClasspoints() {
        return classpoints;
    }

}
