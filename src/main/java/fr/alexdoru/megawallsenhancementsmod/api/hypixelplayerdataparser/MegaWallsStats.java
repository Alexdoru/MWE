package fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.enums.MWClass;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.ColorUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.LinkedHashMap;
import java.util.Map;

public class MegaWallsStats {

    /**
     * First element of the array is the prestige level, second element is the amount of classpoints
     */
    private final LinkedHashMap<String, Integer[]> classpointsMap = new LinkedHashMap<>();
    private String chosen_class;
    private String chosen_skin_class;
    private int coins;
    private int mythic_favor;
    private int wither_damage;
    private int wither_kills;
    private int wins;
    private int wins_face_off;
    private int wins_practice;
    private int losses;
    private int kills;
    private int defender_kills;
    //private int assists; // useless
    private int deaths;
    private int final_kills;
    //private int final_kills_standard; // useless
    //private int final_assists; // useless
    //private int final_assists_standard; // useless
    private int final_deaths;
    private float kdr;
    private float fkdr;
    private float wlr;
    private float wither_damage_game;
    private float def_kill_game;
    private int time_played;
    private int nbprestiges;
    private int total_classpoints;
    private int legendary_skins;

    private int games_played;
    private float fkpergame;

    private JsonObject classesdata = null;

    public MegaWallsStats(JsonObject playerData) {

        if (playerData == null) {
            return;
        }

        final JsonObject statsdata = playerData.get("stats").getAsJsonObject();

        if (statsdata == null) {
            return;
        }

        final JsonObject mwdata = JsonUtil.getJsonObject(statsdata, "Walls3");

        if (mwdata == null) {
            return;
        }

        chosen_class = JsonUtil.getString(mwdata, "chosen_class");
        chosen_skin_class = JsonUtil.getString(mwdata, "chosen_skin_" + chosen_class);
        coins = JsonUtil.getInt(mwdata, "coins");
        mythic_favor = JsonUtil.getInt(mwdata, "mythic_favor");
        wither_damage = JsonUtil.getInt(mwdata, "wither_damage") + JsonUtil.getInt(mwdata, "witherDamage"); // add both to get wither damage
        wither_kills = JsonUtil.getInt(mwdata, "wither_kills");
        wins = JsonUtil.getInt(mwdata, "wins");
        wins_face_off = JsonUtil.getInt(mwdata, "wins_face_off");
        wins_practice = JsonUtil.getInt(mwdata, "wins_practice");
        losses = JsonUtil.getInt(mwdata, "losses");
        kills = JsonUtil.getInt(mwdata, "kills");
        defender_kills = JsonUtil.getInt(mwdata, "defender_kills");
        //assists = JsonUtil.getInt(mwdata,"assists");
        deaths = JsonUtil.getInt(mwdata, "deaths");
        final_kills = JsonUtil.getInt(mwdata, "final_kills");
        //final_kills_standard = JsonUtil.getInt(mwdata,"final_kills_standard");
        //final_assists = JsonUtil.getInt(mwdata,"final_assists");
        //final_assists_standard = JsonUtil.getInt(mwdata,"final_assists_standard");
        final_deaths = JsonUtil.getInt(mwdata, "final_deaths") + JsonUtil.getInt(mwdata, "finalDeaths");

        kdr = (float) kills / (deaths == 0 ? 1 : (float) deaths);
        fkdr = (float) final_kills / (final_deaths == 0 ? 1 : (float) final_deaths);
        wlr = (float) wins / (losses == 0 ? 1 : (float) losses);

        time_played = JsonUtil.getInt(mwdata, "time_played") + JsonUtil.getInt(mwdata, "time_played_standard"); // additionner les deux / en minutes
        games_played = wins + losses; // doesn't count the draws
        fkpergame = (float) final_kills / (games_played == 0 ? 1 : (float) games_played);
        wither_damage_game = wither_damage / (games_played == 0 ? 1 : (float) games_played);
        def_kill_game = defender_kills / (games_played == 0 ? 1 : (float) games_played);

        // computes the number of prestiges
        classesdata = JsonUtil.getJsonObject(mwdata, "classes");
        if (classesdata != null) {
            for (final MWClass mwclass : MWClass.values()) {
                final String classname = mwclass.className.toLowerCase();
                final JsonObject classeobj = JsonUtil.getJsonObject(classesdata, classname);
                if (classeobj == null) {
                    continue;
                }
                final int prestige = JsonUtil.getInt(classeobj, "prestige");
                nbprestiges = nbprestiges + prestige;
                final int classpoints = JsonUtil.getInt(mwdata, classname + "_final_kills_standard")
                        + JsonUtil.getInt(mwdata, classname + "_final_assists_standard")
                        + JsonUtil.getInt(mwdata, classname + "_wins_standard") * 10;
                total_classpoints += classpoints;
                classpointsMap.put(classname, new Integer[]{prestige, classpoints});
            }
        }

        try {
            final JsonElement achievementsOneTime = playerData.get("achievementsOneTime");
            if (achievementsOneTime != null && achievementsOneTime.isJsonArray()) {
                for (final JsonElement jsonElement : achievementsOneTime.getAsJsonArray()) {
                    if (jsonElement.isJsonArray()) {
                        continue;
                    }
                    final String achievementName = jsonElement.getAsString();
                    if (achievementName != null && achievementName.startsWith("walls3_legendary_")) {
                        legendary_skins++;
                    }
                }
            }
        } catch (Exception ignored) {}

    }

    public float getFkdr() {
        return fkdr;
    }

    public float getWlr() {
        return wlr;
    }

    public float getFkpergame() {
        return fkpergame;
    }

    public int getGames_played() {
        return games_played;
    }

    public int getLegSkins() {
        return legendary_skins;
    }

    public JsonObject getClassesdata() {
        return classesdata;
    }

    public IChatComponent getClassPointsMessage(String formattedname, String playername) {
        final IChatComponent imsg = new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n")
                .appendSibling(ChatUtil.PlanckeHeaderText(formattedname, playername, " - Mega Walls Classpoints\n\n"));
        for (final Map.Entry<String, Integer[]> entry : classpointsMap.entrySet()) {
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + ChatUtil.capitalizeFirstLetter(entry.getKey()))
                    .setChatStyle(new ChatStyle()
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click for " + entry.getKey() + " stats")))
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plancke " + playername + " mw " + entry.getKey()))));
            if (entry.getValue()[0] != 0) {
                imsg.appendSibling(new ChatComponentText(EnumChatFormatting.GOLD + " P" + ChatUtil.intToRoman(entry.getValue()[0])));
            }
            final Integer classpoints = entry.getValue()[1];
            imsg.appendSibling(new ChatComponentText(" : " + ColorUtil.getPrestigeVColor(classpoints) + classpoints + "\n"));
        }
        imsg.appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + "Total : " + EnumChatFormatting.GOLD + total_classpoints + "\n"));
        imsg.appendSibling(new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar()));
        return imsg;
    }

    public IChatComponent getFormattedMessage(String formattedname, String playername) {

        final String[][] matrix1 = {
                {
                        EnumChatFormatting.AQUA + "Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(kills) + " ",
                        EnumChatFormatting.AQUA + "Deaths : " + EnumChatFormatting.RED + ChatUtil.formatInt(deaths) + " ",
                        EnumChatFormatting.AQUA + "K/D Ratio : " + (kdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.3f", kdr)
                },

                {
                        EnumChatFormatting.AQUA + "Final Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(final_kills) + " ",
                        EnumChatFormatting.AQUA + "Final Deaths : " + EnumChatFormatting.RED + ChatUtil.formatInt(final_deaths) + " ",
                        EnumChatFormatting.AQUA + "FK/D Ratio : " + (fkdr > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.3f", fkdr)
                },

                {
                        EnumChatFormatting.AQUA + "Wins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(wins) + " ",
                        EnumChatFormatting.AQUA + "Losses : " + EnumChatFormatting.RED + ChatUtil.formatInt(losses) + " ",
                        EnumChatFormatting.AQUA + "W/L Ratio : " + (wlr > 0.25f ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.3f", wlr)
                }};

        final String[][] matrix2 = {
                {
                        EnumChatFormatting.AQUA + "Games played : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(games_played) + "     ",
                        EnumChatFormatting.AQUA + "FK/game : " + (fkpergame > 1 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + String.format("%.3f", fkpergame)
                },

                {
                        EnumChatFormatting.AQUA + "Wither damage : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(wither_damage) + "     ",
                        EnumChatFormatting.AQUA + "Defending Kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(defender_kills)
                },

                {
                        EnumChatFormatting.AQUA + "Wither dmg/game : " + EnumChatFormatting.GOLD + ((int) wither_damage_game) + "     ",
                        EnumChatFormatting.AQUA + "Def Kills/game : " + EnumChatFormatting.GOLD + String.format("%.2f", def_kill_game)
                },

                {
                        EnumChatFormatting.AQUA + "Wither kills : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(wither_kills) + "     ",
                        EnumChatFormatting.AQUA + "Leg skins : " + (legendary_skins == 24 ? EnumChatFormatting.GOLD : EnumChatFormatting.GREEN) + legendary_skins + EnumChatFormatting.GOLD + "/24"
                },

                {
                        EnumChatFormatting.AQUA + "Faceoff wins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(wins_face_off) + "     ",
                        EnumChatFormatting.AQUA + "Practice wins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(wins_practice)
                },

                {
                        EnumChatFormatting.AQUA + "Coins : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(coins) + "     ",
                        EnumChatFormatting.AQUA + "Mythic favors : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(mythic_favor) + "\n"
                }};

        return new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n")
                .appendSibling(ChatUtil.PlanckeHeaderText(formattedname, playername, " - Mega Walls stats"))
                .appendSibling(new ChatComponentText("\n" + "\n" +
                        ChatUtil.centerLine(EnumChatFormatting.GREEN + "Prestiges : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(nbprestiges) + " "
                                + EnumChatFormatting.GREEN + " Playtime (approx.) : " + EnumChatFormatting.GOLD + String.format("%.2f", time_played / 60f) + "h") + "\n"))
                .appendSibling(new ChatComponentText(
                        ChatUtil.centerLine(EnumChatFormatting.GREEN + "Selected class : " + EnumChatFormatting.GOLD + (chosen_class == null ? "None" : chosen_class) + " "
                                + EnumChatFormatting.GREEN + " Selected skin : " + EnumChatFormatting.GOLD + (chosen_skin_class == null ? (chosen_class == null ? "None" : chosen_class) : chosen_skin_class)) + "\n" + "\n")
                        .setChatStyle(new ChatStyle()
                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click for this class' stats")))
                                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plancke " + playername + " mw " + (chosen_class == null ? "None" : chosen_class)))))
                .appendSibling(new ChatComponentText(ChatUtil.alignText(matrix2) + ChatUtil.alignText(matrix1)))
                .appendSibling(new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar()));
    }

}
