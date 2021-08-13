package fr.alexdoru.nocheatersmod;

import java.io.File;

import fr.alexdoru.nocheatersmod.commands.CommandNocheaters;
import fr.alexdoru.nocheatersmod.commands.CommandReport;
import fr.alexdoru.nocheatersmod.commands.CommandSendReportAgain;
import fr.alexdoru.nocheatersmod.commands.CommandUnWDR;
import fr.alexdoru.nocheatersmod.commands.CommandWDR;
import fr.alexdoru.nocheatersmod.events.GameInfoGrabber;
import fr.alexdoru.nocheatersmod.events.KeybindingsEvent;
import fr.alexdoru.nocheatersmod.events.NoCheatersEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(name = "No cheaters", modid = "nocheaters", version = "2.0", acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class NoCheatersMod {

	public static KeyBinding addtimemark_key = new KeyBinding("Add Timestamp", 0, "NoCheaters");

	@EventHandler
	public void init(FMLInitializationEvent event) {
		
		ClientRegistry.registerKeyBinding(addtimemark_key);
		MinecraftForge.EVENT_BUS.register(new KeybindingsEvent());
		MinecraftForge.EVENT_BUS.register(new NoCheatersEvents());
		MinecraftForge.EVENT_BUS.register(new GameInfoGrabber());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandWDR());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandUnWDR());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandReport());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandNocheaters());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandSendReportAgain());
		NoCheatersEvents.wdrsFile = new File((Minecraft.getMinecraft()).mcDataDir,"config/wdred.txt");
		Runtime.getRuntime().addShutdownHook(new Thread() {public void run() {NoCheatersEvents.saveReportedPlayers();}});
		NoCheatersEvents.loadReportedPlayers();
		
	}

}