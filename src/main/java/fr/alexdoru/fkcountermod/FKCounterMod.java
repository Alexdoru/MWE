package fr.alexdoru.fkcountermod;

import fr.alexdoru.fkcountermod.commands.CommandFKCounter;
import fr.alexdoru.fkcountermod.events.ScoreboardEvent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = FKCounterMod.MODID, version = FKCounterMod.VERSION, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class FKCounterMod {

    public static final String MODID = "fkcounter";
    public static final String VERSION = "2.5";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ScoreboardEvent());
        ClientCommandHandler.instance.registerCommand(new CommandFKCounter());
    }

    /**
     * Returns true during a mega walls game
     */
    public static boolean isInMwGame() {
        return (ScoreboardEvent.getMwScoreboardParser().getGameId() != null);
    }

    /**
     * Returns true during the preparation phase of a mega walls game
     */
    public static boolean isitPrepPhase() {
        return (ScoreboardEvent.getMwScoreboardParser().isitPrepPhase());
    }

}