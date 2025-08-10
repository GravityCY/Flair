package me.gravityio.flair.condition.variable;

import cpw.mods.fml.common.registry.GameData;
import me.gravityio.flair.condition.CompareMethod;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.function.Function;

public enum ItemVariableType implements VariableType<ItemStack> {

    ITEM_DISPLAY_NAME("DisplayName", str -> str, ItemStack::getDisplayName, CompareMethod.CONTAINS,
            CompareMethod.MATCHES, CompareMethod.EQUALS, CompareMethod.NEQUALS),

    ITEM_ID("Id", str -> str, stack -> GameData.getItemRegistry().getNameForObject(stack.getItem()),
            CompareMethod.CONTAINS, CompareMethod.MATCHES, CompareMethod.EQUALS, CompareMethod.NEQUALS),

    ITEM_OREDICT("OreDict", str -> str, ItemVariableType::getOreDictNames, CompareMethod.CONTAINS,
            CompareMethod.MATCHES, CompareMethod.EQUALS, CompareMethod.NEQUALS),

    ITEM_DAMAGE("Damage", Integer::parseInt, ItemStack::getItemDamage, CompareMethod.EQUALS, CompareMethod.NEQUALS),

    IS_BLOCK_ITEM("IsBlock", Boolean::parseBoolean, stack -> stack.getItem() instanceof ItemBlock, CompareMethod.EQUALS,
            CompareMethod.NEQUALS);

    public static String[] getOreDictNames(ItemStack stack) {
        int[] names = OreDictionary.getOreIDs(stack);
        String[] ret = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            ret[i] = OreDictionary.getOreName(names[i]);
        }
        return ret;
    }

    private final String str;
    private final Function<ItemStack, Object> valueGetter;
    private final Function<String, Object> typeConverter;
    private final CompareMethod[] compareMethods;

    ItemVariableType(String str, Function<String, Object> typeGetter, Function<ItemStack, Object> valueGetter, CompareMethod... compareMethods) {
        this.str = "$" + str;
        this.typeConverter = typeGetter;
        this.valueGetter = valueGetter;
        this.compareMethods = compareMethods;
    }

    @Override
    public String getSyntaxString() {
        return this.str;
    }

    @Override
    public Object getValue(ItemStack stack) {
        if (stack == null) return null;
        return this.valueGetter.apply(stack);
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

    public static VariableType<ItemStack> fromString(String str) {
        for (ItemVariableType type : values()) {
            if (type.str.equals(str)) return type;
        }
        return null;
    }
}
