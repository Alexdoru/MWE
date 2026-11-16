package fr.alexdoru.configlib.api;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.jetbrains.annotations.ApiStatus;

/**
 * Position of a {@link IRendererV2}. The relative coordinates are an anchor point
 * on the screen and the {@link RendererAlignment} says which point of the HUD's box sits on it.
 * The absolute coordinates methods inherited from {@link RendererPosition} are not supported,
 * use {@link #applyTransform(ScaledResolution, int, int)} instead.
 */
public final class RendererPositionV2 extends RendererPosition {

    public static final double MIN_SCALE = 0.5d;
    public static final double MAX_SCALE = 3d;

    private final double defaultScale;
    private double scale;
    private final RendererAlignment defaultAlignment;
    private RendererAlignment alignment;

    /** The last rendered box in screen space, already scaled */
    private float boxX, boxY, boxWidth, boxHeight;

    public RendererPositionV2(boolean enabled, double defaultX, double defaultY, double defaultScale, RendererAlignment defaultAlignment) {
        super(enabled, defaultX, defaultY);
        this.defaultScale = clampScale(defaultScale);
        this.scale = this.defaultScale;
        this.defaultAlignment = defaultAlignment;
        this.alignment = defaultAlignment;
    }

    static double clampScale(double scale) {
        return Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
    }

    /**
     * Moves and scales the GL matrix so that the HUD can be drawn with (0, 0) as its top left corner.
     * The caller is responsible for pushing and popping the matrix.
     *
     * @param width  unscaled width of the HUD
     * @param height unscaled height of the HUD
     */
    public void applyTransform(ScaledResolution res, int width, int height) {
        updateBox(res.getScaledWidth(), res.getScaledHeight(), res.getScaleFactor(), width, height);
        GlStateManager.translate(boxX, boxY, 0f);
        GlStateManager.scale(scale, scale, 1d);
    }

    /**
     * Computes the scaled HUD box from the anchor and keeps it inside the screen
     */
    void updateBox(int screenWidth, int screenHeight, int scaleFactor, int width, int height) {
        this.boxWidth = (float) (width * scale);
        this.boxHeight = (float) (height * scale);
        final float x = (float) (getRelativeX() * screenWidth - alignment.xFactor * boxWidth);
        final float y = (float) (getRelativeY() * screenHeight - alignment.yFactor * boxHeight);
        this.boxX = snapToPixelGrid(x, screenWidth - boxWidth, scaleFactor);
        this.boxY = snapToPixelGrid(y, screenHeight - boxHeight, scaleFactor);
    }

    /**
     * The font texture is sampled with GL_NEAREST, so drawing text off the
     * physical pixel grid gives glyphs uneven widths and makes it jitter
     */
    private static float snapToPixelGrid(float value, float max, int scaleFactor) {
        final float snapped = Math.round(value * scaleFactor) / (float) scaleFactor;
        final float snappedMax = (float) Math.floor(max * scaleFactor) / scaleFactor;
        return Math.max(0f, Math.min(snapped, snappedMax));
    }

    @ApiStatus.Internal
    @Override
    public void resetToDefault() {
        super.resetToDefault();
        this.scale = defaultScale;
        this.alignment = defaultAlignment;
    }

    private static UnsupportedOperationException absolutePositionUnsupported() {
        return new UnsupportedOperationException("RendererPositionV2 has no absolute position, use applyTransform instead");
    }

    @Deprecated
    @Override
    public void setAbsolutePositionForRender(int x, int y) {
        throw absolutePositionUnsupported();
    }

    @Deprecated
    @Override
    public void saveAbsoluteToRelative() {
        throw absolutePositionUnsupported();
    }

    @Deprecated
    @Override
    public void updateAbsolutePosition() {
        throw absolutePositionUnsupported();
    }

    @Deprecated
    @Override
    public void updateAbsolutePosition(ScaledResolution res) {
        throw absolutePositionUnsupported();
    }

    @Deprecated
    @Override
    public void updateAdjustedAbsolutePosition(ScaledResolution res, int hudWidth, int hudHeight) {
        throw absolutePositionUnsupported();
    }

    @Deprecated
    @Override
    public void updateAdjustedAbsolutePosition(ScaledResolution res, int hudWidth, int hudHeight, int renderOffsetX, int renderOffsetY) {
        throw absolutePositionUnsupported();
    }

    @Deprecated
    @Override
    public int getAbsoluteRenderX() {
        throw absolutePositionUnsupported();
    }

    @Deprecated
    @Override
    public int getAbsoluteRenderY() {
        throw absolutePositionUnsupported();
    }

    @ApiStatus.Internal
    public double getScale() {
        return scale;
    }

    @ApiStatus.Internal
    public void setScale(double scale) {
        this.scale = clampScale(scale);
    }

    @ApiStatus.Internal
    public RendererAlignment getAlignment() {
        return alignment;
    }

    @ApiStatus.Internal
    public void setAlignment(RendererAlignment alignment) {
        this.alignment = alignment;
    }

    @ApiStatus.Internal
    public RendererAlignment getDefaultAlignment() {
        return defaultAlignment;
    }

    @ApiStatus.Internal
    public float getBoxX() {
        return boxX;
    }

    @ApiStatus.Internal
    public float getBoxY() {
        return boxY;
    }

    @ApiStatus.Internal
    public float getBoxWidth() {
        return boxWidth;
    }

    @ApiStatus.Internal
    public float getBoxHeight() {
        return boxHeight;
    }

}
