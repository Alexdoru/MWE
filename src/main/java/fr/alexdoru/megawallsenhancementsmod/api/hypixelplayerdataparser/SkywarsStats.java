package fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class SkywarsStats {

    private int coins;
    private int souls;
    private int heads;
    private int shard;
    private int losses;
    private int losses_solo;
    //private int losses_solo_normal;
    //private int losses_solo_insane;
    private int losses_team;
    //private int losses_team_normal;
    //private int losses_team_insane;
    private int wins;
    private int wins_solo;
    //private int wins_solo_normal;
    //private int wins_solo_insane;
    private int wins_team;
    //private int wins_team_normal;
    //private int wins_team_insane;
    private int deaths;
    private int deaths_solo;
    //private int deaths_solo_normal;
    //private int deaths_solo_insane;
    private int deaths_team;
    //private int deaths_team_normal;
    //private int deaths_team_insane;
    private int kills;
    private int kills_solo;
    //private int kills_solo_normal;
    //private int kills_solo_insane;
    private int kills_team;
    //private int kills_team_normal;
    //private int kills_team_insane;
    private int time_played; /*Seconds*/
    private int skywars_experience;
    private String levelFormatted;

    private float kdr;
    private float kdr_solo;
    private float kdr_team;

    private float wlr;
    private float wlr_solo;
    private float wlr_team;

    public SkywarsStats(JsonObject playerData) {

        if (playerData == null) {
            return;
        }
        final JsonObject statsObj = JsonUtil.getJsonObject(playerData, "stats");
        if (statsObj == null) {
            return;
        }
        final JsonObject swdata = JsonUtil.getJsonObject(statsObj, "SkyWars");
        if (swdata == null) {
            return;
        }

        coins = JsonUtil.getInt(swdata, "coins");
        souls = JsonUtil.getInt(swdata, "souls");
        heads = JsonUtil.getInt(swdata, "heads");
        shard = JsonUtil.getInt(swdata, "shard");
        losses = JsonUtil.getInt(swdata, "losses");
        losses_solo = JsonUtil.getInt(swdata, "losses_solo");
        //losses_solo_normal = JsonUtil.getInt(swdata, "losses_solo_normal");
        //losses_solo_insane = JsonUtil.getInt(swdata, "losses_solo_insane");
        losses_team = JsonUtil.getInt(swdata, "losses_team");
        //losses_team_normal = JsonUtil.getInt(swdata, "losses_team_normal");
        //losses_team_insane = JsonUtil.getInt(swdata, "losses_team_insane");
        wins = JsonUtil.getInt(swdata, "wins");
        wins_solo = JsonUtil.getInt(swdata, "wins_solo");
        //wins_solo_normal = JsonUtil.getInt(swdata, "wins_solo_normal");
        //wins_solo_insane = JsonUtil.getInt(swdata, "wins_solo_insane");
        wins_team = JsonUtil.getInt(swdata, "wins_team");
        //wins_team_normal = JsonUtil.getInt(swdata, "wins_team_normal");
        //wins_team_insane = JsonUtil.getInt(swdata, "wins_team_insane");
        deaths = JsonUtil.getInt(swdata, "deaths");
        deaths_solo = JsonUtil.getInt(swdata, "deaths_solo");
        //deaths_solo_normal = JsonUtil.getInt(swdata, "deaths_solo_normal");
        //deaths_solo_insane = JsonUtil.getInt(swdata, "deaths_solo_insane");
        deaths_team = JsonUtil.getInt(swdata, "deaths_team");
        //deaths_team_normal = JsonUtil.getInt(swdata, "deaths_team_normal");
        //deaths_team_insane = JsonUtil.getInt(swdata, "deaths_team_insane");
        kills = JsonUtil.getInt(swdata, "kills");
        kills_solo = JsonUtil.getInt(swdata, "kills_solo");
        //kills_solo_normal = JsonUtil.getInt(swdata, "kills_solo_normal");
        //kills_solo_insane = JsonUtil.getInt(swdata, "kills_solo_insane");
        kills_team = JsonUtil.getInt(swdata, "kills_team");
        //kills_team_normal = JsonUtil.getInt(swdata, "kills_team_normal");
        //kills_team_insane = JsonUtil.getInt(swdata, "kills_team_insane");
        time_played = JsonUtil.getInt(swdata, "time_played");
        skywars_experience = JsonUtil.getInt(swdata, "skywars_experience");
        levelFormatted = JsonUtil.getString(swdata, "levelFormatted");

        kdr = (float) kills / (deaths == 0 ? 1 : (float) deaths);
        kdr_solo = (float) kills_solo / (deaths_solo == 0 ? 1 : (float) deaths_solo);
        kdr_team = (float) kills_team / (deaths_team == 0 ? 1 : (float) deaths_team);

        wlr = (float) wins / (losses == 0 ? 1 : (float) losses);
        wlr_solo = (float) wins_solo / (losses_solo == 0 ? 1 : (float) losses_solo);
        wlr_team = (float) wins_team / (losses_team == 0 ? 1 : (float) losses_team);

    }

    private String formatSkywarsLevel(int skywars_experience) {

        //int current_level;
        final int current_level_xp_max;
        final int xp_remaning;
        final float level_progress;

        if (skywars_experience < 20) {
            //current_level = 1;
            current_level_xp_max = 20;
            xp_remaning = 20 - skywars_experience;
            level_progress = xp_remaning / 20f;
        } else if (skywars_experience < 70) {
            //current_level = 2;
            current_level_xp_max = 50;
            xp_remaning = 70 - skywars_experience;
            level_progress = xp_remaning / 50f;
        } else if (skywars_experience < 150) {
            //current_level = 3;
            current_level_xp_max = 80;
            xp_remaning = 150 - skywars_experience;
            level_progress = xp_remaning / 80f;
        } else if (skywars_experience < 250) {
            //current_level = 4;
            current_level_xp_max = 100;
            xp_remaning = 250 - skywars_experience;
            level_progress = xp_remaning / 100f;
        } else if (skywars_experience < 500) {
            //current_level = 5;
            current_level_xp_max = 250;
            xp_remaning = 500 - skywars_experience;
            level_progress = xp_remaning / 250f;
        } else if (skywars_experience < 1000) {
            //current_level = 6;
            current_level_xp_max = 500;
            xp_remaning = 1000 - skywars_experience;
            level_progress = xp_remaning / 500f;
        } else if (skywars_experience < 2000) {
            //current_level = 7;
            current_level_xp_max = 1000;
            xp_remaning = 2000 - skywars_experience;
            level_progress = xp_remaning / 1000f;
        } else if (skywars_experience < 3500) {
            //current_level = 8;
            current_level_xp_max = 1500;
            xp_remaning = 3500 - skywars_experience;
            level_progress = xp_remaning / 1500f;
        } else if (skywars_experience < 6000) {
            //current_level = 9;
            current_level_xp_max = 2500;
            xp_remaning = 6000 - skywars_experience;
            level_progress = xp_remaning / 2500f;
        } else if (skywars_experience < 10000) {
            //current_level = 10;
            current_level_xp_max = 4000;
            xp_remaning = 10000 - skywars_experience;
            level_progress = xp_remaning / 4000f;
        } else if (skywars_experience < 15000) {
            //current_level = 11;
            current_level_xp_max = 5000;
            xp_remaning = 15000 - skywars_experience;
            level_progress = xp_remaning / 5000f;
        } else {
            //current_level = (skywars_experience - 15000) / 10000 + 12;
            current_level_xp_max = 10000;
            xp_remaning = 10000 - (skywars_experience - 15000) % 10000;
            level_progress = xp_remaning / 10000f;
        }

        final int i = (int) ((1f - level_progress) * 10) + 1;

        final String str1 = new String(new char[i]).replace("\0", "■");
        final String str2 = new String(new char[10 - i]).replace("\0", "■");

        return EnumChatFormatting.GRAY + "Progress: " + EnumChatFormatting.AQUA + (current_level_xp_max - xp_remaning) + EnumChatFormatting.GRAY + "/" + EnumChatFormatting.GREEN + current_level_xp_max + EnumChatFormatting.GRAY + " XP"
                + EnumChatFormatting.DARK_GRAY + " [" + EnumChatFormatting.AQUA + str1 + EnumChatFormatting.GRAY + str2 + EnumChatFormatting.DARK_GRAY + "]";
    }

    public void printMessage(String formattedName, String playername) {

        final String[][] matrix = {
                {
                        EnumChatFormatting.YELLOW + "Overall : ",
                        EnumChatFormatting.AQUA + "K : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(kills) + " ",
                        EnumChatFormatting.AQUA + "D : " + EnumChatFormatting.RED + ChatUtil.formatInt(deaths) + " ",
                        EnumChatFormatting.AQUA + "K/D : " + (kdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", kdr)
                },

                {
                        "",
                        EnumChatFormatting.AQUA + "W : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(wins) + " ",
                        EnumChatFormatting.AQUA + "L : " + EnumChatFormatting.RED + ChatUtil.formatInt(losses) + " ",
                        EnumChatFormatting.AQUA + "W/L : " + (wlr > 1f / 12f ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", wlr) + "\n"
                },

                {
                        EnumChatFormatting.YELLOW + "Solo : ",
                        EnumChatFormatting.AQUA + "K : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(kills_solo) + " ",
                        EnumChatFormatting.AQUA + "D : " + EnumChatFormatting.RED + ChatUtil.formatInt(deaths_solo) + " ",
                        EnumChatFormatting.AQUA + "K/D : " + (kdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", kdr_solo)
                },

                {
                        "",
                        EnumChatFormatting.AQUA + "W : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(wins_solo) + " ",
                        EnumChatFormatting.AQUA + "L : " + EnumChatFormatting.RED + ChatUtil.formatInt(losses_solo) + " ",
                        EnumChatFormatting.AQUA + "W/L : " + (wlr > 1f / 12f ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", wlr_solo) + "\n"
                },

                {
                        EnumChatFormatting.YELLOW + "Team : ",
                        EnumChatFormatting.AQUA + "K : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(kills_team) + " ",
                        EnumChatFormatting.AQUA + "D : " + EnumChatFormatting.RED + ChatUtil.formatInt(deaths_team) + " ",
                        EnumChatFormatting.AQUA + "K/D : " + (kdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", kdr_team)
                },

                {
                        "",
                        EnumChatFormatting.AQUA + "W : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(wins_team) + " ",
                        EnumChatFormatting.AQUA + "L : " + EnumChatFormatting.RED + ChatUtil.formatInt(losses_team) + " ",
                        EnumChatFormatting.AQUA + "W/L : " + (wlr > 1f / 12f ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", wlr_team)
                }
        };

        ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n")
                .appendSibling(ChatUtil.PlanckeHeaderText(levelFormatted == null ? formattedName : formattedName + EnumChatFormatting.GOLD + " - " + levelFormatted, playername, " - Skywars stats"))
                .appendText("\n" + "\n")
                .appendText(ChatUtil.centerLine(formatSkywarsLevel(skywars_experience)) + "\n" + "\n")
                .appendText(ChatUtil.alignText(matrix) + "\n")
                .appendText(
                        ChatUtil.centerLine(EnumChatFormatting.GREEN + "Coins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(coins)
                                + EnumChatFormatting.GREEN + " Souls : " + EnumChatFormatting.AQUA + ChatUtil.formatInt(souls)) + "\n"
                                + ChatUtil.centerLine(EnumChatFormatting.GREEN + " Playtime : " + EnumChatFormatting.GOLD + String.format("%.2f", time_played / 3600f) + "h"
                                + EnumChatFormatting.GREEN + " Heads : " + EnumChatFormatting.DARK_PURPLE + ChatUtil.formatInt(heads)
                                + EnumChatFormatting.GREEN + " Shards : " + EnumChatFormatting.AQUA + ChatUtil.formatInt(shard)))
                .appendText(EnumChatFormatting.AQUA + ChatUtil.bar()));

    }

}
