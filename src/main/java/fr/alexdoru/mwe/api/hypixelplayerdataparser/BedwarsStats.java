package fr.alexdoru.mwe.api.hypixelplayerdataparser;

import com.google.gson.JsonObject;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.utils.JsonUtil;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class BedwarsStats {

    private int coins;
    private int wins;
    private int losses;
    private int kills;
    private int deaths;
    private int finalKills;
    private int finalDeaths;
    private int bedsBroken;
    private int bedsLost;
    private int gamesPlayed;
    private int bedwarsExperience;
    private int level;  // Updated to use the level directly from JSON

    private float kdr;
    private float fkdr;
    private float wlr;
    private float bblr; // Beds Broken/Lost Ratio

    public BedwarsStats(JsonObject playerData) {

        if (playerData == null) {
            return;
        }
        final JsonObject statsObj = JsonUtil.getJsonObject(playerData, "stats");
        if (statsObj == null) {
            return;
        }
        final JsonObject bwdata = JsonUtil.getJsonObject(statsObj, "Bedwars");
        if (bwdata == null) {
            return;
        }

        coins = JsonUtil.getInt(bwdata, "coins");
        wins = JsonUtil.getInt(bwdata, "wins_bedwars");
        losses = JsonUtil.getInt(bwdata, "losses_bedwars");
        kills = JsonUtil.getInt(bwdata, "kills_bedwars");
        deaths = JsonUtil.getInt(bwdata, "deaths_bedwars");
        finalKills = JsonUtil.getInt(bwdata, "final_kills_bedwars");
        finalDeaths = JsonUtil.getInt(bwdata, "final_deaths_bedwars");
        bedsBroken = JsonUtil.getInt(bwdata, "beds_broken_bedwars");
        bedsLost = JsonUtil.getInt(bwdata, "beds_lost_bedwars");
        gamesPlayed = JsonUtil.getInt(bwdata, "games_played_bedwars");
        bedwarsExperience = JsonUtil.getInt(bwdata, "Experience");
        
        // Assuming the level is stored in the player achievements
        JsonObject achievements = JsonUtil.getJsonObject(playerData, "achievements");
        if (achievements != null) {
            level = JsonUtil.getInt(achievements, "bedwars_level");
        } else {
            level = -1; // Default value if level is not found
        }

        kdr = (float) kills / (deaths == 0 ? 1 : (float) deaths);
        fkdr = (float) finalKills / (finalDeaths == 0 ? 1 : (float) finalDeaths);
        wlr = (float) wins / (losses == 0 ? 1 : (float) losses);
        bblr = (float) bedsBroken / (bedsLost == 0 ? 1 : (float) bedsLost);
    }

    private String formatBedwarsLevel(int level) {
        // Format level as per your requirements
        return EnumChatFormatting.GRAY + "Level: " + level;
    }

    public void printMessage(String formattedName, String playername) {

        final String[][] matrix = {
                {
                        EnumChatFormatting.YELLOW + "Overall : ",
                        EnumChatFormatting.AQUA + "K : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(kills + finalKills) + " ",
                        EnumChatFormatting.AQUA + "D : " + EnumChatFormatting.RED + ChatUtil.formatInt(deaths) + " ",
                        EnumChatFormatting.AQUA + "K/D : " + (kdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", kdr)
                },
                {
                        "",
                        EnumChatFormatting.AQUA + "FK : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(finalKills) + " ",
                        EnumChatFormatting.AQUA + "FD : " + EnumChatFormatting.RED + ChatUtil.formatInt(finalDeaths) + " ",
                        EnumChatFormatting.AQUA + "FK/D : " + (fkdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", fkdr) + "\n"
                },
                {
                        "",
                        EnumChatFormatting.AQUA + "W : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(wins) + " ",
                        EnumChatFormatting.AQUA + "L : " + EnumChatFormatting.RED + ChatUtil.formatInt(losses) + " ",
                        EnumChatFormatting.AQUA + "W/L : " + (wlr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", wlr) + "\n"
                },
                {
                        EnumChatFormatting.YELLOW + "Beds : ",
                        EnumChatFormatting.AQUA + "B : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(bedsBroken) + " ",
                        EnumChatFormatting.AQUA + "L : " + EnumChatFormatting.RED + ChatUtil.formatInt(bedsLost) + " ",
                        EnumChatFormatting.AQUA + "B/L : " + (bblr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", bblr)
                }
        };

        ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n")
                .appendSibling(ChatUtil.PlanckeHeaderText(level == -1 ? formattedName : formattedName + EnumChatFormatting.GOLD + " - Level " + level, playername, " - Bedwars stats"))
                .appendText("\n" + "\n")
                .appendText(ChatUtil.centerLine(formatBedwarsLevel(level)) + "\n" + "\n")
                .appendText(ChatUtil.alignText(matrix) + "\n")
                .appendText(
                        ChatUtil.centerLine(EnumChatFormatting.GREEN + "Coins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(coins)
                                + EnumChatFormatting.GREEN + " Games Played : " + EnumChatFormatting.AQUA + ChatUtil.formatInt(gamesPlayed)) + "\n")
                .appendText(EnumChatFormatting.AQUA + ChatUtil.bar()));
    }
}
