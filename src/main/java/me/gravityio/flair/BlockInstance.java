package me.gravityio.flair;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockInstance {
    public final Block block;
    public final World world;
    public final TileEntity tileEntity;
    public final int meta;
    public final int x;
    public final int y;
    public final int z;

    public BlockInstance(Block block, World world, TileEntity te, int meta, int x, int y, int z) {
        this.block = block;
        this.world = world;
        this.tileEntity = te;
        this.meta = meta;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
