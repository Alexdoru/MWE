package fr.alexdoru.mwe.hackerdetector.data;

import net.minecraft.util.BlockPos;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class TickingBlockMap {

    private final int MAX_TICK;
    private int tickTime;
    private final Deque<BlockPlaced> deque = new ArrayDeque<>(128);
    private final Map<BlockPos, Integer> map = new HashMap<>(128);

    public TickingBlockMap(int maxTick) {
        MAX_TICK = maxTick;
    }

    public void add(BlockPos pos) {
        deque.add(new BlockPlaced(tickTime, pos));
        map.merge(pos, 1, Integer::sum);
    }

    public boolean contains(BlockPos pos) {
        return map.containsKey(pos);
    }

    public void onTick() {
        tickTime++;
        while (true) {
            final BlockPlaced block = deque.peekFirst();
            if (block == null || block.tickPlaced + MAX_TICK >= tickTime) {
                break;
            }
            deque.removeFirst();
            final Integer i = map.get(block.pos);
            if (i != null) {
                if (i == 1) {
                    map.remove(block.pos);
                } else {
                    map.put(block.pos, i - 1);
                }
            }
        }
    }

    public String size() {
        return map.size() + "/" + deque.size();
    }

    private static class BlockPlaced {

        final int tickPlaced;
        final BlockPos pos;

        public BlockPlaced(int tickPlaced, BlockPos pos) {
            this.tickPlaced = tickPlaced;
            this.pos = pos;
        }

    }

}
