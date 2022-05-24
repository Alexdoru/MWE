package fr.alexdoru.megawallsenhancementsmod.updater;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.Multithreading;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ModUpdater {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/Alexdoru/MegaWallsEnhancements/releases";
    private static final String GITHUB_URL = "https://github.com/Alexdoru/MegaWallsEnhancements/releases";
    public static boolean isUpTodate = false;
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean hasTriggered = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld != null && mc.thePlayer != null && !hasTriggered) {
            hasTriggered = true;
            Multithreading.addTaskToQueue(() -> {
                try {
                    checkForUpdate();
                } catch (ApiException e) {
                    e.printStackTrace();
                }
                return null;
            });
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    public static void checkForUpdate() throws ApiException {

        HttpClient httpClient = new HttpClient(GITHUB_API_URL);
        String rawresponse = httpClient.getrawresponse();
        if (rawresponse == null) {
            throw new ApiException("No response from github's Api");
        }
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(rawresponse);
        if (element == null) {
            throw new ApiException("Cannot parse response from github's Api");
        }

        if (!element.isJsonArray()) {
            throw new ApiException("Failed to parse response from github's Api, it is not a Json Array");
        }

        int latestVersion = 0;
        String version = "";

        for (JsonElement jsonElement : element.getAsJsonArray()) {
            JsonObject release = jsonElement.getAsJsonObject();
            String tag_name = JsonUtil.getString(release, "tag_name");
            if (tag_name != null && release.has("assets")) {
                int releaseVersion = Integer.parseInt(tag_name.replace(".", ""));
                if (releaseVersion > latestVersion) {
                    latestVersion = releaseVersion;
                    version = tag_name;
                }
            }
        }

        if (Integer.parseInt(MegaWallsEnhancementsMod.version.replace(".", "")) < latestVersion) {
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Mega Walls Enhancements "
                    + EnumChatFormatting.GOLD + "version v" + version + EnumChatFormatting.GREEN + " is available, click this message to see the changelog and download page.")
                    .setChatStyle(new ChatStyle()
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, GITHUB_URL))
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + GITHUB_URL)))));
        } else {
            isUpTodate = true;
        }

    }

}
