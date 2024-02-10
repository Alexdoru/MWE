package fr.alexdoru.megawallsenhancementsmod;

import fr.alexdoru.megawallsenhancementsmod.asm.hooks.RenderPlayerHook_RenegadeArrowCount;
import fr.alexdoru.megawallsenhancementsmod.asm.loader.ASMLoadingPlugin;
import fr.alexdoru.megawallsenhancementsmod.chat.ChatListener;
import fr.alexdoru.megawallsenhancementsmod.commands.*;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.KeybindingListener;
import fr.alexdoru.megawallsenhancementsmod.features.FinalKillCounter;
import fr.alexdoru.megawallsenhancementsmod.features.LowHPIndicator;
import fr.alexdoru.megawallsenhancementsmod.features.MegaWallsEndGameStats;
import fr.alexdoru.megawallsenhancementsmod.features.SquadHandler;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiManager;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.PlayerJoinListener;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.ReportSuggestionHandler;
import fr.alexdoru.megawallsenhancementsmod.nocheaters.WdrData;
import fr.alexdoru.megawallsenhancementsmod.scoreboard.ScoreboardTracker;
import fr.alexdoru.megawallsenhancementsmod.updater.ModUpdater;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.UUID;

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
    public static final String version = "3.5";
    public static final Logger logger = LogManager.getLogger(modName);
    public static File jarFile;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.preInit(event.getSuggestedConfigurationFile());
        jarFile = event.getSourceFile();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(new GuiManager());
        MinecraftForge.EVENT_BUS.register(new ModUpdater());
        MinecraftForge.EVENT_BUS.register(new ChatListener());
        MinecraftForge.EVENT_BUS.register(new SquadHandler());
        MinecraftForge.EVENT_BUS.register(new LowHPIndicator());
        MinecraftForge.EVENT_BUS.register(new FinalKillCounter());
        MinecraftForge.EVENT_BUS.register(new ScoreboardTracker());
        MinecraftForge.EVENT_BUS.register(new PlayerJoinListener());
        MinecraftForge.EVENT_BUS.register(new KeybindingListener());
        MinecraftForge.EVENT_BUS.register(new MegaWallsEndGameStats());
        MinecraftForge.EVENT_BUS.register(new ReportSuggestionHandler());
        MinecraftForge.EVENT_BUS.register(new RenderPlayerHook_RenegadeArrowCount());

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
        ClientCommandHandler.instance.registerCommand(new CommandMWEnhancements());

        WdrData.init();

    }

    public static boolean isDev() {
        if (!ASMLoadingPlugin.isObf()) return true;
        if (Minecraft.getMinecraft().thePlayer == null) return false;
        return Minecraft.getMinecraft().thePlayer.getUniqueID().equals(UUID.fromString("57715d32-a685-4e2e-ae68-54c19808b58d"));
    }

}