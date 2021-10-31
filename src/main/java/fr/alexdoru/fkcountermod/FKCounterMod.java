package fr.alexdoru.fkcountermod;

import fr.alexdoru.fkcountermod.commands.CommandFKCounter;
import fr.alexdoru.fkcountermod.config.ConfigHandler;
import fr.alexdoru.fkcountermod.events.KillCounter;
import fr.alexdoru.fkcountermod.events.MwGameEvent;
import fr.alexdoru.fkcountermod.events.ScoreboardEvent;
import fr.alexdoru.fkcountermod.gui.FKCounterGui;
import fr.alexdoru.fkcountermod.hudproperty.HUDManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = FKCounterMod.MODID, version = FKCounterMod.VERSION, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class FKCounterMod {

    public static final String MODID = "fkcounter";
    public static final String VERSION = "2.6";
    private static ConfigHandler configHandler;
    private static HUDManager hudManager;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        configHandler = new ConfigHandler(event.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new KillCounter());
        MinecraftForge.EVENT_BUS.register(new ScoreboardEvent());

        hudManager = HUDManager.newInstance();
        hudManager.register(new FKCounterGui());

        configHandler.loadConfig();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new CommandFKCounter());
    }

    @SubscribeEvent
    public void onMwGame(MwGameEvent event) {

        if (event.getType() == MwGameEvent.EventType.CONNECT) {

            String currentGameId = ScoreboardEvent.getMwScoreboardParser().getGameId(); // this is not null due to how the event is defined/Posted

            if (KillCounter.getGameId() == null || !KillCounter.getGameId().equals(currentGameId)) {
                KillCounter.ResetKillCounterTo(currentGameId);
            }

            return;
        }

        /*
         * to fix the bug where the FKCounter doesn't work properly if you play two games in a row on a server with the same serverID
         */
        if (event.getType() == MwGameEvent.EventType.GAME_END) {
            KillCounter.ResetKillCounterTo(null);
        }

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

    public static ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public static HUDManager getHudManager() {
        return hudManager;
    }

}