package me.gravityio.flair.config;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.gravityio.flair.Flair;
import me.gravityio.flair.SoundRegistries;
import me.gravityio.flair.condition.sound.ISoundGenerator;
import net.minecraft.item.ItemStack;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SideOnly(Side.CLIENT)
public class FlairConfig {
    public static final String CONFIG_FILENAME = "flair.config";
    public static final String SOUND_LOG_NAME = "sounds.log";
    public static final String SCREEN_LOG_NAME = "screens.log";


    public static FlairConfig INSTANCE;
    public static File CONFIG_DIRECTORY;
    public static File CONFIG_FILE;

    public boolean ALLOW_SPAM = false;
    public int VOLUME = 100;
    public ISoundGenerator<ItemStack> HOTBAR_SOUND;
    public ISoundGenerator<Object> TYPING_SOUND;
    public ISoundGenerator<Object> INVENTORY_SOUND;

    public static void init(File configDirectory) {
        INSTANCE = new FlairConfig();
        CONFIG_DIRECTORY = new File(configDirectory, "flair");

        //noinspection ResultOfMethodCallIgnored
        CONFIG_DIRECTORY.mkdirs();
        CONFIG_FILE = new File(CONFIG_DIRECTORY, CONFIG_FILENAME);
    }

    public static void loadFirst() {
        if (copyDefaultConfigs()) load();
    }

    public static void copyResources(String resourceFolder, File toDirectory, String... resourceNames ) {
        for (String resourceName : resourceNames) {
            String resourcePath = resourceFolder + "/" + resourceName;
            File output = new File(toDirectory, resourceName);
            if (Files.exists(output.toPath())) continue;

            try (InputStream in = Flair.class.getResourceAsStream(resourcePath)) {
                if (in == null) {
                    throw new FileNotFoundException("Resource not found: " + resourceName);
                }
                Flair.LOGGER.info("Copying {}", resourcePath);
                Files.copy(in, output.toPath());
            } catch (IOException e) {
                Flair.LOGGER.error("Failed to copy {}", resourcePath, e);
            }
        }
    }

    public static boolean copyDefaultConfigs() {
        boolean exists = CONFIG_FILE.exists();
        copyResources("/configs", CONFIG_DIRECTORY, "flair.config", "gtnh.config");
        return !exists;
    }

    public static void load() {
        copyDefaultConfigs();

        try {
            INSTANCE.ALLOW_SPAM = false;
            INSTANCE.VOLUME = 100;
            INSTANCE.HOTBAR_SOUND = null;
            INSTANCE.TYPING_SOUND = null;
            INSTANCE.INVENTORY_SOUND = null;

            SoundRegistries.INSTANCE.reset();
            ConfigParser.parseLines(Files.readAllLines(CONFIG_FILE.toPath()).toArray(new String[0]));
        } catch (IOException e) {
            Flair.LOGGER.error("Failed to load flair config", e);
        }
    }
}
