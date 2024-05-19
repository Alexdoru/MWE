package fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class UHCStats {

    private int coins;
    private int score;
    // teams
    private int deaths;
    private int kills;
    private int heads_eaten;
    private int wins;

    private int deaths_solo;
    private int kills_solo;
    private int heads_eaten_solo;
    private int wins_solo;

    private int deaths_total;
    private int kills_total;
    private int heads_eaten_total;
    private int wins_total;

    private float kdr_team;
    private float kdr_solo;
    private float kdr_total;

    public UHCStats(JsonObject playerData) {

        if (playerData == null) {
            return;
        }
        final JsonObject statsObj = JsonUtil.getJsonObject(playerData, "stats");
        if (statsObj == null) {
            return;
        }
        final JsonObject uhcData = JsonUtil.getJsonObject(statsObj, "UHC");
        if (uhcData == null) {
            return;
        }

        coins = JsonUtil.getInt(uhcData, "coins");
        score = JsonUtil.getInt(uhcData, "score");
        deaths = JsonUtil.getInt(uhcData, "deaths");
        kills = JsonUtil.getInt(uhcData, "kills");
        heads_eaten = JsonUtil.getInt(uhcData, "heads_eaten");
        wins = JsonUtil.getInt(uhcData, "wins");
        deaths_solo = JsonUtil.getInt(uhcData, "deaths_solo");
        kills_solo = JsonUtil.getInt(uhcData, "kills_solo");
        heads_eaten_solo = JsonUtil.getInt(uhcData, "heads_eaten_solo");
        wins_solo = JsonUtil.getInt(uhcData, "wins_solo");

        deaths_total = deaths + deaths_solo;
        kills_total = kills + kills_solo;
        heads_eaten_total = heads_eaten + heads_eaten_solo;
        wins_total = wins + wins_solo;

        kdr_team = (float) kills / (deaths == 0 ? 1 : (float) deaths);
        kdr_solo = (float) kills_solo / (deaths_solo == 0 ? 1 : (float) deaths_solo);
        kdr_total = (float) kills_total / (deaths_total == 0 ? 1 : (float) deaths_total);

    }

    private int getStarLevel(int score) {
        if (score < 10) {
            return 1;
        } else if (score < 60) {
            return 2;
        } else if (score < 210) {
            return 3;
        } else if (score < 460) {
            return 4;
        } else if (score < 960) {
            return 5;
        } else if (score < 1710) {
            return 6;
        } else if (score < 2710) {
            return 7;
        } else if (score < 5210) {
            return 8;
        } else if (score < 10210) {
            return 9;
        } else if (score < 13210) {
            return 10;
        } else if (score < 16210) {
            return 11;
        } else if (score < 19210) {
            return 12;
        } else if (score < 22210) {
            return 13;
        } else if (score < 25210) {
            return 14;
        } else {
            return 15;
        }
    }

    public void printMessage(String formattedName, String playername) {

        final String[][] matrix = {
                {
                        EnumChatFormatting.YELLOW + "Overall : ",
                        EnumChatFormatting.AQUA + "W : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(wins_total) + " ",
                        EnumChatFormatting.AQUA + "Heads : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(heads_eaten_total)
                },

                {
                        EnumChatFormatting.AQUA + "K : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(kills_total) + " ",
                        EnumChatFormatting.AQUA + "D : " + EnumChatFormatting.RED + ChatUtil.formatInt(deaths_total) + " ",
                        EnumChatFormatting.AQUA + "K/D : " + (kdr_total > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", kdr_total) + "\n"
                },

                {
                        EnumChatFormatting.YELLOW + "Solo : ",
                        EnumChatFormatting.AQUA + "W : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(wins_solo) + " ",
                        EnumChatFormatting.AQUA + "Heads : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(heads_eaten_solo)
                },

                {
                        EnumChatFormatting.AQUA + "K : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(kills_solo) + " ",
                        EnumChatFormatting.AQUA + "D : " + EnumChatFormatting.RED + ChatUtil.formatInt(deaths_solo) + " ",
                        EnumChatFormatting.AQUA + "K/D : " + (kdr_solo > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", kdr_solo) + "\n"
                },

                {
                        EnumChatFormatting.YELLOW + "Team : ",
                        EnumChatFormatting.AQUA + "W : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(wins) + " ",
                        EnumChatFormatting.AQUA + "Heads : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(heads_eaten)
                },

                {
                        EnumChatFormatting.AQUA + "K : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(kills) + " ",
                        EnumChatFormatting.AQUA + "D : " + EnumChatFormatting.RED + ChatUtil.formatInt(deaths) + " ",
                        EnumChatFormatting.AQUA + "K/D : " + (kdr_team > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", kdr_team)
                }
        };

        ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n")
                .appendSibling(ChatUtil.PlanckeHeaderText(EnumChatFormatting.GOLD + "[" + getStarLevel(score) + '✫' + "] " + formattedName, playername, " - UHC stats"))
                .appendText("\n" + "\n" + ChatUtil.alignText(matrix) + "\n")
                .appendText(ChatUtil.centerLine(EnumChatFormatting.GREEN + "Score : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(score) + EnumChatFormatting.GREEN + " Coins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(coins) + "\n"))
                .appendText(EnumChatFormatting.AQUA + ChatUtil.bar()));
    }

}
