package fr.alexdoru.configlib.api;

/**
 * Which point of a HUD's box is placed on its anchor point
 */
public enum RendererAlignment {

    TOP_LEFT(0f, 0f),
    TOP(0.5f, 0f),
    TOP_RIGHT(1f, 0f),
    RIGHT(1f, 0.5f),
    BOTTOM_RIGHT(1f, 1f),
    BOTTOM(0.5f, 1f),
    BOTTOM_LEFT(0f, 1f),
    LEFT(0f, 0.5f),
    CENTER(0.5f, 0.5f);

    private static final RendererAlignment[] VALUES = values();

    public final float xFactor;
    public final float yFactor;

    RendererAlignment(float xFactor, float yFactor) {
        this.xFactor = xFactor;
        this.yFactor = yFactor;
    }

    public RendererAlignment cycle(int direction) {
        return VALUES[((ordinal() + direction) % VALUES.length + VALUES.length) % VALUES.length];
    }

}
