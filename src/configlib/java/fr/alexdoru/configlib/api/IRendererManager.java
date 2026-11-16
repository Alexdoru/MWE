package fr.alexdoru.configlib.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Obtained from {@link IConfigHandler#getRendererManager()}, not meant to be implemented.
 */
@ApiStatus.NonExtendable
public interface IRendererManager {

    /**
     * Registers a HUD renderer to the config,
     * this renderer will be automatically rendered
     */
    void registerHUDRenderer(@NotNull IRenderer renderer);

    /**
     * Registers a renderer to the config,
     * this renderer will NOT be automatically rendered
     */
    void registerRenderer(@NotNull IRenderer renderer);

}
