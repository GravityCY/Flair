package me.gravityio.flair.data;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockInstance {
    public final Block block;
    public final World world;
    public final TileEntity tileEntity;
    public final int meta;
    public final int x;
    public final int y;
    public final int z;

    public static BlockInstance create(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block == null) return null;
        return new BlockInstance(block, world, world.getTileEntity(x, y, z), world.getBlockMetadata(x, y, z), x, y, z);
    }

    public BlockInstance(@Nullable Block block, World world, TileEntity te, int meta, int x, int y, int z) {
        this.block = block;
        this.world = world;
        this.tileEntity = te;
        this.meta = meta;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
