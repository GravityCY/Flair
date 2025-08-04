package me.gravityio.flair;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class BlockInstance {
    public Block block;
    public World world;
    public int meta;
    public int x;
    public int y;
    public int z;

    public BlockInstance(Block block, World world, int meta, int x, int y, int z) {
        this.block = block;
        this.meta = meta;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
