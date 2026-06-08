package fr.alexdoru.mwe.api;

import fr.alexdoru.mwe.gui.HUDRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

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
}
