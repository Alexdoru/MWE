package fr.alexdoru.mwe.api;

import fr.alexdoru.mwe.features.SquadHandler;
import fr.alexdoru.mwe.gui.HUDRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class MWEApi {

    private static final Logger LOGGER = LogManager.getLogger("MWE API");

    private MWEApi() {}

    public static final class Hud {

        private Hud() {}

        /**
         * Register a HUD to render
         */
        public static void registerHUD(@NotNull IRenderer renderer) {
            HUDRenderer.registerRenderer(renderer);
            LOGGER.debug("Registered HUD {}", renderer.getClass().getName());
        }
    }

    public static final class Squad {

        private Squad() {}

        /**
         * Returns true if the player is in the squad
         */
        public static boolean isSquadmate(String playername) {
            return SquadHandler.isSquadmate(playername);
        }

        /**
         * Adds a player to the squad
         */
        public static void addSquadmate(String playername) {
            SquadHandler.addPlayer(playername);
        }

        /**
         * Adds a player to the squad with a replacement name
         */
        public static void addSquadmate(String playername, String friendlyName) {
            SquadHandler.addPlayer(playername, friendlyName);
        }

        /**
         * Removes a player from the squad
         */
        public static void removeSquadmate(String playername) {
            SquadHandler.removePlayer(playername);
        }

        /**
         * Returns an unmodifiable view of the squad map
         */
        public static Map<String, String> getSquadMap() {
            return SquadHandler.getSquad();
        }
    }
}
