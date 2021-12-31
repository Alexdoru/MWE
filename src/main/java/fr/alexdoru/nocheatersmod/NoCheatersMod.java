package fr.alexdoru.nocheatersmod;

import fr.alexdoru.nocheatersmod.commands.*;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import fr.alexdoru.nocheatersmod.events.NoCheatersEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.io.File;

@Mod(name = "No cheaters", modid = NoCheatersMod.modid, version = NoCheatersMod.version, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class NoCheatersMod {

    public static final String modid = "nocheaters";
    public static final String version = "2.1";

    public static final KeyBinding addtimemark_key = new KeyBinding("Add Timestamp", 0, "NoCheaters");

    @EventHandler
    public void init(FMLInitializationEvent event) {

        ClientRegistry.registerKeyBinding(addtimemark_key);
        MinecraftForge.EVENT_BUS.register(new NoCheatersEvents());
        ClientCommandHandler.instance.registerCommand(new CommandWDR());
        ClientCommandHandler.instance.registerCommand(new CommandUnWDR());
        ClientCommandHandler.instance.registerCommand(new CommandReport());
        ClientCommandHandler.instance.registerCommand(new CommandNocheaters());
        ClientCommandHandler.instance.registerCommand(new CommandSendReportAgain());
        WdredPlayers.wdrsFile = new File((Minecraft.getMinecraft()).mcDataDir, "config/wdred.txt");
        Runtime.getRuntime().addShutdownHook(new Thread(WdredPlayers::saveReportedPlayers));
        WdredPlayers.loadReportedPlayers();

    }

}