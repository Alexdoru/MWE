package fr.alexdoru.nocheatersmod;

import fr.alexdoru.megawallsenhancementsmod.commands.CommandNocheaters;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandReport;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandUnWDR;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandWDR;
import fr.alexdoru.nocheatersmod.data.WdredPlayers;
import fr.alexdoru.nocheatersmod.events.NoCheatersEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class NoCheatersMod {

    public static final String modid = "nocheaters";
    public static final String version = "2.3";

    public static final Logger logger = LogManager.getLogger("NoCheaters");
    public static final KeyBinding addtimemark_key = new KeyBinding("Add Timestamp", 0, "NoCheaters");

    public static void init() {

        ClientRegistry.registerKeyBinding(addtimemark_key);
        MinecraftForge.EVENT_BUS.register(new NoCheatersEvents());
        ClientCommandHandler.instance.registerCommand(new CommandWDR());
        ClientCommandHandler.instance.registerCommand(new CommandUnWDR());
        ClientCommandHandler.instance.registerCommand(new CommandReport());
        ClientCommandHandler.instance.registerCommand(new CommandNocheaters());
        WdredPlayers.wdrsFile = new File(Minecraft.getMinecraft().mcDataDir, "config/wdred.txt");
        Runtime.getRuntime().addShutdownHook(new Thread(WdredPlayers::saveReportedPlayers));
        WdredPlayers.loadReportedPlayers();

    }

}