package fr.alexdoru.megawallsenhancementsmod.api.hypixelplayerdataparser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.enums.MWClass;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
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

    private final LinkedHashMap<String, Integer> classpointsMap = new LinkedHashMap<>();
    private String chosen_class;
    private String chosen_skin_class;
    private int coins = 0;
    private int mythic_favor = 0;
    private int wither_damage = 0;
    private int wither_kills = 0;
    private int wins = 0;
    private int wins_face_off = 0;
    private int wins_practice = 0;
    private int losses = 0;
    private int kills = 0;
    private int defender_kills = 0;
    //private int assists = 0; // useless
    private int deaths = 0;
    private int final_kills = 0;
    //private int final_kills_standard = 0; // useless
    //private int final_assists = 0; // useless
    //private int final_assists_standard = 0; // useless
    private int final_deaths = 0;
    private float kdr = 0;
    private float fkdr = 0;
    private float wlr = 0;
    private float wither_damage_game = 0;
    private float def_kill_game = 0;
    private int time_played = 0;
    private int nbprestiges = 0;
    private int total_classpoints = 0;
    private int legendary_skins = 0;

    private int games_played = 0;
    private float fkpergame = 0;

    private JsonObject classesdata = null;

    public MegaWallsStats(JsonObject playerData) {

        if (playerData == null) {
            return;
        }

        JsonObject statsdata = playerData.get("stats").getAsJsonObject();

        if (statsdata == null) {
            return;
        }

        JsonObject mwdata = JsonUtil.getJsonObject(statsdata, "Walls3");

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

        if (classesdata == null) {
            return;
        }

        for (MWClass mwclass : MWClass.values()) {
            String classname = mwclass.className.toLowerCase();
            JsonObject classeobj = JsonUtil.getJsonObject(classesdata, classname);
            if (classeobj == null) {
                continue;
            }
            nbprestiges = nbprestiges + JsonUtil.getInt(classeobj, "prestige");
            int classpoints = JsonUtil.getInt(mwdata, classname + "_final_kills_standard")
                    + JsonUtil.getInt(mwdata, classname + "_final_assists_standard")
                    + JsonUtil.getInt(mwdata, classname + "_wins_standard") * 10;
            total_classpoints += classpoints;
            classpointsMap.put(classname, classpoints);
        }

        final JsonElement achievementsOneTime = playerData.get("achievementsOneTime");

        if (achievementsOneTime != null && achievementsOneTime.isJsonArray()) {
            for (JsonElement jsonElement : achievementsOneTime.getAsJsonArray()) {
                final String achievementName = jsonElement.getAsString();
                if (achievementName != null && achievementName.startsWith("walls3_legendary_")) {
                    legendary_skins++;
                }
            }
        }

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
        IChatComponent imsg = new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n")
                .appendSibling(ChatUtil.PlanckeHeaderText(formattedname, playername, " - Mega Walls Classpoints\n\n"));
        for (Map.Entry<String, Integer> entry : classpointsMap.entrySet()) {
            imsg.appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + ChatUtil.capitalizeFirstLetter(entry.getKey()))
                            .setChatStyle(new ChatStyle()
                                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click for " + entry.getKey() + " stats")))
                                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plancke " + playername + " mw " + entry.getKey()))))
                    .appendSibling(new ChatComponentText(" : " + formatClasspoint(entry.getValue()) + "\n"));
        }
        imsg.appendSibling(new ChatComponentText(EnumChatFormatting.GREEN + "Total : " + EnumChatFormatting.GOLD + total_classpoints + "\n"));
        imsg.appendSibling(new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar()));
        return imsg;
    }

    private String formatClasspoint(int classpoints) {
        if (classpoints < 2000) {
            return EnumChatFormatting.GRAY.toString() + classpoints;
        } else if (classpoints < 10000) {
            return EnumChatFormatting.GOLD.toString() + classpoints;
        } else if (classpoints < 13000) {
            return EnumChatFormatting.DARK_PURPLE.toString() + classpoints;
        } else if (classpoints < 19000) {
            return EnumChatFormatting.DARK_BLUE.toString() + classpoints;
        } else if (classpoints < 28000) {
            return EnumChatFormatting.DARK_AQUA.toString() + classpoints;
        } else if (classpoints < 40000) {
            return EnumChatFormatting.DARK_GREEN.toString() + classpoints;
        } else {
            return EnumChatFormatting.DARK_RED.toString() + classpoints;
        }
    }

    public IChatComponent getFormattedMessage(String formattedname, String playername) {

        String[][] matrix1 = {
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

        String[][] matrix2 = {
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
                        EnumChatFormatting.AQUA + "Def Kills/game : " + EnumChatFormatting.GOLD + String.format("%.3f", def_kill_game)
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
