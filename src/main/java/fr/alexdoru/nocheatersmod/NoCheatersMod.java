package fr.alexdoru.nocheatersmod;

import java.io.File;

import fr.alexdoru.nocheatersmod.commands.CommandNocheaters;
import fr.alexdoru.nocheatersmod.commands.CommandReport;
import fr.alexdoru.nocheatersmod.commands.CommandSendReportAgain;
import fr.alexdoru.nocheatersmod.commands.CommandUnWDR;
import fr.alexdoru.nocheatersmod.commands.CommandWDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import fr.alexdoru.nocheatersmod.events.GameInfoGrabber;
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

@Mod(name = "No cheaters", modid = "nocheaters", version = "2.1", acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class NoCheatersMod {

	public static KeyBinding addtimemark_key = new KeyBinding("Add Timestamp", 0, "NoCheaters");
	
	private static boolean toggleicons = true;
	private static boolean togglewarnings = true;
	private static boolean toggleautoreport = false;
	/* Time before the mod suggests to report the player again */
	private static final long timeBetweenReports = 21600000L; // 6 heures
	/* It won't autoreport players whose last report is older than*/
	private static final long timeAutoReport = 1209600000L; // Two weeks

	@EventHandler
	public void init(FMLInitializationEvent event) {
		
		ClientRegistry.registerKeyBinding(addtimemark_key);
		MinecraftForge.EVENT_BUS.register(new NoCheatersEvents());
		MinecraftForge.EVENT_BUS.register(new GameInfoGrabber());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandWDR());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandUnWDR());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandReport());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandNocheaters());
		ClientCommandHandler.instance.registerCommand((ICommand)new CommandSendReportAgain());
		WdredPlayers.wdrsFile = new File((Minecraft.getMinecraft()).mcDataDir,"config/wdred.txt");
		Runtime.getRuntime().addShutdownHook(new Thread() {public void run() {WdredPlayers.saveReportedPlayers();}});
		WdredPlayers.loadReportedPlayers();
		
	}

	public static boolean areIconsToggled() {
		return toggleicons;
	}

	public static void setToggleicons(boolean toggleicons) {
		NoCheatersMod.toggleicons = toggleicons;
	}

	public static boolean areWarningsToggled() {
		return togglewarnings;
	}

	public static void setTogglewarnings(boolean togglewarnings) {
		NoCheatersMod.togglewarnings = togglewarnings;
	}

	public static boolean isAutoreportToggled() {
		return toggleautoreport;
	}

	public static void setToggleautoreport(boolean toggleautoreport) {
		NoCheatersMod.toggleautoreport = toggleautoreport;
	}
	
	public static long getTimebetweenreports() {
		return timeBetweenReports;
	}

	public static long getTimeautoreport() {
		return timeAutoReport;
	}
}