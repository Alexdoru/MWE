package fr.alexdoru.mwe.hackerdetector.data.buffers;

import org.junit.Test;

import static org.junit.Assert.*;

public class SampleBufferDTest {

    private static final double DELTA = 1e-9;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    @Test
    public void constructorThrowsForCapacityLessThanTwo() {
        assertThrows(IllegalArgumentException.class, () -> new SampleBufferD(1));
        assertThrows(IllegalArgumentException.class, () -> new SampleBufferD(0));
        assertThrows(IllegalArgumentException.class, () -> new SampleBufferD(-5));
    }

    @Test
    public void constructorAcceptsMinimumCapacity() {
        final SampleBufferD buf = new SampleBufferD(2);
        assertEquals(2, buf.capacity());
        assertEquals(0, buf.size());
    }

    @Test
    public void newBufferIsEmptyAndNotFull() {
        final SampleBufferD buf = new SampleBufferD(3);
        assertTrue(buf.isEmpty());
        assertFalse(buf.isFull());
        assertEquals(0, buf.size());
    }

    // ---------------------------------------------------------------
    // add / size / isFull / isEmpty
    // ---------------------------------------------------------------

    @Test
    public void sizeGrowsUpToCapacity() {
        final SampleBufferD buf = new SampleBufferD(3);
        buf.add(1.0);
        assertEquals(1, buf.size());
        buf.add(2.0);
        assertEquals(2, buf.size());
        buf.add(3.0);
        assertEquals(3, buf.size());
        assertTrue(buf.isFull());

        buf.add(4.0); // beyond capacity
        assertEquals(3, buf.size());
        assertTrue(buf.isFull());
    }

    @Test
    public void isEmptyIsFullToggle() {
        final SampleBufferD buf = new SampleBufferD(2);
        assertTrue(buf.isEmpty());
        assertFalse(buf.isFull());

        buf.add(10.0);
        assertFalse(buf.isEmpty());
        assertFalse(buf.isFull());

        buf.add(20.0);
        assertFalse(buf.isEmpty());
        assertTrue(buf.isFull());
    }

    // ---------------------------------------------------------------
    // get()
    // ---------------------------------------------------------------

    @Test
    public void getZeroReturnsLatest() {
        final SampleBufferD buf = new SampleBufferD(5);
        buf.add(1.0);
        buf.add(2.0);
        buf.add(3.0);
        assertEquals(3.0, buf.get(0), DELTA);
    }

    @Test
    public void getLastIndexReturnsOldest() {
        final SampleBufferD buf = new SampleBufferD(5);
        buf.add(1.0);
        buf.add(2.0);
        buf.add(3.0);
        assertEquals(1.0, buf.get(buf.size() - 1), DELTA);
    }

    @Test
    public void getReturnsCorrectOrder() {
        final SampleBufferD buf = new SampleBufferD(5);
        buf.add(10.0);
        buf.add(20.0);
        buf.add(30.0);
        assertEquals(30.0, buf.get(0), DELTA);
        assertEquals(20.0, buf.get(1), DELTA);
        assertEquals(10.0, buf.get(2), DELTA);
    }

    @Test
    public void getThrowsForNegativeIndex() {
        final SampleBufferD buf = new SampleBufferD(3);
        buf.add(1.0);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> buf.get(-1));
    }

    @Test
    public void getThrowsForIndexEqualToSize() {
        final SampleBufferD buf = new SampleBufferD(3);
        buf.add(1.0);
        buf.add(2.0);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> buf.get(2));
    }

    @Test
    public void getThrowsForIndexGreaterThanSize() {
        final SampleBufferD buf = new SampleBufferD(3);
        buf.add(1.0);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> buf.get(5));
    }

    @Test
    public void getThrowsOnEmptyBuffer() {
        final SampleBufferD buf = new SampleBufferD(3);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> buf.get(0));
    }

    // ---------------------------------------------------------------
    // wraparound behavior
    // ---------------------------------------------------------------

    @Test
    public void wraparoundOverwritesOldest() {
        final SampleBufferD buf = new SampleBufferD(3);
        buf.add(1.0);
        buf.add(2.0);
        buf.add(3.0);
        // buffer full: [3,2,1] latest-to-oldest
        buf.add(4.0);
        // 1.0 should have been overwritten; order is now [4,3,2]
        assertEquals(3, buf.size());
        assertEquals(4.0, buf.get(0), DELTA);
        assertEquals(3.0, buf.get(1), DELTA);
        assertEquals(2.0, buf.get(2), DELTA);
    }

    @Test
    public void wraparoundMultipleCycles() {
        final SampleBufferD buf = new SampleBufferD(3);
        for (int i = 1; i <= 7; i++) {
            buf.add(i);
        }
        // last three added values were 5, 6, 7
        assertEquals(3, buf.size());
        assertEquals(7.0, buf.get(0), DELTA);
        assertEquals(6.0, buf.get(1), DELTA);
        assertEquals(5.0, buf.get(2), DELTA);
    }

    // ---------------------------------------------------------------
    // clear()
    // ---------------------------------------------------------------

    @Test
    public void clearResetsBuffer() {
        final SampleBufferD buf = new SampleBufferD(3);
        buf.add(1.0);
        buf.add(2.0);
        buf.clear();

        assertEquals(0, buf.size());
        assertTrue(buf.isEmpty());
        assertFalse(buf.isFull());
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> buf.get(0));
    }

    @Test
    public void addAfterClearBehavesLikeFreshBuffer() {
        final SampleBufferD buf = new SampleBufferD(3);
        buf.add(1.0);
        buf.add(2.0);
        buf.add(3.0);
        buf.clear();

        buf.add(9.0);
        assertEquals(1, buf.size());
        assertEquals(9.0, buf.get(0), DELTA);
    }

    // ---------------------------------------------------------------
    // sum() / average()
    // ---------------------------------------------------------------

    @Test
    public void sumOfEmptyBufferIsZero() {
        final SampleBufferD buf = new SampleBufferD(3);
        assertEquals(0.0, buf.sum(), DELTA);
    }

    @Test
    public void sumAddsAllElements() {
        final SampleBufferD buf = new SampleBufferD(4);
        buf.add(1.5);
        buf.add(2.5);
        buf.add(3.0);
        assertEquals(7.0, buf.sum(), DELTA);
    }

    @Test
    public void sumAfterWraparound() {
        final SampleBufferD buf = new SampleBufferD(2);
        buf.add(10.0);
        buf.add(20.0);
        buf.add(30.0); // overwrites 10.0
        assertEquals(50.0, buf.sum(), DELTA); // 20 + 30
    }

    @Test
    public void averageComputesMean() {
        final SampleBufferD buf = new SampleBufferD(4);
        buf.add(2.0);
        buf.add(4.0);
        buf.add(6.0);
        assertEquals(4.0, buf.average(), DELTA);
    }

    @Test
    public void averageOfEmptyBufferIsZero() {
        final SampleBufferD buf = new SampleBufferD(3);
        assertEquals(0D, buf.average(), DELTA);
    }

    // ---------------------------------------------------------------
    // isSameValues()
    // ---------------------------------------------------------------

    @Test
    public void isSameValuesFalseWhenEmpty() {
        final SampleBufferD buf = new SampleBufferD(3);
        assertFalse(buf.isSameValues());
    }

    @Test
    public void isSameValuesFalseWithOneElement() {
        final SampleBufferD buf = new SampleBufferD(3);
        buf.add(5.0);
        assertFalse(buf.isSameValues());
    }

    @Test
    public void isSameValuesTrueWhenAllEqual() {
        final SampleBufferD buf = new SampleBufferD(4);
        buf.add(7.0);
        buf.add(7.0);
        buf.add(7.0);
        assertTrue(buf.isSameValues());
    }

    @Test
    public void isSameValuesFalseWhenValuesDiffer() {
        final SampleBufferD buf = new SampleBufferD(4);
        buf.add(7.0);
        buf.add(7.0);
        buf.add(8.0);
        assertFalse(buf.isSameValues());
    }

    @Test
    public void isSameValuesAfterWraparound() {
        final SampleBufferD buf = new SampleBufferD(2);
        buf.add(1.0);
        buf.add(9.0);
        buf.add(9.0); // overwrites 1.0, buffer now holds [9.0, 9.0]
        assertTrue(buf.isSameValues());
    }

}
