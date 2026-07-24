package fr.alexdoru.mwe.hackerdetector.data;

import net.minecraft.util.BlockPos;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TickingBlockMapTest {

    private static final int MAX_TICK = 20;
    private TickingBlockMap tickingBlockMap;

    @Before
    public void setUp() {
        tickingBlockMap = new TickingBlockMap(MAX_TICK);
    }

    private void tick(int times) {
        for (int i = 0; i < times; i++) {
            tickingBlockMap.onTick();
        }
    }

    @Test
    public void testInitialStateIsEmpty() {
        assertEquals("0/0", tickingBlockMap.size());
    }

    @Test
    public void testAddIncreasesSize() {
        tickingBlockMap.add(new BlockPos(1, 2, 3));
        assertEquals("1/1", tickingBlockMap.size());
    }

    @Test
    public void testContainsReturnsTrueAfterAdd() {
        final BlockPos pos = new BlockPos(1, 2, 3);
        tickingBlockMap.add(pos);
        assertTrue(tickingBlockMap.contains(pos));
    }

    @Test
    public void testContainsReturnsFalseWhenNotAdded() {
        final BlockPos pos = new BlockPos(1, 2, 3);
        assertFalse(tickingBlockMap.contains(pos));
    }

    @Test
    public void testContainsReturnsFalseForDifferentPos() {
        tickingBlockMap.add(new BlockPos(1, 2, 3));
        assertFalse(tickingBlockMap.contains(new BlockPos(4, 5, 6)));
    }

    @Test
    public void testAddingSamePosTwiceIncreasesDequeButNotMapSize() {
        final BlockPos pos = new BlockPos(1, 2, 3);
        tickingBlockMap.add(pos);
        tickingBlockMap.add(pos);
        assertEquals("1/2", tickingBlockMap.size());
        assertTrue(tickingBlockMap.contains(pos));
    }

    @Test
    public void testAddingDifferentPosIncreasesMapSize() {
        tickingBlockMap.add(new BlockPos(1, 2, 3));
        tickingBlockMap.add(new BlockPos(4, 5, 6));
        assertEquals("2/2", tickingBlockMap.size());
    }

    @Test
    public void testOnTickDoesNotEvictBeforeMaxTickExpires() {
        final BlockPos pos = new BlockPos(1, 2, 3);
        tickingBlockMap.add(pos);

        // At tickTime == MAX_TICK the entry is still valid (>= comparison keeps it)
        tick(MAX_TICK);

        assertTrue(tickingBlockMap.contains(pos));
        assertEquals("1/1", tickingBlockMap.size());
    }

    @Test
    public void testOnTickEvictsAfterMaxTickExpires() {
        final BlockPos pos = new BlockPos(1, 2, 3);
        tickingBlockMap.add(pos);

        // One extra tick beyond MAX_TICK triggers eviction
        tick(MAX_TICK + 1);

        assertFalse(tickingBlockMap.contains(pos));
        assertEquals("0/0", tickingBlockMap.size());
    }

    @Test
    public void testOnTickEvictsOnlyExpiredEntries() {
        final BlockPos pos1 = new BlockPos(1, 2, 3);
        final BlockPos pos2 = new BlockPos(4, 5, 6);

        tickingBlockMap.add(pos1); // placed at tickTime = 0
        tick(1);                  // tickTime becomes 1
        tickingBlockMap.add(pos2); // placed at tickTime = 1

        // Advance so pos1 (placed at 0) expires but pos2 (placed at 1) does not
        tick(MAX_TICK);

        assertFalse(tickingBlockMap.contains(pos1));
        assertTrue(tickingBlockMap.contains(pos2));
        assertEquals("1/1", tickingBlockMap.size());
    }

    @Test
    public void testOnTickEvictsAllExpiredEntriesInSingleCall() {
        final BlockPos pos1 = new BlockPos(1, 2, 3);
        final BlockPos pos2 = new BlockPos(4, 5, 6);
        final BlockPos pos3 = new BlockPos(7, 8, 9);

        tickingBlockMap.add(pos1);
        tickingBlockMap.add(pos2);
        tickingBlockMap.add(pos3);

        tick(MAX_TICK + 1);

        assertFalse(tickingBlockMap.contains(pos1));
        assertFalse(tickingBlockMap.contains(pos2));
        assertFalse(tickingBlockMap.contains(pos3));
        assertEquals("0/0", tickingBlockMap.size());
    }

    @Test
    public void testDuplicatePosDecrementsCountInsteadOfRemovingOnFirstExpiry() {
        final BlockPos pos = new BlockPos(1, 2, 3);

        tickingBlockMap.add(pos); // placed at tick 0
        tick(1);                 // tickTime = 1
        tickingBlockMap.add(pos); // placed at tick 1

        // Advance so only the first occurrence (placed at 0) expires
        tick(MAX_TICK);

        // Still present because the second occurrence hasn't expired yet
        assertTrue(tickingBlockMap.contains(pos));
        assertEquals("1/1", tickingBlockMap.size());
    }

    @Test
    public void testDuplicatePosFullyRemovedWhenBothEntriesExpire() {
        final BlockPos pos = new BlockPos(1, 2, 3);

        tickingBlockMap.add(pos);
        tickingBlockMap.add(pos);

        tick(MAX_TICK + 1);

        assertFalse(tickingBlockMap.contains(pos));
        assertEquals("0/0", tickingBlockMap.size());
    }

    @Test
    public void testOnTickWithEmptyMapDoesNotThrow() {
        tick(50);
        assertEquals("0/0", tickingBlockMap.size());
    }

    @Test
    public void testSizeFormatMatchesMapAndDequeCounts() {
        tickingBlockMap.add(new BlockPos(0, 0, 0));
        tickingBlockMap.add(new BlockPos(1, 1, 1));
        tickingBlockMap.add(new BlockPos(1, 1, 1));

        assertEquals("2/3", tickingBlockMap.size());
    }

}