package me.gravityio.flair.condition;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

public enum ItemVariableType implements VariableType<ItemStack> {
    ITEM_DISPLAY_NAME("DisplayName", str -> str, ItemStack::getDisplayName),
    ITEM_ID("Id", str -> str, stack -> GameData.getItemRegistry().getNameForObject(stack.getItem())),
    ITEM_DAMAGE("Damage", Integer::parseInt, ItemStack::getItemDamage),
    IS_BLOCK_ITEM("IsBlock", Boolean::parseBoolean, stack -> stack.getItem() instanceof ItemBlock);

    private final String str;
    private final Function<ItemStack, Object> valueGetter;
    private final Function<String, Object> typeConverter;

    ItemVariableType(String str, Function<String, Object> typeGetter, Function<ItemStack, Object> valueGetter) {
        this.str = "$" + str;
        this.typeConverter = typeGetter;
        this.valueGetter = valueGetter;
    }

    @Override
    public Object getValue(ItemStack stack) {
        return this.valueGetter.apply(stack);
    }

    @Override
    public Object convert(String str) {
        return this.typeConverter.apply(str);
    }

    public static VariableType<ItemStack> fromString(String str) {
        for (ItemVariableType type : values()) {
            if (type.str.equals(str)) return type;
        }
        return null;
    }
}
