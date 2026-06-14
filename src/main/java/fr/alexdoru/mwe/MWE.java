package fr.alexdoru.mwe;

import fr.alexdoru.configlib.ConfigHandler;
import fr.alexdoru.configlib.IConfigHandler;
import fr.alexdoru.mwe.api.IMWEAddon;
import fr.alexdoru.mwe.asm.MWELoadingPlugin;
import fr.alexdoru.mwe.asm.hooks.RenderPlayerHook_RenegadeArrowCount;
import fr.alexdoru.mwe.chat.ChatListener;
import fr.alexdoru.mwe.commands.*;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.config.MWEConfigTitle;
import fr.alexdoru.mwe.events.KeybindingListener;
import fr.alexdoru.mwe.features.*;
import fr.alexdoru.mwe.gui.MWERendererManager;
import fr.alexdoru.mwe.hackerdetector.HackerDetector;
import fr.alexdoru.mwe.nocheaters.PlayerJoinListener;
import fr.alexdoru.mwe.nocheaters.ReportQueue;
import fr.alexdoru.mwe.nocheaters.WdrData;
import fr.alexdoru.mwe.scoreboard.ScoreboardTracker;
import fr.alexdoru.mwe.updater.ModUpdater;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mod(
        modid = MWE.modid,
        name = MWE.modName,
        version = BuildConfig.VERSION,
        dependencies = "required-after:Forge@["
                + net.minecraftforge.common.ForgeVersion.majorVersion + '.'
                + net.minecraftforge.common.ForgeVersion.minorVersion + '.'
                + net.minecraftforge.common.ForgeVersion.revisionVersion + '.'
                + net.minecraftforge.common.ForgeVersion.buildVersion + ",);",
        acceptedMinecraftVersions = "[1.8.9]",
        clientSideOnly = true)
public class MWE {

    public static final String modid = "mwenhancements";
    public static final String modName = "MWE";
    public static final String version = BuildConfig.VERSION;
    public static final Logger logger = LogManager.getLogger(modName);
    private static MWE INSTANCE;
    private final List<IMWEAddon> loadedAddons = new ArrayList<>();
    private IConfigHandler configHandler;
    private MWERendererManager rendererManager;
    private FinalKillCounterManager fkManager;

    public MWE() {
        INSTANCE = this;
        MWELoadingPlugin.loadClasses("mwe.addons", IMWEAddon.class).forEach(addon -> {
            final ComparableVersion MWEVersion = new ComparableVersion(version);
            final ComparableVersion requestedVersion = new ComparableVersion(addon.targetVersion());
            if (requestedVersion.compareTo(MWEVersion) > 0) {
                logger.fatal("Addon {} requested version {}, but MWE version is {}", addon.name(), addon.targetVersion(), version);
                throw new IllegalStateException("Invalid MWE version");
            }
            this.loadedAddons.add(addon);
            logger.info("Successfully loaded addon {}", addon.name());
        });
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.configHandler = new ConfigHandler(new File(event.getModConfigurationDirectory(), "mwe.cfg"), MWE.version);
        this.configHandler.setConfigTitleRenderer(new MWEConfigTitle());
        this.rendererManager = new MWERendererManager();
        this.configHandler.setRendererManager(this.rendererManager);
        this.configHandler.registerConfig(MWEConfig.class);
        if (MWEConfig.checkForUpdate && !Boolean.getBoolean("mwe.disableUpdater")) {
            MinecraftForge.EVENT_BUS.register(new ModUpdater(event.getSourceFile()));
        }
        this.loadedAddons.forEach(a -> a.preInit(event));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        this.fkManager = new FinalKillCounterManager();
        MinecraftForge.EVENT_BUS.register(this.fkManager);

        MinecraftForge.EVENT_BUS.register(new WdrData());
        this.rendererManager.loadRenderers();
        MinecraftForge.EVENT_BUS.register(this.rendererManager);
        MinecraftForge.EVENT_BUS.register(new ReportQueue());
        MinecraftForge.EVENT_BUS.register(new ChatListener());
        MinecraftForge.EVENT_BUS.register(new SquadHandler());
        MinecraftForge.EVENT_BUS.register(new HackerDetector());
        MinecraftForge.EVENT_BUS.register(new LowHPIndicator());
        MinecraftForge.EVENT_BUS.register(new StrengthParticles());
        MinecraftForge.EVENT_BUS.register(new ScoreboardTracker());
        MinecraftForge.EVENT_BUS.register(new PlayerJoinListener());
        MinecraftForge.EVENT_BUS.register(new KeybindingListener());
        MinecraftForge.EVENT_BUS.register(new MegaWallsEndGameStats());
        MinecraftForge.EVENT_BUS.register(new RenderPlayerHook_RenegadeArrowCount());

        ClientCommandHandler.instance.registerCommand(new CommandMWE());
        ClientCommandHandler.instance.registerCommand(new CommandWDR());
        ClientCommandHandler.instance.registerCommand(new CommandName());
        ClientCommandHandler.instance.registerCommand(new CommandUnWDR());
        ClientCommandHandler.instance.registerCommand(new CommandSquad());
        ClientCommandHandler.instance.registerCommand(new CommandStalk());
        ClientCommandHandler.instance.registerCommand(new CommandReport());
        ClientCommandHandler.instance.registerCommand(new CommandPlancke());
        ClientCommandHandler.instance.registerCommand(new CommandAddAlias());
        ClientCommandHandler.instance.registerCommand(new CommandScanGame());
        ClientCommandHandler.instance.registerCommand(new CommandFKCounter());
        ClientCommandHandler.instance.registerCommand(new CommandNocheaters());
        ClientCommandHandler.instance.registerCommand(new CommandHypixelShout());

        this.loadedAddons.forEach(a -> a.init(event));

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        this.loadedAddons.forEach(a -> a.postInit(event));
    }

    public static MWE INSTANCE() {
        return INSTANCE;
    }

    public IConfigHandler getConfigHandler() {
        return configHandler;
    }

    public MWERendererManager getRendererManager() {
        return rendererManager;
    }

    @Nullable
    public FinalKillCounter getFinalKillCounter() {
        return this.fkManager.getFkCounter();
    }

}