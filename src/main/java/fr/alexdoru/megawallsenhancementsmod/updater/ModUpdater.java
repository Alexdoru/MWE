package fr.alexdoru.megawallsenhancementsmod.updater;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.Multithreading;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Util;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ModUpdater {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Logger updateLogger = LogManager.getLogger("Updater MegaWallsEnhancements");
    private static boolean hasTriggered = false;
    public static boolean isUpTodate = false;

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

    /**
     * https://github.com/DeDiamondPro/Auto-Updater
     */
    public static void checkForUpdate() throws ApiException, IOException {

        String GITHUB_API_URL = "https://api.github.com/repos/Alexdoru/MegaWallsEnhancements/releases";
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
        String browser_download_url = null;

        for (JsonElement jsonElement : element.getAsJsonArray()) {
            JsonObject release = jsonElement.getAsJsonObject();
            String tag_name = JsonUtil.getString(release, "tag_name");
            if (tag_name != null && release.has("assets")) {
                int releaseVersion = Integer.parseInt(tag_name.replace(".", ""));
                if (releaseVersion > latestVersion) {
                    latestVersion = releaseVersion;
                    version = tag_name;
                    final JsonElement assets = release.get("assets");
                    if (assets != null && assets.isJsonArray()) {
                        final JsonArray assetsJsonArray = assets.getAsJsonArray();
                        for (JsonElement assetsElement : assetsJsonArray) {
                            if (assetsElement != null && assetsElement.isJsonObject()) {
                                final JsonObject assetsJsonObject = assetsElement.getAsJsonObject();
                                browser_download_url = JsonUtil.getString(assetsJsonObject, "browser_download_url");
                            }
                        }
                    }
                }
            }
        }

        if (Integer.parseInt(MegaWallsEnhancementsMod.version.replace(".", "")) < latestVersion) {

            String GITHUB_URL = "https://github.com/Alexdoru/MegaWallsEnhancements/releases";
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Mega Walls Enhancements "
                    + EnumChatFormatting.GOLD + "version v" + version + EnumChatFormatting.GREEN + " is available, click this message to see the changelog and download page.")
                    .setChatStyle(new ChatStyle()
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, GITHUB_URL))
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + GITHUB_URL)))));

            if (ConfigHandler.automaticUpdate && browser_download_url != null && browser_download_url.endsWith(".jar")) {

                if (Loader.isModLoaded("feather")) {
                    ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + "The automatic updater is disabled on Feather."));
                    return;
                }

                File cacheDir = new File("config/updatecache");
                if (!cacheDir.exists() && !cacheDir.mkdir()) {
                    throw new IllegalStateException("Could not create cache folder");
                }

                String newModFileName = getFileName(browser_download_url);
                File modCacheFile = new File(cacheDir, newModFileName);
                downloadFileTo(browser_download_url, modCacheFile);
                updateLogger.info("Downloaded MWEnhancement Update");

                String GITHUB_DELETER_URL = "https://github.com/W-OVERFLOW/Deleter/releases/download/v1.8/Deleter-1.8.jar";
                String deleterFileName = getFileName(GITHUB_DELETER_URL);
                File deleterFile = new File(cacheDir, deleterFileName);
                downloadFileTo(GITHUB_DELETER_URL, deleterFile);
                updateLogger.info("Downloaded Mod Deleter");

                if (modCacheFile.exists() && deleterFile.exists()) {

                    ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Mega Walls Enhancements " + EnumChatFormatting.GOLD + "version v" + version + EnumChatFormatting.GREEN + " has been downloaded and will be installed automatically when closing your game."));

                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        try {
                            final File oldJarFile = MegaWallsEnhancementsMod.jarFile;
                            final File newJarFile = new File(oldJarFile.getParent(), newModFileName);
                            if (newJarFile.createNewFile() && modCacheFile.exists() && oldJarFile.exists()) {
                                try (InputStream source = new FileInputStream(modCacheFile); OutputStream dest = new FileOutputStream(newJarFile)) {
                                    byte[] buffer = new byte[1024];
                                    int length;
                                    while ((length = source.read(buffer)) > 0) {
                                        dest.write(buffer, 0, length);
                                    }
                                }
                                modCacheFile.delete();
                                deleteOldJar(oldJarFile.getAbsolutePath(), deleterFile);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }));

                }

            }

        } else {

            isUpTodate = true;

        }

    }

    private static void downloadFileTo(String browser_download_url, File cacheFile) throws IOException {
        URLConnection connection = new URL(browser_download_url).openConnection();
        connection.setRequestProperty("User-Agent", "Updater");
        InputStream inputStream = connection.getInputStream();
        Files.copy(inputStream, cacheFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        inputStream.close();
    }

    private static String getFileName(String downloadUrl) {
        return downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
    }

    private static void deleteOldJar(String absolutePathToDelete, File deleter) throws IOException {
        if (Util.getOSType() == Util.EnumOS.LINUX) {
            Runtime.getRuntime().exec("chmod +x \"" + deleter.getAbsolutePath() + "\"");
        } else if (Util.getOSType() == Util.EnumOS.OSX) {
            Runtime.getRuntime().exec("chmod 755 \"" + deleter.getAbsolutePath() + "\"");
        }
        Runtime.getRuntime().exec(
                "java -jar " + deleter.getName() + " \"" + absolutePathToDelete + "\"",
                null,
                deleter.getParentFile()
        );
    }

}
