package fr.alexdoru.megawallsenhancementsmod.hackerdetector.data;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class BrokenBlock {

    public final Block block;
    public final BlockPos blockPos;
    public final long breakTime;

    public BrokenBlock(Block block, BlockPos blockPos, long breakTime) {
        this.block = block;
        this.blockPos = blockPos;
        this.breakTime = breakTime;
    }

}
