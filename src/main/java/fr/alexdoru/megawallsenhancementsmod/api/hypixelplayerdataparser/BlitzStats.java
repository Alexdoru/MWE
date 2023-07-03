package fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class BlitzStats {

    private int coins;
    //private int wins;// tf is this
    private int wins_solo_normal; // wins solo
    private int wins_teams_normal; // wins team
    private int deaths;
    private int kills;
    //private int kills_solo_normal;
    //private int kills_teams_normal;
    private int time_played; /*Seconds*/
    private String defaultkit;

    private float kdr;

    public BlitzStats(JsonObject playerData) {

        if (playerData == null) {
            return;
        }
        final JsonObject statsObj = JsonUtil.getJsonObject(playerData, "stats");
        if (statsObj == null) {
            return;
        }
        final JsonObject bsgObj = JsonUtil.getJsonObject(statsObj, "HungerGames");
        if (bsgObj == null) {
            return;
        }

        coins = JsonUtil.getInt(bsgObj, "coins");
        //wins = JsonUtil.getInt(bsgObj, "wins");
        wins_solo_normal = JsonUtil.getInt(bsgObj, "wins_solo_normal");
        wins_teams_normal = JsonUtil.getInt(bsgObj, "wins_teams_normal");
        deaths = JsonUtil.getInt(bsgObj, "deaths");
        kills = JsonUtil.getInt(bsgObj, "kills");
        //kills_solo_normal = JsonUtil.getInt(bsgObj, "kills_solo_normal");
        //kills_teams_normal = JsonUtil.getInt(bsgObj, "kills_teams_normal");
        time_played = JsonUtil.getInt(bsgObj, "time_played");
        defaultkit = JsonUtil.getString(bsgObj, "defaultkit");

        kdr = (float) kills / (deaths == 0 ? 1 : (float) deaths);

    }

    public IChatComponent getFormattedMessage(String formattedName, String playername) {

        final String[][] matrix = {
                {
                        EnumChatFormatting.AQUA + "Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(kills) + " ",
                        EnumChatFormatting.AQUA + "Deaths : " + EnumChatFormatting.RED + ChatUtil.formatInt(deaths) + " ",
                        EnumChatFormatting.AQUA + "K/D : " + (kdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", kdr)
                },
        };

        final String[][] matrix2 = {
                {
                        EnumChatFormatting.AQUA + "Wins solo : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(wins_solo_normal) + " ",
                        EnumChatFormatting.AQUA + "Wins team : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(wins_teams_normal) + " ",
                }
        };

        return new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n")
                .appendSibling(ChatUtil.PlanckeHeaderText(formattedName, playername, " - Blitz stats"))
                .appendText("\n" + "\n")
                .appendText(ChatUtil.alignText(matrix))
                .appendText(ChatUtil.alignText(matrix2) + "\n")
                .appendText(defaultkit == null ? "" : ChatUtil.centerLine(EnumChatFormatting.GREEN + "Selected Kit : " + EnumChatFormatting.GOLD + defaultkit + "\n")
                        + ChatUtil.centerLine(EnumChatFormatting.GREEN + " Playtime : " + EnumChatFormatting.GOLD + String.format("%.2f", time_played / 3600f) + "h") + "\n"
                        + ChatUtil.centerLine(EnumChatFormatting.GREEN + "Coins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(coins)))
                .appendText(EnumChatFormatting.AQUA + ChatUtil.bar());

    }

}
