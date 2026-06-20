package fr.alexdoru.configlib.api;

import org.jetbrains.annotations.NotNull;

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
