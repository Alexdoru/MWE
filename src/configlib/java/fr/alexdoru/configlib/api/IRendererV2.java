package fr.alexdoru.configlib.api;

import net.minecraft.client.gui.ScaledResolution;
import org.jetbrains.annotations.ApiStatus;

/**
 * A HUD with a configurable scale and alignment.
 */
public interface IRendererV2 extends IRenderer {

    /**
     * Renders the HUD.
     * <p>
     * To be correctly scaled and aligned, implementors must:
     * <ol>
     *     <li>compute the unscaled width and height of the HUD</li>
     *     <li>call {@link RendererPositionV2#applyTransform(ScaledResolution, int, int)} with that size</li>
     *     <li>draw the HUD with (0, 0) as its top left corner, without using the screen size or the position's coordinates</li>
     * </ol>
     * For example:
     * <pre>{@code
     * public void render(ScaledResolution resolution) {
     *     final int width = mc.fontRendererObj.getStringWidth(text);
     *     final int height = mc.fontRendererObj.FONT_HEIGHT;
     *     getPosition().applyTransform(resolution, width, height);
     *     mc.fontRendererObj.drawStringWithShadow(text, 0, 0, 0xFFFFFF);
     * }
     * }</pre>
     * HUDs registered with {@link IRendererManager#registerHUDRenderer(IRenderer)} are rendered
     * between a GL matrix push and pop, so the transform doesn't need to be undone.
     *
     * @param resolution - Screen's resolution
     */
    @Override
    void render(ScaledResolution resolution);

    /**
     * Renders the dummy HUD, which is
     * used in the configuration screen
     * where you can move it around.
     * <p>
     * It follows the same rules as {@link #render(ScaledResolution)}
     * and is also rendered between a GL matrix push and pop.
     */
    void renderDummy(ScaledResolution resolution);

    /**
     * @deprecated V2 renderers are only drawn through {@link #renderDummy(ScaledResolution)},
     * this method is never called on them. Don't override or call it.
     */
    @Deprecated
    @ApiStatus.NonExtendable
    @Override
    default void renderDummy() {
        throw new UnsupportedOperationException("IRendererV2 uses renderDummy(ScaledResolution)");
    }

    /**
     * Returns the RendererPositionV2 of the Renderer
     */
    @Override
    RendererPositionV2 getPosition();

}
