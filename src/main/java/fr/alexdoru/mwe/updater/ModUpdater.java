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

public class ModUpdater {

    private final Logger LOGGER;
    private final File jarFile;
    private final String apiEndpoint;
    private final String releaseLink;
    private final String currentVersion;
    private final boolean isFeatherClient;
    private final boolean automaticUpdate;
    private volatile ArtifactInfo updateInfo;
    private volatile boolean downloadSuccess;
    private volatile boolean finishedRunning;

    public ModUpdater(File file) {
        this.LOGGER = LogManager.getLogger("MWE Updater");
        this.jarFile = file;
        this.apiEndpoint = "https://api.github.com/repos/Alexdoru/MWE/releases";
        this.releaseLink = "https://github.com/Alexdoru/MWE/releases";
        this.currentVersion = MWE.version;
        this.isFeatherClient = Loader.isModLoaded("feather");
        this.automaticUpdate = MWEConfig.automaticUpdate;
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                checkForUpdate();
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                finishedRunning = true;
            }
        });
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (finishedRunning) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null && mc.thePlayer != null) {
                MinecraftForge.EVENT_BUS.unregister(this);
                this.printMessages();
            }
        }
    }

    private void printMessages() {
        if (this.updateInfo != null) {
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Mega Walls Enhancements "
                    + EnumChatFormatting.GOLD + "version v" + this.updateInfo.version + EnumChatFormatting.GREEN + " is available, click this message to see the changelog and download page.")
                    .setChatStyle(new ChatStyle()
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.releaseLink))
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW + this.releaseLink)))));
            if (this.automaticUpdate) {
                if (this.isFeatherClient) {
                    ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + "The automatic updater is disabled on Feather."));
                } else if (this.downloadSuccess) {
                    ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.getTagMW() + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "Mega Walls Enhancements "
                            + EnumChatFormatting.GOLD + "version v" + this.updateInfo.version
                            + EnumChatFormatting.GREEN + " has been downloaded and will be installed to your mods folder automatically when closing your game."));
                }
            }
        }
    }

    private void checkForUpdate() throws ApiException, IOException {

        LOGGER.info("Checking for updates...");

        final JsonArray jsonArray = HttpClient.getAsJsonArray(this.apiEndpoint);
        final ArtifactInfo artifactInfo = findLatestVersion(jsonArray);

        if (artifactInfo == null) {
            return;
        }

        final ComparableVersion currentVersion = new ComparableVersion(this.currentVersion);

        if (currentVersion.compareTo(artifactInfo.version) >= 0) {
            LOGGER.info("The mod is up to date!");
            return;
        }

        LOGGER.info("New version available {}", artifactInfo.version);

        this.updateInfo = artifactInfo;

        if (!this.automaticUpdate) return;

        if (isFeatherClient) {
            return;
        }

        final File cacheDir = new File("config/updatecache");
        if (!cacheDir.exists() && !cacheDir.mkdir()) {
            throw new IllegalStateException("Could not create cache folder");
        }

        final File modCacheFile = new File(cacheDir, artifactInfo.name);
        downloadFileTo(artifactInfo.url, modCacheFile);
        LOGGER.info("Downloaded {}", artifactInfo.name);

        final File deleterFile = new File(cacheDir, "Deleter.jar");
        try (InputStream bundledDeleter = ModUpdater.class.getResourceAsStream("/jarjar/Deleter.jar")) {
            if (bundledDeleter == null) {
                throw new IllegalStateException("Could not find bundled Deleter.jar in mod resources");
            }
            Files.copy(bundledDeleter, deleterFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        LOGGER.info("Unpacked Mod Deleter");

        if (modCacheFile.exists() && deleterFile.exists()) {

            this.downloadSuccess = true;

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
            }, "Update installer Thread"));

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
            LOGGER.error("Latest release doesn't have assets");
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
