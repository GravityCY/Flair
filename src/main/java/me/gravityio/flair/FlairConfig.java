package me.gravityio.flair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.gravityio.flair.condition.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class FlairConfig {
    public static final String CONFIG_PATH = "flair.config";

    public static FlairConfig CONFIG;
    public static File CONFIG_DIRECTORY;
    public static File CONFIG_FILE;

    public int VOLUME = 100;
    public SoundData DEFAULT_SOUND = new SoundData("random.pop");
    public Map<String, ISoundGenerator> ITEM_SOUNDS = new HashMap<>();
    public List<ItemCondition> CONDITIONS = new ArrayList<>();

    public static void init(File configDirectory) {
        CONFIG = new FlairConfig();
        CONFIG_DIRECTORY = new File(configDirectory, "flair");
        CONFIG_DIRECTORY.mkdirs();
        CONFIG_FILE = new File(CONFIG_DIRECTORY, CONFIG_PATH);
    }

    private static void copyDefaultConfig() {
        if (Files.exists(CONFIG_FILE.toPath())) return;

        try (InputStream in = Flair.class.getResourceAsStream("/flair.config")) {
            if (in == null) {
                throw new FileNotFoundException("Resource not found: flair.config");
            }
            Flair.LOGGER.info("Copying flair config");
            Files.copy(in, CONFIG_FILE.toPath());
        } catch (IOException e) {
            Flair.LOGGER.error("Failed to copy flair config", e);
        }
    }

    public static void load() {
        copyDefaultConfig();

        try {
            CONFIG.CONDITIONS.clear();
            CONFIG.ITEM_SOUNDS.clear();
            Parser.parseLines(Files.readAllLines(CONFIG_FILE.toPath()).toArray(new String[0]));
        } catch (IOException e) {
            Flair.LOGGER.error("Failed to load flair config", e);
        }
    }
}
