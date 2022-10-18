package fr.alexdoru.megawallsenhancementsmod.api.requests;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import net.minecraft.util.EnumChatFormatting;

public class HypixelGuild {

    private String guildName;
    private String formattedGuildTag;

    public HypixelGuild(String uuid) throws ApiException {

        final HttpClient httpclient = new HttpClient("https://api.hypixel.net/guild?key=" + HypixelApiKeyUtil.getApiKey() + "&player=" + uuid);
        final String rawresponse = httpclient.getrawresponse();

        if (rawresponse == null) {
            throw new ApiException("No response from Hypixel's Api");
        }

        final JsonParser parser = new JsonParser();
        final JsonObject obj = parser.parse(rawresponse).getAsJsonObject();

        if (obj == null) {
            throw new ApiException("Cannot parse response from Hypixel's Api");
        }

        if (!obj.get("success").getAsBoolean()) {

            final String msg = JsonUtil.getString(obj, "cause");

            if (msg == null) {
                throw new ApiException("Failed to retreive data from Hypixel's Api for this guild");
            } else {
                throw new ApiException(msg);
            }

        }

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
