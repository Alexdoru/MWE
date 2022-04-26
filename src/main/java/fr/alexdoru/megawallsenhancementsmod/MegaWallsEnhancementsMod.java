package fr.alexdoru.megawallsenhancementsmod;

import fr.alexdoru.fkcountermod.FKCounterMod;
import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.megawallsenhancementsmod.commands.*;
import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.*;
import fr.alexdoru.megawallsenhancementsmod.gui.guiapi.GuiManager;
import fr.alexdoru.nocheatersmod.NoCheatersMod;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

@Mod(modid = MegaWallsEnhancementsMod.modid, name = MegaWallsEnhancementsMod.modName, version = MegaWallsEnhancementsMod.version, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class MegaWallsEnhancementsMod {

    public static final String modid = "mwenhancements";
    public static final String modName = "MegaWallsEnhancements";
    public static final String version = "1.8";
    public static final KeyBinding toggleDroppedItemLimit = new KeyBinding("Toggle dropped item limit", 0, "MegaWallsEnhancements");
    public static File configurationFile;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        configurationFile = event.getSuggestedConfigurationFile();
        ConfigHandler.preinit(configurationFile);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        FKCounterMod.init();

        ClientRegistry.registerKeyBinding(toggleDroppedItemLimit);

        MinecraftForge.EVENT_BUS.register(new GuiManager());
        MinecraftForge.EVENT_BUS.register(new ChatEvents());
        MinecraftForge.EVENT_BUS.register(new SquadEvent());
        MinecraftForge.EVENT_BUS.register(new KillCounter());
        MinecraftForge.EVENT_BUS.register(new LowHPIndicator());
        MinecraftForge.EVENT_BUS.register(new UpdateNotifier());
        MinecraftForge.EVENT_BUS.register(new KeybindingsEvent());
        MinecraftForge.EVENT_BUS.register(new MWGameStatsEvent());

        ClientCommandHandler.instance.registerCommand(new CommandName());
        ClientCommandHandler.instance.registerCommand(new CommandKill());
        ClientCommandHandler.instance.registerCommand(new CommandSquad());
        ClientCommandHandler.instance.registerCommand(new CommandStalk());
        ClientCommandHandler.instance.registerCommand(new CommandPlancke());
        ClientCommandHandler.instance.registerCommand(new CommandScanGame());
        ClientCommandHandler.instance.registerCommand(new CommandHypixelShout());
        ClientCommandHandler.instance.registerCommand(new CommandHypixelReply());
        ClientCommandHandler.instance.registerCommand(new CommandMWEnhancements());
        ClientCommandHandler.instance.registerCommand(new CommandHypixelMessage());

        NoCheatersMod.init();

    }

}