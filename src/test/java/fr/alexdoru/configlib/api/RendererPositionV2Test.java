package fr.alexdoru.configlib.api;

import org.junit.Test;

import static org.junit.Assert.*;

public class RendererPositionV2Test {

    private static final float DELTA = 1e-4f;

    /**
     * @return {x, y, scaledWidth, scaledHeight} of a 20x10 HUD at scale 2 on a 400x200 screen
     */
    private static float[] box(double anchorX, double anchorY, RendererAlignment alignment) {
        final RendererPositionV2 position = new RendererPositionV2(true, anchorX / 400d, anchorY / 200d, 2d, alignment);
        position.updateBox(400, 200, 1, 20, 10);
        return new float[]{position.getBoxX(), position.getBoxY(), position.getBoxWidth(), position.getBoxHeight()};
    }

    private static float boxX(double anchorX, double scale, int width, int scaleFactor) {
        final RendererPositionV2 position = new RendererPositionV2(true, anchorX / 400d, 0d, scale, RendererAlignment.TOP_LEFT);
        position.updateBox(400, 200, scaleFactor, width, 10);
        return position.getBoxX();
    }

    private static float[] centerBox(RendererAlignment alignment) {
        return box(200d, 100d, alignment);
    }

    @Test
    public void updateBoxAllAlignments() {
        assertArrayEquals(new float[]{200f, 100f, 40f, 20f}, centerBox(RendererAlignment.TOP_LEFT), DELTA);
        assertArrayEquals(new float[]{180f, 100f, 40f, 20f}, centerBox(RendererAlignment.TOP), DELTA);
        assertArrayEquals(new float[]{160f, 100f, 40f, 20f}, centerBox(RendererAlignment.TOP_RIGHT), DELTA);
        assertArrayEquals(new float[]{200f, 90f, 40f, 20f}, centerBox(RendererAlignment.LEFT), DELTA);
        assertArrayEquals(new float[]{180f, 90f, 40f, 20f}, centerBox(RendererAlignment.CENTER), DELTA);
        assertArrayEquals(new float[]{160f, 90f, 40f, 20f}, centerBox(RendererAlignment.RIGHT), DELTA);
        assertArrayEquals(new float[]{200f, 80f, 40f, 20f}, centerBox(RendererAlignment.BOTTOM_LEFT), DELTA);
        assertArrayEquals(new float[]{180f, 80f, 40f, 20f}, centerBox(RendererAlignment.BOTTOM), DELTA);
        assertArrayEquals(new float[]{160f, 80f, 40f, 20f}, centerBox(RendererAlignment.BOTTOM_RIGHT), DELTA);
    }

    @Test
    public void updateBoxClampsToScreen() {
        assertArrayEquals(new float[]{360f, 180f, 40f, 20f}, box(390d, 195d, RendererAlignment.TOP_LEFT), DELTA);
        assertArrayEquals(new float[]{0f, 0f, 40f, 20f}, box(10d, 5d, RendererAlignment.BOTTOM_RIGHT), DELTA);
        assertArrayEquals(new float[]{0f, 0f, 40f, 20f}, box(-50d, -50d, RendererAlignment.CENTER), DELTA);
        assertArrayEquals(new float[]{360f, 180f, 40f, 20f}, box(1000d, 1000d, RendererAlignment.CENTER), DELTA);
    }

    @Test
    public void updateBoxSnapsToPhysicalPixels() {
        assertEquals(100f, boxX(100.3d, 1d, 20, 1), DELTA);
        assertEquals(100.5f, boxX(100.3d, 1d, 20, 2), DELTA);
        assertEquals(100f + 1f / 3f, boxX(100.3d, 1d, 20, 3), DELTA);
        // 15 * 1.5 = 22.5 wide, the clamped edge 377.5 is floored so the box stays inside the screen
        assertEquals(377f, boxX(1000d, 1.5d, 15, 1), DELTA);
        assertEquals(377.5f, boxX(1000d, 1.5d, 15, 2), DELTA);
        assertEquals(0f, boxX(-50d, 1d, 20, 3), DELTA);
    }

    @Test
    public void scaleIsClamped() {
        assertEquals(RendererPositionV2.MIN_SCALE, RendererPositionV2.clampScale(0.1d), 0d);
        assertEquals(RendererPositionV2.MAX_SCALE, RendererPositionV2.clampScale(10d), 0d);
        assertEquals(1.5d, RendererPositionV2.clampScale(1.5d), 0d);
        final RendererPositionV2 position = new RendererPositionV2(true, 0d, 0d, 1d, RendererAlignment.TOP_LEFT);
        position.setScale(-1d);
        assertEquals(RendererPositionV2.MIN_SCALE, position.getScale(), 0d);
        position.setScale(42d);
        assertEquals(RendererPositionV2.MAX_SCALE, position.getScale(), 0d);
    }

    @Test
    public void alignmentCycleWraps() {
        assertEquals(RendererAlignment.TOP, RendererAlignment.TOP_LEFT.cycle(1));
        assertEquals(RendererAlignment.CENTER, RendererAlignment.TOP_LEFT.cycle(-1));
        assertEquals(RendererAlignment.TOP_LEFT, RendererAlignment.CENTER.cycle(1));
        assertEquals(RendererAlignment.LEFT, RendererAlignment.CENTER.cycle(-1));
        assertEquals(RendererAlignment.CENTER, RendererAlignment.CENTER.cycle(RendererAlignment.values().length));
    }

    @Test
    public void absolutePositionMethodsUnsupported() {
        final RendererPosition position = new RendererPositionV2(true, 0d, 0d, 1d, RendererAlignment.TOP_LEFT);
        assertThrows(UnsupportedOperationException.class, position::getAbsoluteRenderX);
        assertThrows(UnsupportedOperationException.class, () -> position.setAbsolutePositionForRender(1, 1));
        assertThrows(UnsupportedOperationException.class, position::saveAbsoluteToRelative);
    }

}
