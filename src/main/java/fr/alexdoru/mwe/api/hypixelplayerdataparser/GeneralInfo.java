package fr.alexdoru.mwe.api.hypixelplayerdataparser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.mwe.asm.hooks.GuiScreenHook_CustomChatClickEvent;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.utils.DateUtil;
import fr.alexdoru.mwe.utils.HypixelLevelingUtil;
import fr.alexdoru.mwe.utils.JsonUtil;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Map;

public class GeneralInfo extends LoginData {

    private final long networkExp;
    private final int achievementPoints;
    private final int karma;
    private final String mcVersionRp;

    private int completedQuests;

    private String DISCORD;
    private String TWITCH;
    private String TWITTER;
    private String YOUTUBE;

    public GeneralInfo(JsonObject playerData) {
        super(playerData);

        this.networkExp = JsonUtil.getLong(playerData, "networkExp");
        this.achievementPoints = JsonUtil.getInt(playerData, "achievementPoints");
        this.karma = JsonUtil.getInt(playerData, "karma");
        this.mcVersionRp = JsonUtil.getString(playerData, "mcVersionRp");

        final JsonObject questsdata = JsonUtil.getJsonObject(playerData, "quests");

        if (questsdata != null) {
            for (final Map.Entry<String, JsonElement> entry : questsdata.entrySet()) {
                if (entry.getValue() instanceof JsonObject) {
                    final JsonArray completionsArray = JsonUtil.getJsonArray(entry.getValue().getAsJsonObject(), "completions");
                    if (completionsArray == null) {
                        continue;
                    }
                    for (final JsonElement ignored : completionsArray) {
                        this.completedQuests++;
                    }
                }
            }
        }

        final JsonObject socialMediaObj = JsonUtil.getJsonObject(playerData, "socialMedia");

        if (socialMediaObj != null) {

            final JsonObject linksObj = JsonUtil.getJsonObject(socialMediaObj, "links");

            if (linksObj != null) {
                this.DISCORD = JsonUtil.getString(linksObj, "DISCORD");
                this.TWITCH = JsonUtil.getString(linksObj, "TWITCH");
                this.TWITTER = JsonUtil.getString(linksObj, "TWITTER");
                this.YOUTUBE = JsonUtil.getString(linksObj, "YOUTUBE");
            }

        }

    }

    public void printMessage(String formattedname, String guildname) {

        final IChatComponent msg = new ChatComponentText(EnumChatFormatting.AQUA + ChatUtil.bar() + "\n")

                .appendSibling(ChatUtil.PlanckeHeaderText(formattedname, this.getdisplayname(), " - General info"))

                .appendText("\n" + "\n"

                        + (guildname == null ? "" : ChatUtil.centerLine(EnumChatFormatting.GREEN + "Guild : " + EnumChatFormatting.GOLD + guildname) + "\n")

                        + ChatUtil.centerLine(EnumChatFormatting.GREEN + "Network level : " + EnumChatFormatting.GOLD + String.format("%.2f", getNetworkLevel())) + "\n"

                        + ChatUtil.centerLine(EnumChatFormatting.GREEN + "Karma : " + EnumChatFormatting.LIGHT_PURPLE + ChatUtil.formatInt(this.karma)) + "\n"

                        + ChatUtil.centerLine(EnumChatFormatting.GREEN + "Achievement : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.achievementPoints) + " "
                        + EnumChatFormatting.GREEN + "Quests : " + EnumChatFormatting.GOLD + ChatUtil.formatInt(this.completedQuests)) + "\n"

                        + ChatUtil.centerLine(EnumChatFormatting.GREEN + "Firstlogin : " + EnumChatFormatting.GOLD + DateUtil.localFormatTimeInDay(this.getFirstLogin()) + " "

                        + (this.isStaffonHypixel() || this.isHidingFromAPI() ? "" : EnumChatFormatting.GREEN + "Status : " + (this.isOnline() ? EnumChatFormatting.DARK_GREEN + "Online" :
                        EnumChatFormatting.RED + "Offline " + EnumChatFormatting.DARK_GRAY + "for " + EnumChatFormatting.YELLOW + DateUtil.timeSince(this.getLastLogout())))) + "\n"

                        + (this.mcVersionRp == null ? "" : ChatUtil.centerLine(EnumChatFormatting.GREEN + "Minecraft version : " + EnumChatFormatting.GOLD + this.mcVersionRp) + "\n"));

        String str = "";

        str += (this.DISCORD != null ? "Discord " : "") + (this.TWITCH != null ? "Twitch " : "") + (this.TWITTER != null ? "Twitter " : "") + (this.YOUTUBE != null ? "Youtube" : "");

        msg.appendText(ChatUtil.getSeparatorToCenter(str));

        if (this.DISCORD != null) {

            msg.appendSibling(new ChatComponentText(EnumChatFormatting.BLUE + "Discord ")
                    .setChatStyle(new ChatStyle()
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to copy " + EnumChatFormatting.BLUE + this.DISCORD)))
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, GuiScreenHook_CustomChatClickEvent.COPY_TO_CLIPBOARD_COMMAND + this.DISCORD))
                    ));

        }

        if (this.TWITCH != null) {

            msg.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_PURPLE + "Twitch ")
                    .setChatStyle(new ChatStyle()
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + "Click to open Twitch in browser")))
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.TWITCH))
                    ));

        }

        if (this.TWITTER != null) {

            msg.appendSibling(new ChatComponentText(EnumChatFormatting.AQUA + "Twitter ")
                    .setChatStyle(new ChatStyle()
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.AQUA + "Click to open Twitter in browser")))
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.TWITTER))
                    ));

        }

        if (this.YOUTUBE != null) {

            msg.appendSibling(new ChatComponentText(EnumChatFormatting.RED + "Youtube")
                    .setChatStyle(new ChatStyle()
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.RED + "Click to open Youtube in browser")))
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.YOUTUBE))
                    ));

        }

        msg.appendText(EnumChatFormatting.AQUA + ChatUtil.bar());
        ChatUtil.addChatMessage(msg);

    }

    public float getNetworkLevel() {
        return (float) HypixelLevelingUtil.getExactLevel((double) networkExp);
    }

    public int getCompletedQuests() {
        return completedQuests;
    }

}
