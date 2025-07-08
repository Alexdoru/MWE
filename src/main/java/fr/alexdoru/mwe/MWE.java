package fr.alexdoru.mwe;

import fr.alexdoru.mwe.asm.hooks.RenderPlayerHook_RenegadeArrowCount;
import fr.alexdoru.mwe.chat.ChatListener;
import fr.alexdoru.mwe.commands.CommandAddAlias;
import fr.alexdoru.mwe.commands.CommandFKCounter;
import fr.alexdoru.mwe.commands.CommandHypixelShout;
import fr.alexdoru.mwe.commands.CommandMWE;
import fr.alexdoru.mwe.commands.CommandName;
import fr.alexdoru.mwe.commands.CommandNocheaters;
import fr.alexdoru.mwe.commands.CommandPlancke;
import fr.alexdoru.mwe.commands.CommandReport;
import fr.alexdoru.mwe.commands.CommandScanGame;
import fr.alexdoru.mwe.commands.CommandSquad;
import fr.alexdoru.mwe.commands.CommandStalk;
import fr.alexdoru.mwe.commands.CommandUnWDR;
import fr.alexdoru.mwe.commands.CommandWDR;
import fr.alexdoru.mwe.config.MWEConfig;
import fr.alexdoru.mwe.events.KeybindingListener;
import fr.alexdoru.mwe.features.FinalKillCounter;
import fr.alexdoru.mwe.features.LowHPIndicator;
import fr.alexdoru.mwe.features.MegaWallsEndGameStats;
import fr.alexdoru.mwe.features.SquadHandler;
import fr.alexdoru.mwe.gui.guiapi.GuiManager;
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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(
        modid = MWE.modid,
        name = MWE.modName,
        version = MWE.version,
        acceptedMinecraftVersions = "[1.8.9]",
        clientSideOnly = true)
public class MWE {

    public static final String modid = "mwenhancements";
    public static final String modName = "MWE";
    public static final String version = "4.0";
    public static final Logger logger = LogManager.getLogger(modName);
    public static File jarFile;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MWEConfig.loadConfig(new File(event.getModConfigurationDirectory(), "mwe.cfg"));
        jarFile = event.getSourceFile();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(new WdrData());
        MinecraftForge.EVENT_BUS.register(new GuiManager());
        MinecraftForge.EVENT_BUS.register(new ModUpdater());
        MinecraftForge.EVENT_BUS.register(new ReportQueue());
        MinecraftForge.EVENT_BUS.register(new ChatListener());
        MinecraftForge.EVENT_BUS.register(new SquadHandler());
        MinecraftForge.EVENT_BUS.register(new HackerDetector());
        MinecraftForge.EVENT_BUS.register(new LowHPIndicator());
        MinecraftForge.EVENT_BUS.register(new FinalKillCounter());
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

    }

}