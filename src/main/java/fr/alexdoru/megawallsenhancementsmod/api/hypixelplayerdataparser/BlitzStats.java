package fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser;

import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import static fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil.*;

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

        final JsonObject statsdata = playerData.get("stats").getAsJsonObject();

        if (statsdata == null) {
            return;
        }

        final JsonObject bsgData = JsonUtil.getJsonObject(statsdata, "HungerGames");

        if (bsgData == null) {
            return;
        }

        coins = JsonUtil.getInt(bsgData, "coins");
        //wins = JsonUtil.getInt(bsgData, "wins");
        wins_solo_normal = JsonUtil.getInt(bsgData, "wins_solo_normal");
        wins_teams_normal = JsonUtil.getInt(bsgData, "wins_teams_normal");
        deaths = JsonUtil.getInt(bsgData, "deaths");
        kills = JsonUtil.getInt(bsgData, "kills");
        //kills_solo_normal = JsonUtil.getInt(bsgData, "kills_solo_normal");
        //kills_teams_normal = JsonUtil.getInt(bsgData, "kills_teams_normal");
        time_played = JsonUtil.getInt(bsgData, "time_played");
        defaultkit = JsonUtil.getString(bsgData, "defaultkit");

        kdr = (float) kills / (deaths == 0 ? 1 : (float) deaths);

    }

    public IChatComponent getFormattedMessage(String formattedName, String playername) {

        final String[][] matrix = {
                {
                        EnumChatFormatting.AQUA + "Kills : " + EnumChatFormatting.GOLD + formatInt(kills) + " ",
                        EnumChatFormatting.AQUA + "Deaths : " + EnumChatFormatting.RED + formatInt(deaths) + " ",
                        EnumChatFormatting.AQUA + "K/D : " + (kdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.2f", kdr)
                },
        };

        final String[][] matrix2 = {
                {
                        EnumChatFormatting.AQUA + "Wins solo : " + EnumChatFormatting.GOLD + formatInt(wins_solo_normal) + " ",
                        EnumChatFormatting.AQUA + "Wins team : " + EnumChatFormatting.GOLD + formatInt(wins_teams_normal) + " ",
                },
        };

        return new ChatComponentText(EnumChatFormatting.AQUA + bar() + "\n")
                .appendSibling(PlanckeHeaderText(formattedName, playername, " - Blitz stats"))
                .appendSibling(new ChatComponentText("\n" + "\n"))
                .appendSibling(new ChatComponentText(alignText(matrix)))
                .appendSibling(new ChatComponentText(alignText(matrix2) + "\n"))
                .appendSibling(new ChatComponentText(
                        defaultkit == null ? "" : centerLine(EnumChatFormatting.GREEN + "Selected Kit : " + EnumChatFormatting.GOLD + defaultkit + "\n")
                                + centerLine(EnumChatFormatting.GREEN + " Playtime : " + EnumChatFormatting.GOLD + String.format("%.2f", time_played / 3600f) + "h") + "\n"
                                + centerLine(EnumChatFormatting.GREEN + "Coins : " + EnumChatFormatting.GOLD + formatInt(coins))))
                .appendSibling(new ChatComponentText(EnumChatFormatting.AQUA + bar()));

    }

}
