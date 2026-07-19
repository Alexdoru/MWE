package fr.alexdoru.mwe.updater;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.chat.ChatUtil;
import fr.alexdoru.mwe.http.HttpClient;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.utils.JsonUtil;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static net.minecraft.util.EnumChatFormatting.*;

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

    public ModUpdater(File file, String name, String version, boolean autoInstall) {
        this.LOGGER = LogManager.getLogger(name + " Updater");
        this.jarFile = file;
        this.apiEndpoint = "https://api.github.com/repos/Alexdoru/MWE/releases/latest";
        this.releaseLink = "https://github.com/Alexdoru/MWE/releases";
        this.currentVersion = version;
        this.isFeatherClient = Loader.isModLoaded("feather");
        this.automaticUpdate = autoInstall;
        MultithreadingUtil.addTaskToQueue(() -> {
            try {
                checkForUpdate();
            } catch (Throwable t) {
                LOGGER.error("Caught exception while checking for update", t);
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
            ChatUtil.addChatMessage(DARK_GRAY + ChatUtil.bar());
            ChatUtil.addChatMessage(ChatUtil.centerLine(DARK_RED.toString() + BOLD + "    MWE " + GOLD + "v" + this.updateInfo.version + GREEN + " is available!"));
            ChatUtil.addChatMessage(new ChatComponentText(ChatUtil.centerLine(YELLOW + "    Click here to view the changelog & download page."))
                    .setChatStyle(new ChatStyle()
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, this.releaseLink))
                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(YELLOW + this.releaseLink)))));
            if (this.automaticUpdate) {
                ChatUtil.addChatMessage("");
                if (this.isFeatherClient) {
                    ChatUtil.addChatMessage(new ChatComponentText(RED + "✘ The automatic updater is disabled on Feather."));
                } else if (this.downloadSuccess) {
                    ChatUtil.addChatMessage(new ChatComponentText(GREEN + "✔ Update has been downloaded and will be installed automatically when closing the game."));
                }
            }
            ChatUtil.addChatMessage(DARK_GRAY + ChatUtil.bar());
        }
    }

    private void checkForUpdate() throws ApiException, IOException {

        LOGGER.info("Checking for updates...");

        final JsonObject jsonResponse = HttpClient.getAsJsonObject(this.apiEndpoint);
        final ArtifactInfo artifactInfo = parseArtifactInfo(jsonResponse);

        if (artifactInfo == null) {
            return;
        }

        final boolean isUpToDate = new ComparableVersion(this.currentVersion).compareTo(artifactInfo.version) >= 0;

        if (isUpToDate) {
            LOGGER.info("The mod is up to date!");
            return;
        }

        LOGGER.info("New version available {}", artifactInfo.version);

        this.updateInfo = artifactInfo;

        if (!this.automaticUpdate) return;

        if (isFeatherClient) {
            return;
        }

        final File cacheDir = MWE.INSTANCE().getConfigFolder();
        if (!cacheDir.exists() && !cacheDir.mkdir()) {
            throw new IllegalStateException("Could not create cache folder");
        }

        final File newModCacheFile = new File(cacheDir, artifactInfo.name);
        downloadFileTo(artifactInfo.url, newModCacheFile, artifactInfo.digest);
        LOGGER.info("Downloaded {}", artifactInfo.name);

        final File deleterFile = new File(cacheDir, "Deleter.jar");
        try (InputStream bundledDeleter = ModUpdater.class.getResourceAsStream("/jarjar/Deleter.jar")) {
            if (bundledDeleter == null) {
                throw new IllegalStateException("Could not find bundled Deleter.jar in mod resources");
            }
            Files.copy(bundledDeleter, deleterFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        LOGGER.info("Unpacked Mod Deleter");

        if (newModCacheFile.exists() && deleterFile.exists()) {

            this.downloadSuccess = true;

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    final File oldJarFile = this.jarFile;
                    final File newJarFile = new File(oldJarFile.getParent(), artifactInfo.name);
                    if (newModCacheFile.exists() && oldJarFile.exists()) {
                        Files.copy(
                                newModCacheFile.toPath(),
                                newJarFile.toPath(),
                                StandardCopyOption.REPLACE_EXISTING
                        );
                        if (!newModCacheFile.delete()) {
                            LOGGER.error("Failed to delete temp file {}", newModCacheFile);
                        }
                        final String javaExe = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
                        new ProcessBuilder(javaExe, "-jar", deleterFile.getName(), oldJarFile.getAbsolutePath())
                                .directory(deleterFile.getParentFile())
                                .inheritIO()
                                .start();
                    }
                } catch (IOException e) {
                    LOGGER.error("Failed to install update", e);
                }
            }, "Update installer Thread"));

        }

    }

    private ArtifactInfo parseArtifactInfo(JsonObject json) {

        final String tag = JsonUtil.getString(json, "tag_name");

        if (tag == null) {
            LOGGER.error("Latest release doesn't have a tag");
            return null;
        }

        if (!json.has("assets")) {
            LOGGER.error("Latest release doesn't have assets");
            return null;
        }

        final JsonElement assets = json.get("assets");
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
                        if (!downloadUrl.startsWith("https://github.com")) {
                            LOGGER.error("Download URL doesn't point to Github!");
                            return null;
                        }
                        final String digest = JsonUtil.getString(assetsJsonObject, "digest");
                        return new ArtifactInfo(fileName, downloadUrl, tag, digest);
                    }
                }
            }
        }
        LOGGER.error("Could not find .jar file to download in lates release");
        return null;
    }

    private void downloadFileTo(@NotNull String url, @NotNull File cacheFile, @Nullable String digest) throws IOException {
        final URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "MWE-Updater");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        final MessageDigest sha256;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed to be available on every standard JVM, this should never happen
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }

        try (InputStream inputStream = new DigestInputStream(connection.getInputStream(), sha256)) {
            Files.copy(
                    inputStream,
                    cacheFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            // cleanup partial file left behind by a mid-stream failure
            Files.deleteIfExists(cacheFile.toPath());
            throw e;
        }

        if (digest != null) {
            if (!digest.startsWith("sha256:")) {
                throw new IOException("Invalid digest signature from api");
            }
            final String computedDigest = "sha256:" + bytesToHex(sha256.digest());
            if (!computedDigest.equalsIgnoreCase(digest)) {
                Files.deleteIfExists(cacheFile.toPath());
                throw new IOException("Digest mismatch for " + cacheFile.getName() + " (expected " + digest + ", got " + computedDigest + ")");
            }
        } else {
            LOGGER.warn("No digest provided for {}, skipping integrity check", cacheFile.getName());
        }
    }

    private static String bytesToHex(byte[] bytes) {
        final StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (final byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static class ArtifactInfo {

        @NotNull
        public final String name;
        @NotNull
        public final String url;
        @NotNull
        public final ComparableVersion version;
        @Nullable
        public final String digest;

        private ArtifactInfo(@NotNull String name, @NotNull String url, @NotNull String tag, @Nullable String digest) {
            this.name = name;
            this.url = url;
            this.version = new ComparableVersion(tag);
            this.digest = digest;
        }

    }

}
