package me.gravityio.flair.condition;

import cpw.mods.fml.common.registry.GameData;
import me.gravityio.flair.Flair;
import me.gravityio.flair.FlairConfig;
import me.gravityio.flair.util.IntRef;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

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

    public static VariableType getVariableType(String variable) throws ConfigParseException {
        return switch (variable) {
            case "$Id" -> VariableType.ITEM_ID;
            case "$DisplayName" -> VariableType.ITEM_DISPLAY_NAME;
            case "$Damage" -> VariableType.ITEM_DAMAGE;
            case "$IsBlock" -> VariableType.IS_BLOCK_ITEM;
            default -> throw new ConfigParseException("Unknown variable type: " + variable);
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

    public static String[] split(String line) {
        int quoteIndex = line.indexOf("\"");
        if (quoteIndex == -1) {
            return line.split(" ");
        }
        String[] arr = new String[2];
        arr[0] = line.substring(0, quoteIndex);
        arr[1] = line.substring(quoteIndex + 1);
        return arr;
    }

    public static void parseLines(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("#") || line.isEmpty()) continue;
            String[] args = line.split(" ");
            String command = args[0].toLowerCase();
            try {
                switch (command) {
                    case "volume" -> {
                        info("Adding volume condition...", i);
                        if (args.length == 1) {
                            error("Expected a value...", i);
                            return;
                        }
                        int volume = Integer.parseInt(args[1]);
                        if (volume < 0 || volume > 100) {
                            error("Volume must be between 0 and 100...", i);
                            return;
                        }
                        FlairConfig.CONFIG.VOLUME = volume;
                    }
                    case "default" -> {
                        info("Adding default condition...", i);
                        if (args.length == 1) {
                            error("Expected a sound to play...", i);
                            return;
                        }
                        String sound = args[1];
                        float volume = 1.0f;
                        float pitch = 1.0f;
                        if (args.length > 2) {
                            try {
                                volume = Float.parseFloat(args[2]);
                            } catch (NumberFormatException e) {
                                Flair.LOGGER.error("Volume must be a number...");
                                return;
                            }
                        }
                        if (args.length > 3) {
                            try {
                                pitch = Float.parseFloat(args[3]);
                            } catch (NumberFormatException e) {
                                Flair.LOGGER.error("Pitch must be a number...");
                                return;
                            }
                        }
                        FlairConfig.CONFIG.DEFAULT_SOUND = new SoundData(sound, volume, pitch);
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
                        Flair.LOGGER.error("Unknown command: " + command);
                    }
                }
            } catch (ConfigParseException e) {
                error(e.getMessage(), i);
                return;
            }
        }
    }

    public static void parseSet(String[] args) throws ConfigParseException {
        if (args.length == 1) {
            throw new ConfigParseException("Expected an item and a sound to play...");
        }
        String item = new ResourceLocation(args[1]).toString();
        if (!GameData.getItemRegistry().containsKey(item)) {
            throw new ConfigParseException(String.format("Item not found: %s", item));
        }
        if (args.length == 2) {
            throw new ConfigParseException("Expected either 'play' or 'playblocksound'...");
        }

        FlairConfig.CONFIG.ITEM_SOUNDS.put(item, parseSoundGenerator(Arrays.copyOfRange(args, 2, args.length)));
    }

    public static ConditionalExpression parseIfExpression(String[] args, IntRef index) throws ConfigParseException {
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
        return new ConditionalExpression(variable, condition, argument);
    }

    public static ItemCondition parseIf(String[] args) throws ConfigParseException {
        IntRef index = new IntRef(0);
        if (args[0].equals("if")) index.value++;
        Expression main = parseIfExpression(args, index);
        if (main == null) return null;
        while (true) {
            if (index.value >= args.length) {
                throw new ConfigParseException("Expected and, or, play, etc...");
            }
            String arg = args[index.value].toLowerCase();
            if (arg.equals("and") || arg.equals("or")) {
                index.value++;
                Expression expression = parseIfExpression(args, index);
                BinaryOperator operator = arg.equals("and") ? BinaryOperator.AND : BinaryOperator.OR;
                main = new BinaryExpression(main, expression, operator);
            } else break;
        }

        return new ItemCondition(main, parseSoundGenerator(Arrays.copyOfRange(args, index.value, args.length)));
    }

    public static ISoundGenerator parseSoundGenerator(String[] args) throws ConfigParseException {
        return switch(args[0].toLowerCase()) {
            case "play" -> {
                if (args.length == 1) {
                    throw new ConfigParseException("Expected a sound to play...");
                }
                String sound = args[1];
                float volume = 1.0f;
                float pitch = 1.0f;
                if (args.length > 2) {
                    try {
                        volume = Float.parseFloat(args[2]);
                    } catch (NumberFormatException e) {
                        throw new ConfigParseException("Volume must be a number...");
                    }
                }
                if (args.length > 3) {
                    try {
                        pitch = Float.parseFloat(args[3]);
                    } catch (NumberFormatException e) {
                        throw new ConfigParseException("Pitch must be a number...");
                    }
                }

                yield new NormalSoundGenerator(sound, volume, pitch);
            }
            case "playblocksound" -> {
                if (args.length == 1) {
                    throw new ConfigParseException("Expected a sound type to play...");
                }
                String soundTypeStr = args[1];
                BlockSoundType soundType = switch (soundTypeStr) {
                    case "break" -> soundType = BlockSoundType.BREAK;
                    case "step" -> soundType = BlockSoundType.STEP;
                    default -> null;
                };

                if (soundType == null) {
                    throw new ConfigParseException(String.format("Unknown sound type: %s", soundTypeStr));
                }

                float volume = 1.0f;
                float pitch = 1.0f;
                if (args.length > 2) {
                    try {
                        volume = Float.parseFloat(args[2]);
                    } catch (NumberFormatException e) {
                        throw new ConfigParseException("Volume must be a number...");
                    }
                }
                if (args.length > 3) {
                    try {
                        pitch = Float.parseFloat(args[3]);
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
