package fr.alexdoru.megawallsenhancementsmod.updater;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.megawallsenhancementsmod.MegaWallsEnhancementsMod;
import fr.alexdoru.megawallsenhancementsmod.api.HttpClient;
import fr.alexdoru.megawallsenhancementsmod.api.exceptions.ApiException;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatUtil;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.utils.JsonUtil;
import fr.alexdoru.megawallsenhancementsmod.utils.MultithreadingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Util;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ModUpdater {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static boolean hasTriggered = false;
    public static boolean isUpTodate = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld != null && mc.thePlayer != null && !hasTriggered) {
            hasTriggered = true;
            checkForgeVersion();
            MultithreadingUtil.addTaskToQueue(() -> {
                try {
                    checkForUpdate();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                return null;
            });
            MinecraftForge.EVENT_BUS.unregister(this);
        }
    }

    private static void checkForgeVersion() {
        if (!ForgeVersion.getVersion().contains("2318")) {
            final String forgeUrl = "https://files.minecraftforge.net/net/minecraftforge/forge/index_1.8.9.html";
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + "You are using an outdated version of forge, " +
                    "things might not work properly! Click this message to open the download page for Forge.")
                    .setChatStyle(new ChatStyle()
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, forgeUrl))
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + forgeUrl)))));
        }
    }

    // https://github.com/DeDiamondPro/Auto-Updater
    private static void checkForUpdate() throws ApiException, IOException {

        MegaWallsEnhancementsMod.logger.info("Checking for updates");

        final String GITHUB_API_URL = "https://api.github.com/repos/Alexdoru/MegaWallsEnhancements/releases";
        final HttpClient httpClient = new HttpClient(GITHUB_API_URL);
        final JsonArray jsonArray = httpClient.getJsonArray();

        String downloadUrl = null;
        ComparableVersion latestVersion = null;

        for (final JsonElement jsonElement : jsonArray) {
            final JsonObject release = jsonElement.getAsJsonObject();
            final String tag = JsonUtil.getString(release, "tag_name");
            if (tag != null && release.has("assets")) {
                final ComparableVersion releaseVersion = new ComparableVersion(tag);
                if (latestVersion == null || releaseVersion.compareTo(latestVersion) > 0) {
                    final JsonElement assets = release.get("assets");
                    if (assets != null && assets.isJsonArray()) {
                        final JsonArray assetsJsonArray = assets.getAsJsonArray();
                        for (final JsonElement assetsElement : assetsJsonArray) {
                            if (assetsElement != null && assetsElement.isJsonObject()) {
                                final JsonObject assetsJsonObject = assetsElement.getAsJsonObject();
                                downloadUrl = JsonUtil.getString(assetsJsonObject, "browser_download_url");
                                latestVersion = releaseVersion;
                            }
                        }
                    }
                }
            }
        }

        final ComparableVersion currentVersion = new ComparableVersion(MegaWallsEnhancementsMod.version);
        if (latestVersion == null) {
            return;
        }
        if (currentVersion.compareTo(latestVersion) >= 0) {
            MegaWallsEnhancementsMod.logger.info("The mod is up to date!");
            isUpTodate = true;
            return;
        }

        final String GITHUB_URL = "https://github.com/Alexdoru/MegaWallsEnhancements/releases";
        ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Mega Walls Enhancements "
                + EnumChatFormatting.GOLD + "version v" + latestVersion + EnumChatFormatting.GREEN + " is available, click this message to see the changelog and download page.")
                .setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, GITHUB_URL))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + GITHUB_URL)))));

        if (!ConfigHandler.automaticUpdate || downloadUrl == null || !downloadUrl.endsWith(".jar")) return;

        if (Loader.isModLoaded("feather")) {
            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.RED + "The automatic updater is disabled on Feather.");
            return;
        }

        final File cacheDir = new File("config/updatecache");
        if (!cacheDir.exists() && !cacheDir.mkdir()) {
            throw new IllegalStateException("Could not create cache folder");
        }

        final String newModFileName = getFileName(downloadUrl);
        final File modCacheFile = new File(cacheDir, newModFileName);
        downloadFileTo(downloadUrl, modCacheFile);
        MegaWallsEnhancementsMod.logger.info("Downloaded MWE Update");

        final String GITHUB_DELETER_URL = "https://github.com/Alexdoru/Deleter/releases/download/1.0/Deleter.jar";
        final String deleterFileName = getFileName(GITHUB_DELETER_URL);
        final File deleterFile = new File(cacheDir, deleterFileName);
        downloadFileTo(GITHUB_DELETER_URL, deleterFile);
        MegaWallsEnhancementsMod.logger.info("Downloaded Mod Deleter");

        if (modCacheFile.exists() && deleterFile.exists()) {

            ChatUtil.addChatMessage(ChatUtil.getTagMW() + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Mega Walls Enhancements "
                    + EnumChatFormatting.GOLD + "version v" + latestVersion
                    + EnumChatFormatting.GREEN + " has been downloaded and will be installed to your mods folder automatically when closing your game.");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    final File oldJarFile = MegaWallsEnhancementsMod.jarFile;
                    final File newJarFile = new File(oldJarFile.getParent(), newModFileName);
                    if (newJarFile.createNewFile() && modCacheFile.exists() && oldJarFile.exists()) {
                        try (final InputStream source = Files.newInputStream(modCacheFile.toPath()); final OutputStream dest = Files.newOutputStream(newJarFile.toPath())) {
                            final byte[] buffer = new byte[1024];
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

    private static void downloadFileTo(String browser_download_url, File cacheFile) throws IOException {
        final URLConnection connection = new URL(browser_download_url).openConnection();
        connection.setRequestProperty("User-Agent", "Updater");
        final InputStream inputStream = connection.getInputStream();
        Files.copy(inputStream, cacheFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        inputStream.close();
    }

    private static String getFileName(String downloadUrl) {
        return downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
    }

    private static void deleteOldJar(String absolutePathToDelete, File deleter) throws IOException {
        if (Util.getOSType() == Util.EnumOS.LINUX) {
            Runtime.getRuntime().exec(new String[]{"chmod", "+x", deleter.getAbsolutePath()});
        } else if (Util.getOSType() == Util.EnumOS.OSX) {
            Runtime.getRuntime().exec(new String[]{"chmod", "755", deleter.getAbsolutePath()});
        }
        Runtime.getRuntime().exec(
                new String[]{"java", "-jar", deleter.getName(), absolutePathToDelete},
                null,
                deleter.getParentFile()
        );
    }

}
