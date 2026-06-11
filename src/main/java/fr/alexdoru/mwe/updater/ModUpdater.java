package fr.alexdoru.mwe.updater;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.http.HttpClient;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.utils.JsonUtil;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ModUpdater {

    private final Logger LOGGER = LogManager.getLogger("MWE Updater");
    private final File jarFile;
    private final boolean isFeatherClient = Loader.isModLoaded("feather");
    private final List<IChatComponent> pendingMessages = new ArrayList<>();
    private volatile boolean finishedRunning = false;

    public ModUpdater(File file) {
        this.jarFile = file;
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                checkForUpdate();
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                finishedRunning = true;
            }
            return null;
        });
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (finishedRunning) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null && mc.thePlayer != null) {
                pendingMessages.forEach(ChatUtil::addChatMessage);
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }

    // https://github.com/DeDiamondPro/Auto-Updater
    private void checkForUpdate() throws ApiException, IOException {

        LOGGER.info("Checking for updates...");

        final String GITHUB_MWE_RELEASE_API = "https://api.github.com/repos/Alexdoru/MWE/releases";
        final String GITHUB_MWE_RELEASE = "https://github.com/Alexdoru/MWE/releases";

        final JsonArray jsonArray = HttpClient.getAsJsonArray(GITHUB_MWE_RELEASE_API);

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

        if (latestVersion == null) {
            return;
        }

        final ComparableVersion currentVersion = new ComparableVersion(MWE.version);

        if (currentVersion.compareTo(latestVersion) >= 0) {
            LOGGER.info("The mod is up to date!");
            return;
        }

        this.pendingMessages.add(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Mega Walls Enhancements "
                + EnumChatFormatting.GOLD + "version v" + latestVersion + EnumChatFormatting.GREEN + " is available, click this message to see the changelog and download page.")
                .setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, GITHUB_MWE_RELEASE))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + GITHUB_MWE_RELEASE)))));

        if (!MWEConfig.automaticUpdate || downloadUrl == null || !downloadUrl.endsWith(".jar")) return;

        if (isFeatherClient) {
            this.pendingMessages.add(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + "The automatic updater is disabled on Feather."));
            return;
        }

        final File cacheDir = new File("config/updatecache");
        if (!cacheDir.exists() && !cacheDir.mkdir()) {
            throw new IllegalStateException("Could not create cache folder");
        }

        final String newModFileName = getFileName(downloadUrl);
        final File modCacheFile = new File(cacheDir, newModFileName);
        downloadFileTo(downloadUrl, modCacheFile);
        LOGGER.info("Downloaded {}", newModFileName);

        final String GITHUB_DELETER_URL = "https://github.com/Alexdoru/Deleter/releases/download/1.0/Deleter.jar";
        final String deleterFileName = getFileName(GITHUB_DELETER_URL);
        final File deleterFile = new File(cacheDir, deleterFileName);
        downloadFileTo(GITHUB_DELETER_URL, deleterFile);
        LOGGER.info("Downloaded Mod Deleter");

        if (modCacheFile.exists() && deleterFile.exists()) {

            this.pendingMessages.add(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Mega Walls Enhancements "
                    + EnumChatFormatting.GOLD + "version v" + latestVersion
                    + EnumChatFormatting.GREEN + " has been downloaded and will be installed to your mods folder automatically when closing your game."));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    final File oldJarFile = this.jarFile;
                    final File newJarFile = new File(oldJarFile.getParent(), newModFileName);
                    if (modCacheFile.exists() && oldJarFile.exists()) {
                        Files.copy(
                                modCacheFile.toPath(),
                                newJarFile.toPath(),
                                StandardCopyOption.REPLACE_EXISTING
                        );
                        if (!modCacheFile.delete()) {
                            LOGGER.error("Failed to delete temp file {}", modCacheFile);
                        }
                        final String javaExe = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
                        new ProcessBuilder(javaExe, "-jar", deleterFile.getName(), oldJarFile.getAbsolutePath())
                                .directory(deleterFile.getParentFile())
                                .inheritIO()
                                .start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, "MWE Updater Thread"));

        }

    }

    private static void downloadFileTo(String url, File cacheFile) throws IOException {
        final URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Updater");
        try (InputStream inputStream = connection.getInputStream()) {
            Files.copy(
                    inputStream,
                    cacheFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private static String getFileName(String downloadUrl) {
        return downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
    }

}
