package fr.alexdoru.mwe.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.alexdoru.mwe.MWE;
import fr.alexdoru.mwe.http.HttpClient;
import fr.alexdoru.mwe.http.exceptions.ApiException;
import fr.alexdoru.mwe.utils.JsonUtil;
import fr.alexdoru.mwe.utils.MultithreadingUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ModUpdater {

    private static final AtomicBoolean unpackedDeleter = new AtomicBoolean(false);
    private static final AtomicBoolean registeredShutdownHook = new AtomicBoolean(false);
    private static final List<PendingInstall> pendingInstallations = new ArrayList<>();

    protected final Logger LOGGER;
    protected final File jarFile;
    protected final String currentVersion;
    protected final boolean isFeatherClient;
    protected final boolean automaticUpdate;
    protected volatile ArtifactInfo updateInfo;
    protected volatile boolean downloadSuccess;
    protected volatile boolean finishedRunning;

    /**
     * A mod update checker that will notify the user about updates for your mod and optionally download and install the update.
     *
     * @param modJarFile  - the mod jar to update, usually provided by {@link FMLPreInitializationEvent#getSourceFile()}
     * @param name        - the name to use for the logger
     * @param version     - the current version of your mod
     * @param autoInstall - set to true to automatically install the update
     */
    public ModUpdater(File modJarFile, String name, String version, boolean autoInstall) {
        this.LOGGER = LogManager.getLogger(name + " Updater");
        this.jarFile = modJarFile;
        this.currentVersion = version;
        this.isFeatherClient = Loader.isModLoaded("feather");
        this.automaticUpdate = autoInstall;
    }

    /**
     * Starts the update checking processing
     */
    public final void start() {
        MinecraftForge.EVENT_BUS.register(this);
        MultithreadingUtil.queueIOTask(() -> {
            try {
                this.updateInfo = this.checkForUpdate();
                if (this.updateInfo != null && !this.isFeatherClient && this.automaticUpdate) {
                    this.startUpdateInstallation(this.updateInfo);
                }
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
                this.printChatNotification();
            }
        }
    }

    protected abstract void printChatNotification();

    protected abstract String getApiEndpoint();

    private ArtifactInfo checkForUpdate() throws ApiException {
        LOGGER.info("Checking for updates...");
        final ArtifactInfo artifactInfo = this.parseArtifactInfoFromApi(HttpClient.getAsJsonObject(this.getApiEndpoint()));
        if (artifactInfo == null) {
            return null;
        }
        final boolean isUpToDate = new ComparableVersion(this.currentVersion).compareTo(artifactInfo.version) >= 0;
        if (isUpToDate) {
            LOGGER.info("The mod is up to date!");
            return null;
        }
        LOGGER.info("New version available {}", artifactInfo.version);
        return artifactInfo;
    }

    private void startUpdateInstallation(ArtifactInfo artifactInfo) throws IOException {
        final File cacheDir = MWE.INSTANCE().getConfigFolder();
        if (!cacheDir.exists() && !cacheDir.mkdir()) {
            throw new IllegalStateException("Could not create cache folder");
        }

        final File newJarCacheFile = new File(cacheDir, artifactInfo.name);
        downloadFileTo(artifactInfo.url, newJarCacheFile, artifactInfo.digest);
        LOGGER.info("Downloaded {}", artifactInfo.name);

        final File deleterFile = new File(cacheDir, "Deleter.jar");
        if (!unpackedDeleter.get()) {
            try (InputStream bundledDeleter = ModUpdater.class.getResourceAsStream("/jarjar/Deleter.jar")) {
                if (bundledDeleter == null) {
                    throw new IllegalStateException("Could not find bundled Deleter.jar in mod resources");
                }
                Files.copy(bundledDeleter, deleterFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                unpackedDeleter.set(true);
                LOGGER.info("Unpacked Mod Deleter");
            }
        }

        if (newJarCacheFile.exists() && deleterFile.exists()) {
            this.downloadSuccess = true;
            if (!registeredShutdownHook.get()) {
                registeredShutdownHook.set(true);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> this.performModInstallations(deleterFile), "Update installer Thread"));
            }
            synchronized (pendingInstallations) {
                pendingInstallations.add(new PendingInstall(this.jarFile, newJarCacheFile, artifactInfo.name));
            }
        }
    }

    private void performModInstallations(File deleterFile) {
        final List<String> deleterArgs = new ArrayList<>();
        deleterArgs.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
        deleterArgs.add("-jar");
        deleterArgs.add(deleterFile.getName());

        synchronized (pendingInstallations) {
            for (final PendingInstall install : pendingInstallations) {
                try {
                    if (install.newJarCacheFile.exists() && install.oldJarFile.exists() && deleterFile.exists()) {
                        final File newJarFile = new File(install.oldJarFile.getParent(), install.artifactName);
                        Files.copy(
                                install.newJarCacheFile.toPath(),
                                newJarFile.toPath(),
                                StandardCopyOption.REPLACE_EXISTING
                        );
                        if (!install.newJarCacheFile.delete()) {
                            LOGGER.error("Failed to delete temp file {}", install.newJarCacheFile);
                        }
                        deleterArgs.add(install.oldJarFile.getAbsolutePath());
                    }
                } catch (IOException e) {
                    LOGGER.error("Failed to stage install for {}", install.artifactName, e);
                }
            }
        }

        if (deleterArgs.size() > 3) {
            try {
                new ProcessBuilder(deleterArgs)
                        .directory(deleterFile.getParentFile())
                        .inheritIO()
                        .start();
            } catch (IOException e) {
                LOGGER.error("Failed to start shared deleter process", e);
            }
        }
    }

    protected ArtifactInfo parseArtifactInfoFromApi(JsonObject apiResponse) {

        final String tag = JsonUtil.getString(apiResponse, "tag_name");

        if (tag == null) {
            LOGGER.error("Latest release doesn't have a tag");
            return null;
        }

        if (!apiResponse.has("assets")) {
            LOGGER.error("Latest release doesn't have assets");
            return null;
        }

        final JsonElement assets = apiResponse.get("assets");
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

    protected void downloadFileTo(@NotNull String url, @NotNull File cacheFile, @Nullable String digest) throws IOException {
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

    protected static class ArtifactInfo {

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

    private static class PendingInstall {

        public final File oldJarFile;
        public final File newJarCacheFile;
        public final String artifactName;

        private PendingInstall(File oldJarFile, File newJarCacheFile, String artifactName) {
            this.oldJarFile = oldJarFile;
            this.newJarCacheFile = newJarCacheFile;
            this.artifactName = artifactName;
        }

    }

}
