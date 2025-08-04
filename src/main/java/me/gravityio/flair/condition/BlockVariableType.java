package me.gravityio.flair.condition;

import cpw.mods.fml.common.registry.GameData;
import me.gravityio.flair.BlockInstance;
import me.gravityio.flair.Flair;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.util.MovingObjectPosition;

import java.util.function.Function;

public enum BlockVariableType implements VariableType<BlockInstance> {
    BLOCK_DISPLAY_NAME("DisplayName", str -> str, blockInstance -> ItemVariableType.ITEM_DISPLAY_NAME.getValue(Flair.getIdentifierStack(blockInstance))),
    BLOCK_ID("Id", str -> str, block -> GameData.getBlockRegistry().getNameForObject(block));

    private final String str;
    private final Function<BlockInstance, Object> valueGetter;
    private final Function<String, Object> typeConverter;

    BlockVariableType(String str, Function<String, Object> typeConverter, Function<BlockInstance, Object> valueGetter) {
        this.str = "$" + str;
        this.typeConverter = typeConverter;
        this.valueGetter = valueGetter;
    }

    @Override
    public Object getValue(BlockInstance block) {
        return this.valueGetter.apply(block);
    }

    @Override
    public Object convert(String str) {
        return this.typeConverter.apply(str);
    }

    public static VariableType<BlockInstance> fromString(String str) {
        for (BlockVariableType type : values()) {
            if (type.str.equals(str)) return type;
        }
        return null;
    }
}
