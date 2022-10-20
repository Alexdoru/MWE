package fr.alexdoru.megawallsenhancementsmod;

import fr.alexdoru.megawallsenhancementsmod.asm.hooks.RenderPlayerHook;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatListener;
import fr.alexdoru.megawallsenhancementsmod.chat.ReportSuggestionHandler;
import fr.alexdoru.megawallsenhancementsmod.commands.*;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.data.WdredPlayers;
import fr.alexdoru.megawallsenhancementsmod.events.KeybindingListener;
import fr.alexdoru.megawallsenhancementsmod.features.LowHPIndicator;
import fr.alexdoru.megawallsenhancementsmod.features.MegaWallsEndGameStats;
import fr.alexdoru.megawallsenhancementsmod.features.SquadHandler;
import fr.alexdoru.megawallsenhancementsmod.fkcounter.KillCounter;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiManager;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.NoCheatersEvents;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.updater.ModUpdater;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(
        modid = MegaWallsEnhancementsMod.modid,
        name = MegaWallsEnhancementsMod.modName,
        version = MegaWallsEnhancementsMod.version,
        acceptedMinecraftVersions = "[1.8.9]",
        clientSideOnly = true
)
public class MegaWallsEnhancementsMod {

    public static final String modid = "mwenhancements";
    public static final String modName = "MegaWallsEnhancements";
    public static final String version = "2.6";
    public static final Logger logger = LogManager.getLogger(modName);
    public static final KeyBinding toggleDroppedItemLimit = new KeyBinding("Toggle dropped item limit", 0, "MegaWallsEnhancements");
    public static final KeyBinding newNickKey = new KeyBinding("New Random Nick", 0, "MegaWallsEnhancements");
    public static final KeyBinding addTimestampKey = new KeyBinding("Add Timestamp", 0, "NoCheaters");
    public static File configurationFile;
    public static File jarFile;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        configurationFile = event.getSuggestedConfigurationFile();
        ConfigHandler.preinit(configurationFile);
        jarFile = event.getSourceFile();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        ClientRegistry.registerKeyBinding(newNickKey);
        ClientRegistry.registerKeyBinding(addTimestampKey);
        ClientRegistry.registerKeyBinding(toggleDroppedItemLimit);

        MinecraftForge.EVENT_BUS.register(new GuiManager());
        MinecraftForge.EVENT_BUS.register(new ModUpdater());
        MinecraftForge.EVENT_BUS.register(new KillCounter());
        MinecraftForge.EVENT_BUS.register(new ChatListener());
        MinecraftForge.EVENT_BUS.register(new SquadHandler());
        MinecraftForge.EVENT_BUS.register(new LowHPIndicator());
        MinecraftForge.EVENT_BUS.register(new RenderPlayerHook());
        MinecraftForge.EVENT_BUS.register(new NoCheatersEvents());
        MinecraftForge.EVENT_BUS.register(new ScoreboardTracker());
        MinecraftForge.EVENT_BUS.register(new KeybindingListener());
        MinecraftForge.EVENT_BUS.register(new MegaWallsEndGameStats());
        MinecraftForge.EVENT_BUS.register(new ReportSuggestionHandler());

        ClientCommandHandler.instance.registerCommand(new CommandWDR());
        ClientCommandHandler.instance.registerCommand(new CommandName());
        ClientCommandHandler.instance.registerCommand(new CommandKill());
        ClientCommandHandler.instance.registerCommand(new CommandUnWDR());
        ClientCommandHandler.instance.registerCommand(new CommandSquad());
        ClientCommandHandler.instance.registerCommand(new CommandStalk());
        ClientCommandHandler.instance.registerCommand(new CommandReport());
        ClientCommandHandler.instance.registerCommand(new CommandPlancke());
        ClientCommandHandler.instance.registerCommand(new CommandScanGame());
        ClientCommandHandler.instance.registerCommand(new CommandAddAlias());
        ClientCommandHandler.instance.registerCommand(new CommandFKCounter());
        ClientCommandHandler.instance.registerCommand(new CommandNocheaters());
        ClientCommandHandler.instance.registerCommand(new CommandHypixelShout());
        ClientCommandHandler.instance.registerCommand(new CommandHypixelReply());
        ClientCommandHandler.instance.registerCommand(new CommandMWEnhancements());
        ClientCommandHandler.instance.registerCommand(new CommandHypixelMessage());

        WdredPlayers.init();

    }

}