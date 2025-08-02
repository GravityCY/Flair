package me.gravityio.flair.condition;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ConditionalExpression implements Expression {
    private final VariableType variable;
    private final CompareMethod compareMethod;
    private final String argument;

    public ConditionalExpression(VariableType variable, CompareMethod compareMethod, String argument) {
        this.variable = variable;
        this.compareMethod = compareMethod;
        this.argument = argument.toLowerCase();
    }

    public boolean check(ItemStack stack) {
        return switch (this.variable) {
            case ITEM_ID -> switch (this.compareMethod) {
                case EQUALS -> stack.getUnlocalizedName().equals(this.argument);
                case NEQUALS -> !stack.getUnlocalizedName().equals(this.argument);
                default -> false;
            };
            case ITEM_DISPLAY_NAME -> switch (this.compareMethod) {
                case CONTAINS -> stack.getDisplayName().toLowerCase().contains(this.argument);
                case EQUALS -> stack.getDisplayName().toLowerCase().equals(this.argument);
                case NEQUALS -> !stack.getDisplayName().toLowerCase().equals(this.argument);
            };
            case ITEM_DAMAGE -> switch (this.compareMethod) {
                case EQUALS -> stack.getItemDamage() == Integer.parseInt(this.argument);
                case NEQUALS -> stack.getItemDamage() != Integer.parseInt(this.argument);
                default -> false;
            };
            case IS_BLOCK_ITEM -> switch (this.compareMethod) {
                case EQUALS -> stack.getItem() instanceof ItemBlock == Boolean.parseBoolean(this.argument);
                case NEQUALS -> stack.getItem() instanceof ItemBlock != Boolean.parseBoolean(this.argument);
                default -> false;
            };
        };
    }
}
