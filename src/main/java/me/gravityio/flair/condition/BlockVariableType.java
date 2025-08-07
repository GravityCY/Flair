package me.gravityio.flair.condition;

import cpw.mods.fml.common.registry.GameData;
import me.gravityio.flair.BlockInstance;
import me.gravityio.flair.Flair;

import java.util.function.Function;

public enum BlockVariableType implements VariableType<BlockInstance> {

    BLOCK_DISPLAY_NAME(
            "DisplayName",
            str -> str,
            blockInstance -> ItemVariableType.ITEM_DISPLAY_NAME.getValue(Flair.getIdentifierStack(blockInstance)),
            CompareMethod.CONTAINS, CompareMethod.MATCHES, CompareMethod.EQUALS, CompareMethod.NEQUALS
    ),

    BLOCK_ID(
            "Id",
            str -> str,
            block -> GameData.getBlockRegistry().getNameForObject(block),
            CompareMethod.CONTAINS, CompareMethod.MATCHES, CompareMethod.EQUALS, CompareMethod.NEQUALS
    ),

    BLOCK_META(
            "Meta",
            Integer::parseInt,
            block -> block.meta,
            CompareMethod.EQUALS, CompareMethod.NEQUALS
    ),

    BLOCK_HAS_ENTITY(
            "HasEntity",
            Boolean::parseBoolean,
            block -> block.tileEntity != null,
            CompareMethod.EQUALS, CompareMethod.NEQUALS
    );

    private final String str;
    private final Function<BlockInstance, Object> valueGetter;
    private final Function<String, Object> typeConverter;
    private final CompareMethod[] compareMethods;

    BlockVariableType(String str, Function<String, Object> typeConverter, Function<BlockInstance, Object> valueGetter, CompareMethod... compareMethods) {
        this.str = "$" + str;
        this.typeConverter = typeConverter;
        this.valueGetter = valueGetter;
        this.compareMethods = compareMethods;
    }

    @Override
    public String getSyntaxString() {
        return this.str;
    }

    @Override
    public Object getValue(BlockInstance block) {
        return this.valueGetter.apply(block);
    }

    @Override
    public Object convert(String str) {
        return this.typeConverter.apply(str);
    }

    @Override
    public CompareMethod[] getComparators() {
        return this.compareMethods;
    }

    @Override
    public boolean isValidComparison(CompareMethod comparator) {
        for (CompareMethod type : this.compareMethods) {
            if (type.equals(comparator)) return true;
        }
        return false;
    }

    public static VariableType<BlockInstance> fromString(String str) {
        for (BlockVariableType type : values()) {
            if (type.str.equals(str)) return type;
        }
        return null;
    }
}
