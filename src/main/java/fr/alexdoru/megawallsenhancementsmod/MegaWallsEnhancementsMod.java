package fr.alexdoru.megawallsenhancementsmod;

import java.io.File;

import fr.alexdoru.megawallsenhancementsmod.commands.CommandCopyToClipboard;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandHypixelMessage;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandHypixelReply;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandHypixelShout;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandKill;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandMWGameStats;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandName;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandPlancke;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandScanGame;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandSetupApiKey;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandSquad;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandStalk;
import fr.alexdoru.megawallsenhancementsmod.events.ArrowHitLeapHitEvent;
import fr.alexdoru.megawallsenhancementsmod.events.ChatEvents;
import fr.alexdoru.megawallsenhancementsmod.events.KillCooldownEvent;
import fr.alexdoru.megawallsenhancementsmod.events.MWGameStatsEvent;
import fr.alexdoru.megawallsenhancementsmod.events.SquadEvent;
import fr.alexdoru.megawallsenhancementsmod.utils.HypixelApiKeyUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "mwenhancements", name = "MegaWallsEnhancements", version = "1.1",acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class MegaWallsEnhancementsMod {
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		
		MinecraftForge.EVENT_BUS.register(new ChatEvents());		 
		MinecraftForge.EVENT_BUS.register(new SquadEvent());
		MinecraftForge.EVENT_BUS.register(new MWGameStatsEvent());		
		MinecraftForge.EVENT_BUS.register(new KillCooldownEvent());	
		MinecraftForge.EVENT_BUS.register(new ArrowHitLeapHitEvent());
		
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandName());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandKill());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandSquad());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandStalk());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandPlancke());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandScanGame());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandSetupApiKey());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandMWGameStats());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandHypixelShout());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandHypixelReply());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandHypixelMessage());	
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandCopyToClipboard());	

		HypixelApiKeyUtil.apiFile = new File((Minecraft.getMinecraft()).mcDataDir,"config/HypixelApiKey.txt");
		HypixelApiKeyUtil.loadApiKey();
		Runtime.getRuntime().addShutdownHook(new Thread() {public void run() {HypixelApiKeyUtil.saveApiKey();}});

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

}