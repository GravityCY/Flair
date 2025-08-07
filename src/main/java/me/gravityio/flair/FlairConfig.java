package me.gravityio.flair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.gravityio.flair.condition.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ISoundGenerator<ItemStack> DEFAULT_SOUND;
    public ISoundGenerator<ItemStack> DEFAULT_CRAFTING_SOUND;
    public ISoundGenerator<?> DEFAULT_TYPING_SOUND;
    public ISoundGenerator<?> DEFAULT_INV_SOUND;
    public ISoundGenerator<ItemStack> DEFAULT_DROP_SOUND;
    public Map<String, ISoundGenerator<GuiScreen>> SCREEN_SOUNDS = new HashMap<>();
    public Map<String, ISoundGenerator<BlockInstance>> BLOCK_SOUNDS = new HashMap<>();
    public Map<String, ISoundGenerator<ItemStack>> ITEM_SOUNDS = new HashMap<>();
    public List<SoundCondition<ItemStack>> ITEM_CONDITIONS = new ArrayList<>();
    public List<SoundCondition<BlockInstance>> BLOCK_CONDITIONS = new ArrayList<>();

    public static void init(File configDirectory) {
        INSTANCE = new FlairConfig();
        CONFIG_DIRECTORY = new File(configDirectory, "flair");

        //noinspection ResultOfMethodCallIgnored
        CONFIG_DIRECTORY.mkdirs();
        CONFIG_FILE = new File(CONFIG_DIRECTORY, CONFIG_FILENAME);
    }

    public static void loadFirst() {
        if (copyDefaultConfig()) load();
    }

    public static boolean copyDefaultConfig() {
        if (Files.exists(CONFIG_FILE.toPath())) return false;

        try (InputStream in = Flair.class.getResourceAsStream("/flair.config")) {
            if (in == null) {
                throw new FileNotFoundException("Resource not found: flair.config");
            }
            Flair.LOGGER.info("Copying flair config");
            Files.copy(in, CONFIG_FILE.toPath());
        } catch (IOException e) {
            Flair.LOGGER.error("Failed to copy flair config", e);
        }
        return true;
    }

    public static void load() {
        copyDefaultConfig();

        try {
            INSTANCE.ALLOW_SPAM = false;
            INSTANCE.VOLUME = 100;
            INSTANCE.DEFAULT_SOUND = null;
            INSTANCE.DEFAULT_CRAFTING_SOUND = null;
            INSTANCE.DEFAULT_TYPING_SOUND = null;
            INSTANCE.DEFAULT_DROP_SOUND = null;

            INSTANCE.ITEM_CONDITIONS.clear();
            INSTANCE.ITEM_SOUNDS.clear();
            INSTANCE.BLOCK_CONDITIONS.clear();
            INSTANCE.BLOCK_SOUNDS.clear();
            Parser.parseLines(Files.readAllLines(CONFIG_FILE.toPath()).toArray(new String[0]));
        } catch (IOException e) {
            Flair.LOGGER.error("Failed to load flair config", e);
        }
    }
}
