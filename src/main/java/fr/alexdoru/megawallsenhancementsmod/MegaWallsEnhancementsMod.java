package fr.alexdoru.megawallsenhancementsmod;

import fr.alexdoru.megawallsenhancementsmod.commands.CommandCopyToClipboard;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandHypixelMessage;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandHypixelReply;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandHypixelShout;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandKill;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandMWEnhancements;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandMWGameStats;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandName;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandPlancke;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandScanGame;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandSetupApiKey;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandSquad;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandStalk;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandStalkList;
import fr.alexdoru.megawallsenhancementsmod.config.MWEnConfigHandler;
import fr.alexdoru.megawallsenhancementsmod.events.ArrowHitLeapHitEvent;
import fr.alexdoru.megawallsenhancementsmod.events.ChatEvents;
import fr.alexdoru.megawallsenhancementsmod.events.KeybindingsEvent;
import fr.alexdoru.megawallsenhancementsmod.events.KillCooldownEvent;
import fr.alexdoru.megawallsenhancementsmod.events.MWGameStatsEvent;
import fr.alexdoru.megawallsenhancementsmod.events.SquadEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "mwenhancements", name = "MegaWallsEnhancements", version = "2.9.4",acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class MegaWallsEnhancementsMod {
	
	public static KeyBinding log_key_fast = new KeyBinding("Fast log glitch", 0, "MegaWallsEnhancements");
	public static KeyBinding log_key_normal = new KeyBinding("Log glitch", 0, "MegaWallsEnhancements");
	public static KeyBinding killkey = new KeyBinding("/Kill macro", 0, "MegaWallsEnhancements");
	
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
		
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandName());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandKill());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandSquad());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandStalk());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandPlancke());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandScanGame());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandStalkList());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandSetupApiKey());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandMWGameStats());
		//ClientCommandHandler.instance.registerCommand((ICommand)new CommandAPIRequests());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandHypixelShout());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandHypixelReply());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandMWEnhancements());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandHypixelMessage());	
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandCopyToClipboard());	

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

}