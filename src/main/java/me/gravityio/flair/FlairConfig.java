package me.gravityio.flair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.gravityio.flair.condition.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.gravityio.flair.SoundResources.*;

@SideOnly(Side.CLIENT)
public class FlairConfig {
    public static final String SOUNDMAP_PATH = "item.soundmap";
    public static final String CONFIG_PATH = "flair.json";

    public static FlairConfig CONFIG;
    public static File CONFIG_DIRECTORY;
    public static File CONFIG_FILE;
    public static File SOUNDMAP_FILE;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int VOLUME = 100;
    public String DEFAULT_SOUND = "random.pop";
    public Map<String, String> ITEM_SOUNDS = new HashMap<>();
    public transient List<ItemCondition> CONDITIONS = new ArrayList<>();

    public FlairConfig() {
        this.CONDITIONS.add(
                new ItemCondition(
                        new BinaryExpression(
                                new ItemExpression(
                                        VariableType.ITEM_DISPLAY_NAME,
                                        CompareMethod.CONTAINS,
                                        "wood"
                                ),
                                new ItemExpression(
                                        VariableType.ITEM_ID,
                                        CompareMethod.CONTAINS,
                                        "wood"
                                ),
                                BinaryOperator.OR
                        ),
                        WOODY)
        );

        this.put(Item.getItemFromBlock(Blocks.noteblock), "note.harp");
        this.put(Item.getItemFromBlock(Blocks.piston), "tile.piston.in");
        this.put(Item.getItemFromBlock(Blocks.sticky_piston), "tile.piston.in");
        this.put(Item.getItemFromBlock(Blocks.chest), "random.chestopen");
        this.put(Item.getItemFromBlock(Blocks.furnace), "fire.fire");
        this.put(Item.getItemFromBlock(Blocks.lever), "random.click");
        this.put(Item.getItemFromBlock(Blocks.dispenser), "random.click");
        this.put(Item.getItemFromBlock(Blocks.wooden_button), "random.click");
        this.put(Item.getItemFromBlock(Blocks.stone_button), "random.click");
        this.put(Item.getItemFromBlock(Blocks.powered_repeater), "random.click");
        this.put(Item.getItemFromBlock(Blocks.unpowered_repeater), "random.click");
        this.put(Item.getItemFromBlock(Blocks.powered_comparator), "random.click");
        this.put(Item.getItemFromBlock(Blocks.unpowered_comparator), "random.click");
        this.put(Item.getItemFromBlock(Blocks.ladder), "step.ladder");
        this.put(Item.getItemFromBlock(Blocks.anvil), "random.anvil_land");
        this.put(Items.flint_and_steel, "fire.ignite");
        this.put(Items.fire_charge, "fire.ignite");
        this.put(Items.bow, "random.bowhit");
        this.put(Items.arrow, "random.bowhit");
        this.put(Items.shears, "mob.sheep.shear");
        this.put(Items.lava_bucket, "liquid.lavapop");
        this.put(Items.water_bucket, "liquid.water");
        this.put(Items.slime_ball, "mob.slime.small");
        this.put(Items.magma_cream, "mob.slime.small");
        this.put(Items.fishing_rod, "random.bow");
        this.put(Items.blaze_rod, "fire.fire");
        this.put(Items.bone, "mob.skeleton.step");
        this.put(Items.nether_star, "random.orb");
        this.put(Items.fireworks, "fireworks.launch");
        this.put(Items.fish, "mob.silverfish.step");
        this.put(Items.cooked_fished, "mob.silverfish.step");
        this.put(Items.spider_eye, "mob.silverfish.step");
        this.put(Items.fermented_spider_eye, "mob.silverfish.step");
        this.put(Items.rotten_flesh, "mob.silverfish.step");

        this.put(Items.diamond_helmet, SHING);
        this.put(Items.chainmail_helmet, SHING);
        this.put(Items.golden_helmet, SHING);
        this.put(Items.iron_helmet, SHING);
        this.put(Items.diamond_chestplate, SHING);
        this.put(Items.chainmail_chestplate, SHING);
        this.put(Items.golden_chestplate, SHING);
        this.put(Items.iron_chestplate, SHING);
        this.put(Items.diamond_leggings, SHING);
        this.put(Items.chainmail_leggings, SHING);
        this.put(Items.golden_leggings, SHING);
        this.put(Items.iron_leggings, SHING);
        this.put(Items.diamond_boots, SHING);
        this.put(Items.chainmail_boots, SHING);
        this.put(Items.golden_boots, SHING);
        this.put(Items.iron_boots, SHING);

        this.put(Item.getItemFromBlock(Blocks.glass), CRYSTALLY);
        this.put(Item.getItemFromBlock(Blocks.glass_pane), CRYSTALLY);
        this.put(Item.getItemFromBlock(Blocks.stained_glass), CRYSTALLY);
        this.put(Item.getItemFromBlock(Blocks.stained_glass_pane), CRYSTALLY);
        this.put(Items.diamond, CRYSTALLY);
        this.put(Items.emerald, CRYSTALLY);
        this.put(Items.potionitem, CRYSTALLY);
        this.put(Items.experience_bottle, CRYSTALLY);
        this.put(Items.glass_bottle, CRYSTALLY);
        this.put(Items.dye, 4, CRYSTALLY);

        this.put(Item.getItemFromBlock(Blocks.iron_block), METALLY);
        this.put(Item.getItemFromBlock(Blocks.iron_door), METALLY);
        this.put(Item.getItemFromBlock(Blocks.emerald_block), METALLY);
        this.put(Item.getItemFromBlock(Blocks.lapis_block), METALLY);
        this.put(Item.getItemFromBlock(Blocks.redstone_block), METALLY);
        this.put(Item.getItemFromBlock(Blocks.cauldron), METALLY);
        this.put(Item.getItemFromBlock(Blocks.hopper), METALLY);
        this.put(Item.getItemFromBlock(Blocks.diamond_block), METALLY);
        this.put(Item.getItemFromBlock(Blocks.gold_block), METALLY);
        this.put(Item.getItemFromBlock(Blocks.iron_bars), METALLY);

        this.put(Item.getItemFromBlock(Blocks.snow), SNOWY);
        this.put(Item.getItemFromBlock(Blocks.snow_layer), SNOWY);
        this.put(Items.snowball, SNOWY);

        this.put(Item.getItemFromBlock(Blocks.crafting_table), WOODY);
        this.put(Item.getItemFromBlock(Blocks.bookshelf), WOODY);
        this.put(Item.getItemFromBlock(Blocks.jukebox), WOODY);
        this.put(Item.getItemFromBlock(Blocks.ladder), WOODY);
        this.put(Item.getItemFromBlock(Blocks.chest), WOODY);
        this.put(Item.getItemFromBlock(Blocks.trapped_chest), WOODY);
        this.put(Item.getItemFromBlock(Blocks.fence), WOODY);
        this.put(Item.getItemFromBlock(Blocks.fence_gate), WOODY);
        this.put(Items.bed, WOODY);
        this.put(Items.stick, WOODY);
        this.put(Items.bowl, WOODY);
        this.put(Items.mushroom_stew, WOODY);
        this.put(Items.boat, WOODY);
        this.put(Items.sign, WOODY);

        this.put(Item.getItemFromBlock(Blocks.wool), CLOTHY);
        this.put(Item.getItemFromBlock(Blocks.carpet), CLOTHY);
        this.put(Items.string, CLOTHY);
        this.put(Items.feather, CLOTHY);
        this.put(Items.lead, CLOTHY);

        this.put(Item.getItemFromBlock(Blocks.sand), DUSTY);
        this.put(Item.getItemFromBlock(Blocks.soul_sand), DUSTY);
        this.put(Item.getItemFromBlock(Blocks.gravel), DUSTY);
        this.put(Items.sugar, DUSTY);
        this.put(Items.clay_ball, DUSTY);
        this.put(Items.gunpowder, DUSTY);
        this.put(Items.glowstone_dust, DUSTY);
        this.put(Items.redstone, DUSTY);
        this.put(Items.dye, DUSTY);
        this.put(Items.blaze_powder, DUSTY);

        this.put(Item.getItemFromBlock(Blocks.cactus), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.leaves), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.leaves2), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.farmland), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.hay_block), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.grass), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.dirt), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.mycelium), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.tallgrass), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.red_flower), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.yellow_flower), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.sapling), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.brown_mushroom), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.red_mushroom), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.vine), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.waterlily), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.double_plant), EARTHY);
        this.put(Item.getItemFromBlock(Blocks.deadbush), EARTHY);
        this.put(Items.melon, EARTHY);
        this.put(Items.melon_seeds, EARTHY);
        this.put(Items.speckled_melon, EARTHY);
        this.put(Items.wheat_seeds, EARTHY);
        this.put(Items.wheat, EARTHY);
        this.put(Items.reeds, EARTHY);
        this.put(Items.paper, EARTHY);
        this.put(Items.carrot, EARTHY);
        this.put(Items.potato, EARTHY);

        this.put(Items.saddle, LEATHERY);
        this.put(Items.leather, LEATHERY);
        this.put(Items.leather_helmet, LEATHERY);
        this.put(Items.leather_chestplate, LEATHERY);
        this.put(Items.leather_leggings, LEATHERY);
        this.put(Items.leather_boots, LEATHERY);
    }

    public static void init(File configDirectory) {
        CONFIG_DIRECTORY = new File(configDirectory, "flair");
        CONFIG_DIRECTORY.mkdirs();
        CONFIG_FILE = new File(CONFIG_DIRECTORY, CONFIG_PATH);
        SOUNDMAP_FILE = new File(CONFIG_DIRECTORY, SOUNDMAP_PATH);
    }

    public static void load() {
        loadConfig();
        loadSoundMap();
    }

    public static void loadConfig() {
        Flair.LOGGER.info("Loading flair config...");
        if (!CONFIG_FILE.exists()) {
            save();
        } else {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                CONFIG = GSON.fromJson(reader, FlairConfig.class);
            } catch (IOException e) {
                Flair.LOGGER.error("Failed to load flair config", e);
            } catch (JsonSyntaxException e) {
                Flair.LOGGER.error("Invalid flair config! Resetting...");
                save();
            }
        }
    }

    public static void loadSoundMap() {
        try {
            CONFIG.CONDITIONS.clear();
            Parser.parseLines(Files.readAllLines(SOUNDMAP_FILE.toPath()).toArray(new String[0]));
        } catch (IOException e) {
            Flair.LOGGER.error("Failed to load flair soundmap", e);
        }
    }

    public static void save() {
        Flair.LOGGER.info("Saving flair config...");
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            if (CONFIG == null) {
                CONFIG = new FlairConfig();
            }
            GSON.toJson(CONFIG, writer);
        } catch (IOException e) {
            Flair.LOGGER.error("Failed to save flair config", e);
        }
    }

    public void put(ItemStack stack, String sound) {
        if (stack == null) return;

        Item item = stack.getItem();
        if (item == null) return;
        if (item.getHasSubtypes()) {
            this.put(item, stack.getItemDamage(), sound);
            return;
        }
        this.put(item, sound);
    }

    public void put(Item item, String sound) {
        this.ITEM_SOUNDS.put(String.valueOf(Item.getIdFromItem(item)), sound);
    }

    public void put(Item item, int meta, String sound) {
        this.ITEM_SOUNDS.put(Item.getIdFromItem(item) + ":" + meta, sound);
    }

    public String get(ItemStack stack) {
        if (stack.getHasSubtypes()) {
            String key = Item.getIdFromItem(stack.getItem()) + ":" + stack.getItemDamage();
            if (this.ITEM_SOUNDS.containsKey(key)) return this.ITEM_SOUNDS.get(key);
        }
        return this.ITEM_SOUNDS.get(String.valueOf(Item.getIdFromItem(stack.getItem())));
    }
}
