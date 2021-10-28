package fr.alexdoru.megawallsenhancementsmod;

import fr.alexdoru.megawallsenhancementsmod.commands.*;
import fr.alexdoru.megawallsenhancementsmod.config.MWEnConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "mwenhancements", name = "MegaWallsEnhancements", version = "2.9.4", acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class MegaWallsEnhancementsMod {

    public static final KeyBinding log_key_fast = new KeyBinding("Fast log glitch", 0, "MegaWallsEnhancements");
    public static final KeyBinding log_key_normal = new KeyBinding("Log glitch", 0, "MegaWallsEnhancements");
    public static final KeyBinding killkey = new KeyBinding("/Kill macro", 0, "MegaWallsEnhancements");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MWEnConfigHandler.preinit(event.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        ClientRegistry.registerKeyBinding(log_key_fast);
        ClientRegistry.registerKeyBinding(log_key_normal);
        ClientRegistry.registerKeyBinding(killkey);

        MinecraftForge.EVENT_BUS.register(new ChatEvents());
        MinecraftForge.EVENT_BUS.register(new SquadEvent());
        MinecraftForge.EVENT_BUS.register(new KeybindingsEvent());
        MinecraftForge.EVENT_BUS.register(new MWGameStatsEvent());
        MinecraftForge.EVENT_BUS.register(new KillCooldownEvent());
        MinecraftForge.EVENT_BUS.register(new ArrowHitLeapHitEvent());

        ClientCommandHandler.instance.registerCommand(new CommandName());
        ClientCommandHandler.instance.registerCommand(new CommandKill());
        ClientCommandHandler.instance.registerCommand(new CommandSquad());
        ClientCommandHandler.instance.registerCommand(new CommandStalk());
        ClientCommandHandler.instance.registerCommand(new CommandPlancke());
        ClientCommandHandler.instance.registerCommand(new CommandScanGame());
        ClientCommandHandler.instance.registerCommand(new CommandStalkList());
        ClientCommandHandler.instance.registerCommand(new CommandSetupApiKey());
        ClientCommandHandler.instance.registerCommand(new CommandMWGameStats());
        //ClientCommandHandler.instance.registerCommand(new CommandAPIRequests());
        ClientCommandHandler.instance.registerCommand(new CommandHypixelShout());
        ClientCommandHandler.instance.registerCommand(new CommandHypixelReply());
        ClientCommandHandler.instance.registerCommand(new CommandMWEnhancements());
        ClientCommandHandler.instance.registerCommand(new CommandHypixelMessage());
        ClientCommandHandler.instance.registerCommand(new CommandCopyToClipboard());

    }

}