package me.gravityio.flair.condition;

import cpw.mods.fml.common.registry.GameData;
import me.gravityio.flair.BlockInstance;
import me.gravityio.flair.Flair;
import me.gravityio.flair.FlairConfig;
import me.gravityio.flair.MetaLocation;
import me.gravityio.flair.util.ListPointer;
import me.gravityio.flair.util.StringUtils;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Parser {
    public static class ConfigParseException extends Exception {
        public ConfigParseException(String message, Object... args) {
            super(String.format(message, args));
        }

        public ConfigParseException(String message) {
            super(message);
        }
    }

    private static void info(String message, int lineIndex) {
        Flair.LOGGER.info("Info on line {}: {}", lineIndex + 1, message);
    }

    private static void error(String message, int lineIndex) {
        Flair.LOGGER.error("Error on line {}: {}", lineIndex + 1, message);
        Flair.sendMessage("Error on line %d: '%s'", lineIndex + 1, message);
    }

    public static void parseLines(String[] lines) {
        Flair.sendMessage("Reloading Config...");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("#") || line.isEmpty()) continue;
            List<String> argsStr = StringUtils.split(line);
            String command = argsStr.get(0).toLowerCase();
            ListPointer<String> args = new ListPointer<>(1, argsStr);
            try {
                switch (command) {
                    case "volume" -> {
                        info("Adding volume condition...", i);
                        parseVolume(args);
                    }
                    case "default" -> {
                        info("Adding default condition...", i);
                        parseDefault(args);
                    }
                    case "set" -> {
                        info("Adding set condition...", i);
                        parseSetGeneric(args);
                    }
                    case "if" -> {
                        info("Adding if condition...", i);
                        parseIfGeneric(args);
                    }
                    case "allowspam" -> FlairConfig.INSTANCE.ALLOW_SPAM = true;
                    default -> error(String.format("Unknown command: %s", command), i);
                }
            } catch (ConfigParseException e) {
                error(e.getMessage(), i);
                return;
            }
        }
    }

    private static void parseIfGeneric(ListPointer<String> args) throws ConfigParseException {
        if (args.isEnd()) {
            throw new ConfigParseException("Expected either 'item' or 'block'...");
        }
        String type = args.eat();
        IfType ifType = IfType.fromString(type);
        if (ifType == null) {
            throw new ConfigParseException(String.format("Unknown type: %s... expected 'item' or 'block", type));
        }

        switch (ifType) {
            case ITEM -> FlairConfig.INSTANCE.ITEM_CONDITIONS.add(parseItemIf(args));
            case BLOCK -> FlairConfig.INSTANCE.BLOCK_CONDITIONS.add(parseBlockIf(args));
        }
    }

    public static void parseSetGeneric(ListPointer<String> args) throws ConfigParseException {
        if (args.isEnd()) {
            throw new ConfigParseException("Expected either 'item' or 'block'...");
        }
        String type = args.eat();
        IfType ifType = IfType.fromString(type);
        if (ifType == null) {
            throw new ConfigParseException(String.format("Unknown type: %s... expected 'item' or 'block'", type));
        }

        switch (ifType) {
            case ITEM -> parseSetGeneric(
                    args, GameData.getItemRegistry()::containsKey,
                    args1 -> parseSoundGenerator(args1, ItemSoundGenerator::new),
                    FlairConfig.INSTANCE.ITEM_SOUNDS::put
            );
            case BLOCK -> parseSetGeneric(args, GameData.getItemRegistry()::containsKey,
                    args1 -> parseSoundGenerator(args1, BlockSoundGenerator::new),
                    FlairConfig.INSTANCE.BLOCK_SOUNDS::put
            );
        }
    }

    public static <T> void parseSetGeneric(ListPointer<String> args, Predicate<String> validator, SoundGeneratorParser<T> parser, BiConsumer<String, ISoundGenerator<T>> consumer) throws ConfigParseException {
        if (args.isEnd()) {
            throw new ConfigParseException("Expected an id...");
        }
        MetaLocation id = MetaLocation.parse(args.eat());
        if (!validator.test(id.toRegistry())) {
            throw new ConfigParseException(String.format("'%s' is not a valid object...", id));
        }
        consumer.accept(id.toString(), parser.parse(args));
    }

    public static <T> Expression<T> parseIfExpression(ListPointer<String> args, VariableTypeFactory<T> factory) throws ConfigParseException {
        if (args.isEnd()) {
            throw new ConfigParseException("Expected a variable type... <variable> <comparison> <argument>");
        }
        VariableType<T> variable = factory.create(args.eat());
        if (variable == null) throw new ConfigParseException("Unknown variable type: " + args.prev());
        if (args.isEnd()) {
            throw new ConfigParseException("Expected a comparison method...");
        }
        CompareMethod comparator = CompareMethod.fromString(args.eat());
        if (comparator == null) throw new ConfigParseException("Unknown comparison method: " + args.prev());
        if (!variable.isValidComparison(comparator)) {
            String valid = Arrays.stream(variable.getComparators()).map(m -> m.str).collect(Collectors.joining());
            throw new ConfigParseException("Invalid comparison method '%s', Variable '%s' can only accept '%s'",
                    comparator.str, variable.getSyntaxString(), valid);
        }
        if (args.isEnd()) {
            throw new ConfigParseException("Expected an argument...");
        }
        Object obj = variable.convert(args.eat());
        if (obj == null) throw new ConfigParseException("Unknown argument type: " + args.prev());
        return new IfExpression<>(variable, comparator, obj);
    }

    public static void parseVolume(ListPointer<String> args) throws ConfigParseException {
        if (args.isEnd()) {
            throw new ConfigParseException("Expected a value...");
        }
        int volume = Integer.parseInt(args.eat());
        if (volume < 0 || volume > 100) {
            throw new ConfigParseException("Volume must be between 0 and 100...");
        }
        FlairConfig.INSTANCE.VOLUME = volume;
    }

    public static void parseDefault(ListPointer<String> args) throws ConfigParseException {
        if (args.isEnd()) {
            throw new ConfigParseException("Expected a sound to play...");
        }
        String sound = args.eat();
        float volume = args.hasNext() ? parseFloat(args) : 1.0f;
        float pitch = args.hasNext() ? parseFloat(args) : 1.0f;
        FlairConfig.INSTANCE.DEFAULT_SOUND = new SoundData(sound, volume, pitch);
    }

    public static <T> SoundCondition<T> parseIf(ListPointer<String> args, ExpressionParser<T> expressionFactory, SoundGeneratorParser<T> soundGeneratorParser) throws ConfigParseException {
        if (args.peek().equals("if")) args.skip();
        Expression<T> main = expressionFactory.parse(args);
        while (true) {
            if (args.isEnd()) {
                throw new ConfigParseException("Expected and, or, play, etc...");
            }
            String arg = args.peek().toLowerCase();
            if (arg.equals("and") || arg.equals("or")) {
                args.skip();
                Expression<T> expression = expressionFactory.parse(args);
                main = new BinaryExpression<>(main, expression, BinaryOperator.fromString(arg));
            } else break;
        }

        return new SoundCondition<>(main, soundGeneratorParser.parse(args));
    }

    public static SoundCondition<ItemStack> parseItemIf(ListPointer<String> args) throws ConfigParseException {
        return parseIf(args, args1 -> parseIfExpression(args1, ItemVariableType::fromString),
                args1 -> parseSoundGenerator(args1, ItemSoundGenerator::new));
    }

    public static SoundCondition<BlockInstance> parseBlockIf(ListPointer<String> args) throws ConfigParseException {
        return parseIf(args, args1 -> parseIfExpression(args1, BlockVariableType::fromString),
                args1 -> parseSoundGenerator(args1, BlockSoundGenerator::new));
    }

    public static float parseFloat(ListPointer<String> args) throws ConfigParseException {
        try {
            return Float.parseFloat(args.eat());
        } catch (NumberFormatException e) {
            throw new ConfigParseException("Expected a number... " + args.prev());
        }
    }

    public static <T> ISoundGenerator<T> parseSoundGenerator(ListPointer<String> args, SoundGeneratorFactory<T> blockSoundFactory) throws ConfigParseException {
        if (args.isEnd()) {
            throw new ConfigParseException("Expected either 'play' or 'playblocksound'...");
        }

        SoundGeneratorType type = SoundGeneratorType.fromString(args.eat());
        if (type == null) throw new ConfigParseException("Unknown sound generator type: %s", args.prev());

        return switch (type) {
            case PLAY -> {
                if (args.isEnd()) {
                    throw new ConfigParseException("Expected a sound to play...");
                }
                String sound = args.eat();
                float volume = args.hasNext() ? parseFloat(args) : 1.0f;
                float pitch = args.hasNext() ? parseFloat(args) : 1.0f;
                yield new NormalSoundGenerator<>(sound, volume, pitch);
            }
            case PLAYBLOCKSOUND -> {
                if (args.isEnd()) {
                    throw new ConfigParseException("Expected a sound type to play...");
                }
                String soundTypeStr = args.eat();
                BlockSoundType soundType = BlockSoundType.fromString(soundTypeStr);
                if (soundType == null) {
                    throw new ConfigParseException("Unknown sound type: '%s'... either 'step' or 'break", soundTypeStr);
                }

                float volume = args.hasNext() ? parseFloat(args) : 1.0f;
                float pitch = args.hasNext() ? parseFloat(args) : 1.0f;
                yield blockSoundFactory.create(soundType, volume, pitch);
            }
        };
    }

    public interface ExpressionParser<T> {
        Expression<T> parse(ListPointer<String> args) throws ConfigParseException;
    }

    public interface SoundGeneratorParser<T> {
        ISoundGenerator<T> parse(ListPointer<String> args) throws ConfigParseException;
    }

    public interface SoundGeneratorFactory<T> {
        ISoundGenerator<T> create(BlockSoundType type, float volume, float pitch) throws ConfigParseException;
    }

    public interface VariableTypeFactory<T> {
        VariableType<T> create(String str);
    }
}
