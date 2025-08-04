package me.gravityio.flair;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.gravityio.flair.condition.ISoundGenerator;
import me.gravityio.flair.condition.SoundCondition;
import me.gravityio.flair.condition.SoundData;
import me.gravityio.flair.event.HotbarChangedEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(
        modid = Flair.MODID,
        version = "1.0",
        name = "Flair",
        dependencies = "required-after:gtnhlib;required-after:unimixins",
        acceptedMinecraftVersions = "[1.7.10]"
)
@SideOnly(Side.CLIENT)
public class Flair {
    public static final String MODID = "flair";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static Flair INSTANCE;

    // just a flag for the mixin to identify our sound
    public boolean ourSound = false;
    private long lastSound;

    public static void sendMessage(String message, Object... args) {
        GuiIngame ingameGui = Minecraft.getMinecraft().ingameGUI;
        if (ingameGui == null) return;

        message = String.format(message, args);
        String[] arr = message.split("\n");
        if (arr.length == 0) {
            ingameGui.getChatGUI().printChatMessage(new ChatComponentText(message));
        } else {
            for (String str : arr) {
                ingameGui.getChatGUI().printChatMessage(new ChatComponentText(str));
            }
        }
    }

    public static void sendMessage(String message, EnumChatFormatting colour, Object... args) {
        GuiIngame ingameGui = Minecraft.getMinecraft().ingameGUI;
        if (ingameGui == null) return;

        message = String.format(message, args);
        String[] arr = message.split("\n");
        if (arr.length == 0) {
            ingameGui.getChatGUI().printChatMessage(new ChatComponentText(message).setChatStyle(new ChatStyle().setColor(colour)));
        } else {
            for (String str : arr) {
                ingameGui.getChatGUI().printChatMessage(new ChatComponentText(str).setChatStyle(new ChatStyle().setColor(colour)));
            }
        }
    }

    public static ItemStack getIdentifierStack(BlockInstance block) {
        List<ItemStack> items = getIdentifierItems(new MovingObjectPosition(block.x, block.y, block.z, 0, Vec3.createVectorHelper(block.x, block.y, block.z)));

        if (items.isEmpty()) return null;
        items.sort((stack0, stack1) -> stack1.getItemDamage() - stack0.getItemDamage());
        return items.get(0);
    }

    public static List<ItemStack> getIdentifierItems(MovingObjectPosition target) {
        List<ItemStack> items = new ArrayList<>();
        if (target == null) return items;

        World world = Minecraft.getMinecraft().theWorld;
        if (world == null) return items;

        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;

        Block mouseoverBlock = world.getBlock(x, y, z);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (mouseoverBlock == null) return items;

        if (tileEntity == null) {
            try {
                ItemStack block = new ItemStack(mouseoverBlock, 1, world.getBlockMetadata(x, y, z));

                if (block.getItem() != null) items.add(block);

            } catch (Exception ignored) {}
        }

        if (!items.isEmpty()) return items;

        try {
            ItemStack pick = mouseoverBlock.getPickBlock(target, world, x, y, z);
            if (pick != null) items.add(pick);
        } catch (Exception ignored) {}

        if (!items.isEmpty()) return items;

        items.add(0, new ItemStack(mouseoverBlock, 1, world.getBlockMetadata(x, y, z)));

        return items;
    }

    public static SoundData getSound(BlockInstance blockInstance) {
        if (blockInstance == null) return null;

        String name = GameData.getBlockRegistry().getNameForObject(blockInstance.block);
        ISoundGenerator<BlockInstance> sound = FlairConfig.INSTANCE.BLOCK_SOUNDS.get(name + "@" + blockInstance.meta);
        if (sound == null) sound = FlairConfig.INSTANCE.BLOCK_SOUNDS.get(name);

        if (sound == null) {
            for (SoundCondition<BlockInstance> condition : FlairConfig.INSTANCE.BLOCK_CONDITIONS) {
                if (!condition.shouldPlay(blockInstance)) continue;
                return condition.getSound(blockInstance);
            }
            return null;
        }
        return sound.getSound(blockInstance);
    }

    public static SoundData getSound(ItemStack stack) {
        if (stack == null) return null;

        ISoundGenerator<ItemStack> sound;
        String name = GameData.getItemRegistry().getNameForObject(stack.getItem());
        if (stack.getHasSubtypes()) {
            sound = FlairConfig.INSTANCE.ITEM_SOUNDS.get(name + "@" + stack.getItemDamage());
            if (sound == null) sound = FlairConfig.INSTANCE.ITEM_SOUNDS.get(name);
        } else {
            sound = FlairConfig.INSTANCE.ITEM_SOUNDS.get(name);
        }

        if (sound == null) {
            for (SoundCondition<ItemStack> condition : FlairConfig.INSTANCE.ITEM_CONDITIONS) {
                if (!condition.shouldPlay(stack)) continue;
                return condition.getSound(stack);
            }
            return FlairConfig.INSTANCE.DEFAULT_SOUND;
        }
        return sound.getSound(stack);
    }

    public void playSound(ItemStack stack) {
        if (stack == null) return;
        this.playSound(getSound(stack));
    }

    public void playSound(BlockInstance block) {
        if (block == null) return;
        this.playSound(getSound(block));
    }

    public void playSound(SoundData sound) {
        if (sound == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        if (!FlairConfig.INSTANCE.ALLOW_SPAM && System.currentTimeMillis() == this.lastSound) return;

        // FOR THE MIXIN MOD
        ourSound = true;
        mc.thePlayer.playSound(sound.sound, sound.volume * FlairConfig.INSTANCE.VOLUME / 100f, sound.pitch);

        this.lastSound = System.currentTimeMillis();
    }

    public static boolean isClientThread() {
        return Minecraft.getMinecraft().func_152345_ab();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FlairConfig.init(event.getModConfigurationDirectory());
        FlairConfig.load();
        WatchThread watchThread = new WatchThread(FlairConfig.CONFIG_DIRECTORY);
        watchThread.start();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        ClientCommandHandler.instance.registerCommand(new FlairCommand());

        INSTANCE = this;
    }

    @SubscribeEvent
    public void onCraft(PlayerEvent.ItemCraftedEvent event) {
        if (!Flair.isClientThread()) return;
        event.player.playSound(SoundResources.POP, 1, 1);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!Flair.isClientThread()) return;

        Block block = event.world.getBlock(event.x, event.y, event.z);
        if (block == null || block == Blocks.air) return;
        int meta = event.world.getBlockMetadata(event.x, event.y, event.z);
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;

        playSound(new BlockInstance(block, event.world, meta, event.x, event.y, event.z));
    }

    @SubscribeEvent
    public void onItemTossedEvent(ItemTossEvent event) {
        if (!Flair.isClientThread()) return;
        playSound(event.entityItem.getEntityItem());
    }

    @SubscribeEvent
    public void onHotbarChange(HotbarChangedEvent event) {
        playSound(event.player.inventory.getCurrentItem());
    }

}
