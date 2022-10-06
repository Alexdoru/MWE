package fr.alexdoru.fkcountermod;

import fr.alexdoru.fkcountermod.events.ScoreboardEvent;
import fr.alexdoru.megawallsenhancementsmod.commands.CommandFKCounter;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

public class FKCounterMod {

    public static final String MODID = "fkcounter";
    public static final String VERSION = "2.7";

    /**
     * Turns true from the moment the player gets tp to the cage to the end of the game
     * False in the pre game lobby
     */
    public static boolean isInMwGame = false;
    /**
     * True in the pre game lobby in mega walls
     */
    public static boolean preGameLobby = false;
    /**
     * True during the preparation phase of a mega walls game
     */
    public static boolean isitPrepPhase = false;
    public static boolean isMWEnvironement = false;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new ScoreboardEvent());
        ClientCommandHandler.instance.registerCommand(new CommandFKCounter());
    }

}