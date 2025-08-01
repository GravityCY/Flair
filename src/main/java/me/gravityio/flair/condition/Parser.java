package me.gravityio.flair.condition;

import me.gravityio.flair.Flair;
import me.gravityio.flair.FlairConfig;
import me.gravityio.flair.util.IntRef;

public class Parser {

    public static VariableType getVariableType(String variable) {
        return switch (variable) {
            case "$Id" -> VariableType.ITEM_ID;
            case "$DisplayName" -> VariableType.ITEM_DISPLAY_NAME;
            case "$Damage" -> VariableType.ITEM_DAMAGE;
            default -> null;
        };
    }

    public static CompareMethod getConditionType(String condition) {
        return switch (condition) {
            case "is" -> CompareMethod.EQUALS;
            case "isnt" -> CompareMethod.NEQUALS;
            case "contains" -> CompareMethod.CONTAINS;
            default -> null;
        };
    }

    public static ItemExpression parseExpression(String[] args, IntRef index) {
        if (index.value >= args.length) {
            Flair.LOGGER.error("Expected a variable type... <variable> <comparison> <argument>");
            return null;
        }
        VariableType variable = getVariableType(args[index.value++]);
        if (index.value >= args.length) {
            Flair.LOGGER.error("Expected a comparison method...");
            return null;
        }
        CompareMethod condition = getConditionType(args[index.value++]);
        if (index.value >= args.length) {
            Flair.LOGGER.error("Expected an argument...");
            return null;
        }
        String argument = args[index.value++];
        return new ItemExpression(variable, condition, argument);
    }

    public static void parseLines(String[] lines) {
        for (String s : lines) {
            String line = s.trim();
            if (line.startsWith("#")) continue;
            String[] args = line.split(" ");
            if (args[0].equals("if")) {
                Flair.LOGGER.info("Adding if condition...");
                FlairConfig.CONFIG.CONDITIONS.add(parseIf(args));
            } /*else if (args[0].equals("item")) {
                FlairConfig.CONFIG.put();
            }*/
        }
    }

    public static ItemCondition parseIf(String[] args) {
        IntRef index = new IntRef(0);
        if (args[0].equals("if")) index.value++;
        Expression main = parseExpression(args, index);
        if (main == null) return null;
        while (true) {
            if (index.value >= args.length) {
                Flair.LOGGER.error("Expected and, or, play...");
                return null;
            }
            String arg = args[index.value].toLowerCase();
            if (arg.equals("and") || arg.equals("or")) {
                index.value++;
                Expression expression = parseExpression(args, index);
                BinaryOperator operator = arg.equals("and") ? BinaryOperator.AND : BinaryOperator.OR;
                main = new BinaryExpression(main, expression, operator);
            } else break;
        }

        if (!args[index.value++].equalsIgnoreCase("play")) {
            Flair.LOGGER.error("Expected 'play'...");
            return null;
        }
        if (index.value >= args.length) {
            Flair.LOGGER.error("Expected a sound to play...");
            return null;
        }
        String sound = args[index.value++];
        float volume = 1.0f;
        float pitch = 1.0f;
        if (index.value < args.length)
            volume = Float.parseFloat(args[index.value++]);
        if (index.value < args.length)
            pitch = Float.parseFloat(args[index.value++]);

        return new ItemCondition(main, sound, volume, pitch);
    }
}
