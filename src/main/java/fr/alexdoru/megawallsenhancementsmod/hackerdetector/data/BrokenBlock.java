package fr.alexdoru.megawallsenhancementsmod.hackerdetector.data;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BrokenBlock {

    public final Block block;
    public final BlockPos blockPos;
    public final long breakTime;
    public List<EntityPlayer> playersList = null;

    public BrokenBlock(Block block, BlockPos blockPos, long breakTime) {
        this.block = block;
        this.blockPos = blockPos;
        this.breakTime = breakTime;
    }

    public void addPlayer(EntityPlayer player) {
        if (this.playersList == null) {
            this.playersList = new ArrayList<>();
        }
        this.playersList.add(player);
    }

}
