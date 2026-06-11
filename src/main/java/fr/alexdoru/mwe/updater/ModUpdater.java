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
import org.jetbrains.annotations.NotNull;

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

    private void checkForUpdate() throws ApiException, IOException {

        LOGGER.info("Checking for updates...");

        final String GITHUB_MWE_RELEASE_API = "https://api.github.com/repos/Alexdoru/MWE/releases";
        final String GITHUB_MWE_RELEASE = "https://github.com/Alexdoru/MWE/releases";

        final JsonArray jsonArray = HttpClient.getAsJsonArray(GITHUB_MWE_RELEASE_API);
        final ArtifactInfo artifactInfo = findLatestVersion(jsonArray);

        if (artifactInfo == null) {
            return;
        }

        final ComparableVersion currentVersion = new ComparableVersion(MWE.version);

        if (currentVersion.compareTo(artifactInfo.version) >= 0) {
            LOGGER.info("The mod is up to date!");
            return;
        }

        this.pendingMessages.add(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Mega Walls Enhancements "
                + EnumChatFormatting.GOLD + "version v" + artifactInfo.version + EnumChatFormatting.GREEN + " is available, click this message to see the changelog and download page.")
                .setChatStyle(new ChatStyle()
                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, GITHUB_MWE_RELEASE))
                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + GITHUB_MWE_RELEASE)))));

        if (!MWEConfig.automaticUpdate) return;

        if (isFeatherClient) {
            this.pendingMessages.add(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + "The automatic updater is disabled on Feather."));
            return;
        }

        final File cacheDir = new File("config/updatecache");
        if (!cacheDir.exists() && !cacheDir.mkdir()) {
            throw new IllegalStateException("Could not create cache folder");
        }

        final File modCacheFile = new File(cacheDir, artifactInfo.name);
        downloadFileTo(artifactInfo.url, modCacheFile);
        LOGGER.info("Downloaded {}", artifactInfo.name);

        final String GITHUB_DELETER_URL = "https://github.com/Alexdoru/Deleter/releases/download/1.0/Deleter.jar";
        final File deleterFile = new File(cacheDir, "Deleter.jar");
        downloadFileTo(GITHUB_DELETER_URL, deleterFile);
        LOGGER.info("Downloaded Mod Deleter");

        if (modCacheFile.exists() && deleterFile.exists()) {

            this.pendingMessages.add(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Mega Walls Enhancements "
                    + EnumChatFormatting.GOLD + "version v" + artifactInfo.version
                    + EnumChatFormatting.GREEN + " has been downloaded and will be installed to your mods folder automatically when closing your game."));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    final File oldJarFile = this.jarFile;
                    final File newJarFile = new File(oldJarFile.getParent(), artifactInfo.name);
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

    private ArtifactInfo findLatestVersion(JsonArray jsonArray) {

        JsonObject maxRelease = null;
        ComparableVersion maxVersion = null;

        for (final JsonElement jsonElement : jsonArray) {
            final JsonObject release = jsonElement.getAsJsonObject();
            final String tag = JsonUtil.getString(release, "tag_name");
            if (tag != null) {
                final ComparableVersion releaseVersion = new ComparableVersion(tag);
                if (maxVersion == null || releaseVersion.compareTo(maxVersion) > 0) {
                    maxRelease = release;
                    maxVersion = releaseVersion;
                }
            }
        }

        if (maxVersion == null) {
            LOGGER.error("Could not find latest release");
            return null;
        }

        if (!maxRelease.has("assets")) {
            LOGGER.error("Latest release doesn't have assests");
            return null;
        }

        final JsonElement assets = maxRelease.get("assets");
        if (assets != null && assets.isJsonArray()) {
            for (final JsonElement asset : assets.getAsJsonArray()) {
                if (asset != null && asset.isJsonObject()) {
                    final JsonObject assetsJsonObject = asset.getAsJsonObject();
                    final String fileName = JsonUtil.getString(assetsJsonObject, "name");
                    if (fileName != null && fileName.endsWith(".jar") && !fileName.contains("-api") && !fileName.contains("-dev") && !fileName.contains("-sources")) {
                        final String downloadUrl = JsonUtil.getString(assetsJsonObject, "browser_download_url");
                        if (downloadUrl == null) {
                            LOGGER.error(".jar artifact doesn't have download url");
                            return null;
                        }
                        return new ArtifactInfo(fileName, downloadUrl, maxVersion);
                    }
                }
            }
        }
        LOGGER.error("Could not find .jar file in release {} assets", maxVersion);
        return null;
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

    private static class ArtifactInfo {

        @NotNull
        public final String name;
        @NotNull
        public final String url;
        @NotNull
        public final ComparableVersion version;

        private ArtifactInfo(@NotNull String name, @NotNull String url, @NotNull ComparableVersion version) {
            this.name = name;
            this.url = url;
            this.version = version;
        }
    }
}
