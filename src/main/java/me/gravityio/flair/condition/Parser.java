package me.gravityio.flair.condition;

import cpw.mods.fml.common.registry.GameData;
import me.gravityio.flair.Flair;
import me.gravityio.flair.FlairConfig;
import me.gravityio.flair.util.ListPointer;
import me.gravityio.flair.util.StringUtils;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class Parser {
    public static class ConfigParseException extends Exception {
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
                        parseSet(args);
                    }
                    case "if" -> {
                        info("Adding if condition...", i);
                        FlairConfig.CONFIG.CONDITIONS.add(parseIf(args));
                    }
                    default -> {
                        error(String.format("Unknown command: %s", command), i);
                    }
                }
            } catch (ConfigParseException e) {
                error(e.getMessage(), i);
                return;
            }
        }
    }

    public static void parseSet(ListPointer<String> args) throws ConfigParseException {
        if (args.isEnd()) {
            throw new ConfigParseException("Expected an item and a sound to play...");
        }
        String item = new ResourceLocation(args.eat()).toString();
        if (!GameData.getItemRegistry().containsKey(item)) {
            throw new ConfigParseException(String.format("Item not found: %s", item));
        }
        if (args.isEnd()) {
            throw new ConfigParseException("Expected either 'play' or 'playblocksound'...");
        }

        FlairConfig.CONFIG.ITEM_SOUNDS.put(item, parseSoundGenerator(args));
    }

    public static ConditionalExpression parseIfExpression(ListPointer<String> args) throws ConfigParseException {
        if (args.isEnd()) {
            throw new ConfigParseException("Expected a variable type... <variable> <comparison> <argument>");
        }
        VariableType variable = VariableType.fromString(args.eat());
        if (variable == null) throw new ConfigParseException("Unknown variable type: " + args.prev());
        if (args.isEnd()) {
            throw new ConfigParseException("Expected a comparison method...");
        }
        CompareMethod condition = CompareMethod.fromString(args.eat());
        if (condition == null) throw new ConfigParseException("Unknown comparison method: " + args.prev());
        if (args.isEnd()) {
            throw new ConfigParseException("Expected an argument...");
        }
        Object obj = variable.convert(args.eat());
        if (obj == null) throw new ConfigParseException("Unknown argument type: " + args.prev());
        return new ConditionalExpression(variable, condition, obj);
    }

    public static void parseVolume(ListPointer<String> args) throws ConfigParseException {
        if (args.isEnd()) {
            throw new ConfigParseException("Expected a value...");
        }
        int volume = Integer.parseInt(args.eat());
        if (volume < 0 || volume > 100) {
            throw new ConfigParseException("Volume must be between 0 and 100...");
        }
        FlairConfig.CONFIG.VOLUME = volume;
    }

    public static void parseDefault(ListPointer<String> args) throws ConfigParseException {
        if (args.isEnd()) {
            throw new ConfigParseException("Expected a sound to play...");
        }
        String sound = args.eat();
        float volume = 1.0f;
        float pitch = 1.0f;
        if (args.hasNext()) {
            try {
                volume = Float.parseFloat(args.eat());
            } catch (NumberFormatException e) {
                throw new ConfigParseException("Volume must be a number...");
            }
        }
        if (args.hasNext()) {
            try {
                pitch = Float.parseFloat(args.eat());
            } catch (NumberFormatException e) {
                throw new ConfigParseException("Pitch must be a number...");
            }
        }
        FlairConfig.CONFIG.DEFAULT_SOUND = new SoundData(sound, volume, pitch);
    }

    public static ItemCondition parseIf(ListPointer<String> args) throws ConfigParseException {
        if (args.peek().equals("if")) args.skip();
        Expression main = parseIfExpression(args);
        while (true) {
            if (args.isEnd()) {
                throw new ConfigParseException("Expected and, or, play, etc...");
            }
            String arg = args.peek().toLowerCase();
            if (arg.equals("and") || arg.equals("or")) {
                args.skip();
                Expression expression = parseIfExpression(args);
                BinaryOperator operator = arg.equals("and") ? BinaryOperator.AND : BinaryOperator.OR;
                main = new BinaryExpression(main, expression, operator);
            } else break;
        }

        return new ItemCondition(main, parseSoundGenerator(args));
    }

    public static ISoundGenerator parseSoundGenerator(ListPointer<String> args) throws ConfigParseException {
        return switch(args.peek().toLowerCase()) {
            case "play" -> {
                if (args.isEnd()) {
                    throw new ConfigParseException("Expected a sound to play...");
                }
                args.skip();
                String sound = args.eat();
                float volume = 1.0f;
                float pitch = 1.0f;
                if (args.hasNext()) {
                    try {
                        volume = Float.parseFloat(args.eat());
                    } catch (NumberFormatException e) {
                        throw new ConfigParseException("Volume must be a number...");
                    }
                }
                if (args.hasNext()) {
                    try {
                        pitch = Float.parseFloat(args.eat());
                    } catch (NumberFormatException e) {
                        throw new ConfigParseException("Pitch must be a number...");
                    }
                }

                yield new NormalSoundGenerator(sound, volume, pitch);
            }
            case "playblocksound" -> {
                if (args.isEnd()) {
                    throw new ConfigParseException("Expected a sound type to play...");
                }
                args.skip();
                String soundTypeStr = args.eat();
                BlockSoundType soundType = BlockSoundType.fromString(soundTypeStr);
                if (soundType == null) {
                    throw new ConfigParseException(String.format("Unknown sound type: %s", soundTypeStr));
                }

                float volume = 1.0f;
                float pitch = 1.0f;
                if (args.hasNext()) {
                    try {
                        volume = Float.parseFloat(args.eat());
                    } catch (NumberFormatException e) {
                        throw new ConfigParseException("Volume must be a number...");
                    }
                }
                if (args.hasNext()) {
                    try {
                        pitch = Float.parseFloat(args.eat());
                    } catch (NumberFormatException e) {
                        throw new ConfigParseException("Pitch must be a number...");
                    }
                }
                yield new BlockSoundGenerator(soundType, volume, pitch);
            }
            default -> {
                throw new ConfigParseException("Expected either 'play' or 'playblocksound'...");
            }
        };
    }
}
