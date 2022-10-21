package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.apikey.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import net.minecraft.util.EnumChatFormatting;

public class HypixelGuild {

    private String guildName;
    private String formattedGuildTag;

    public HypixelGuild(String uuid) throws ApiException {

        final HttpClient httpClient = new HttpClient("https://api.hypixel.net/guild?key=" + HypixelApiKeyUtil.getApiKey() + "&player=" + uuid);
        final JsonObject obj = httpClient.getJsonResponse();

        final JsonElement guildDataElem = obj.get("guild");
        if (guildDataElem == null || !guildDataElem.isJsonObject()) {
            return;
        }

        final JsonObject guildData = guildDataElem.getAsJsonObject();

        if (guildData == null) {
            return;
        }

        this.guildName = JsonUtil.getString(guildData, "name");
        final String tag = JsonUtil.getString(guildData, "tag");
        final EnumChatFormatting tagColor = EnumChatFormatting.getValueByName(JsonUtil.getString(guildData, "tagColor"));
        this.formattedGuildTag = " " + (tag == null ? "" : (tagColor == null ? "[" + tag + "]" : tagColor + "[" + tag + "]"));

    }

    public String getGuildName() {
        return guildName;
    }

    public String getFormattedGuildTag() {
        return formattedGuildTag;
    }

}
