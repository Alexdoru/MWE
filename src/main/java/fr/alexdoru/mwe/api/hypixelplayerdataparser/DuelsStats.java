package fr.alexdoru.mwe.api.hypixelplayerdataparser;

import com.google.gson.JsonObject;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.utils.JsonUtil;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class DuelsStats {

    private int gamesPlayed;
    private int wins;
    private int losses;
    private int kills;
    private int deaths;
    private int coins;
    private int bestWinstreak;

    private float kdr;
    private float wlr;

    public DuelsStats(JsonObject playerData) {

        if (playerData == null) {
            return;
        }
        final JsonObject statsObj = JsonUtil.getJsonObject(playerData, "stats");
        if (statsObj == null) {
            return;
        }
        final JsonObject duelsData = JsonUtil.getJsonObject(statsObj, "Duels");
        if (duelsData == null) {
            return;
        }

        // Parsing common stats
        gamesPlayed = JsonUtil.getInt(duelsData, "games_played_duels");
        wins = JsonUtil.getInt(duelsData, "wins");
        losses = JsonUtil.getInt(duelsData, "losses");
        kills = JsonUtil.getInt(duelsData, "kills");
        deaths = JsonUtil.getInt(duelsData, "deaths");
        coins = JsonUtil.getInt(duelsData, "coins");
        bestWinstreak = JsonUtil.getInt(duelsData, "best_all_modes_winstreak");

        // Calculating ratios
        kdr = (float) kills / (deaths == 0 ? 1 : (float) deaths);
        wlr = (float) wins / (losses == 0 ? 1 : (float) losses);
    }

    private String getDivision(int wins) {
        if (wins >= 200000) {
            int ascendedLevel = (wins - 200000) / 20000 + 1;
            return "Ascended " + ascendedLevel;
        } else if (wins >= 180000) {
            return "Divine V";
        } else if (wins >= 160000) {
            return "Divine IV";
        } else if (wins >= 140000) {
            return "Divine III";
        } else if (wins >= 120000) {
            return "Divine II";
        } else if (wins >= 100000) {
            return "Divine I";
        } else if (wins >= 90000) {
            return "Celestial V";
        } else if (wins >= 80000) {
            return "Celestial IV";
        } else if (wins >= 70000) {
            return "Celestial III";
        } else if (wins >= 60000) {
            return "Celestial II";
        } else if (wins >= 50000) {
            return "Celestial I";
        } else if (wins >= 44000) {
            return "Godlike V";
        } else if (wins >= 38000) {
            return "Godlike IV";
        } else if (wins >= 32000) {
            return "Godlike III";
        } else if (wins >= 26000) {
            return "Godlike II";
        } else if (wins >= 20000) {
            return "Godlike I";
        } else if (wins >= 18000) {
            return "Grandmaster V";
        } else if (wins >= 16000) {
            return "Grandmaster IV";
        } else if (wins >= 14000) {
            return "Grandmaster III";
        } else if (wins >= 12000) {
            return "Grandmaster II";
        } else if (wins >= 10000) {
            return "Grandmaster I";
        } else if (wins >= 8800) {
            return "Legend V";
        } else if (wins >= 7600) {
            return "Legend IV";
        } else if (wins >= 6400) {
            return "Legend III";
        } else if (wins >= 5200) {
            return "Legend II";
        } else if (wins >= 4000) {
            return "Legend I";
        } else if (wins >= 3600) {
            return "Master V";
        } else if (wins >= 3200) {
            return "Master IV";
        } else if (wins >= 2800) {
            return "Master III";
        } else if (wins >= 2400) {
            return "Master II";
        } else if (wins >= 2000) {
            return "Master I";
        } else if (wins >= 1800) {
            return "Diamond V";
        } else if (wins >= 1600) {
            return "Diamond IV";
        } else if (wins >= 1400) {
            return "Diamond III";
        } else if (wins >= 1200) {
            return "Diamond II";
        } else if (wins >= 1000) {
            return "Diamond I";
        } else if (wins >= 900) {
            return "Gold V";
        } else if (wins >= 800) {
            return "Gold IV";
        } else if (wins >= 700) {
            return "Gold III";
        } else if (wins >= 600) {
            return "Gold II";
        } else if (wins >= 500) {
            return "Gold I";
        } else if (wins >= 440) {
            return "Iron V";
        } else if (wins >= 380) {
            return "Iron IV";
        } else if (wins >= 320) {
            return "Iron III";
        } else if (wins >= 260) {
            return "Iron II";
        } else if (wins >= 200) {
            return "Iron I";
        } else if (wins >= 180) {
            return "Rookie V";
        } else if (wins >= 160) {
            return "Rookie IV";
        } else if (wins >= 140) {
            return "Rookie III";
        } else if (wins >= 120) {
            return "Rookie II";
        } else {
            return "Rookie I";
        }
    }

    public void printMessage(String formattedName, String playername) {
        final String division = getDivision(wins);

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
                EnumChatFormatting.AQUA + "W/L : " + (wlr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", wlr) + "\n"
            },
        };

        ChatUtil.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n")
                .appendSibling(ChatUtil.PlanckeHeaderText(formattedName + " - " + EnumChatFormatting.GOLD + division, playername, " - Duels stats"))
                .appendText("\n" + "\n")
                .appendText(ChatUtil.alignText(matrix) + "\n")
                .appendText(
                        ChatUtil.centerLine(EnumChatFormatting.GREEN + "Coins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(coins) + " "
                                + EnumChatFormatting.GREEN + " Games Played : " + EnumChatFormatting.AQUA + ChatUtil.formatInt(gamesPlayed) + " "
                                + EnumChatFormatting.GREEN + " Best Winstreak : " + EnumChatFormatting.AQUA + ChatUtil.formatInt(bestWinstreak)) + "\n")
                .appendText(EnumChatFormatting.AQUA + ChatUtil.bar()));
    }    
    
}
