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
import me.gravityio.flair.event.AnvilTypingEvent;
import me.gravityio.flair.event.ChatTypingEvent;
import me.gravityio.flair.event.HotbarChangedEvent;
import me.gravityio.flair.event.SignEvent;
import me.gravityio.flair.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Math;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
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

    private boolean logScreenClasses = false;
    private boolean logSounds = false;

    private int lastSoundTick;

    private final HashMap<String, String> playingSounds = new HashMap<>(16);

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
            ingameGui.getChatGUI().printChatMessage(
                    new ChatComponentText(message).setChatStyle(new ChatStyle().setColor(colour)));
        } else {
            for (String str : arr) {
                ingameGui.getChatGUI().printChatMessage(
                        new ChatComponentText(str).setChatStyle(new ChatStyle().setColor(colour)));
            }
        }
    }

    public static ItemStack getIdentifierStack(BlockInstance block) {
        List<ItemStack> items = getIdentifierItems(new MovingObjectPosition(block.x, block.y, block.z, 0,
                Vec3.createVectorHelper(block.x, block.y, block.z)));

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

            } catch (Exception ignored) {
            }
        }

        if (!items.isEmpty()) return items;

        try {
            @SuppressWarnings("deprecation")
            ItemStack pick = mouseoverBlock.getPickBlock(target, world, x, y, z);
            if (pick != null) items.add(pick);
        } catch (Exception ignored) {
        }

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
            return FlairConfig.INSTANCE.DEFAULT_SOUND.getSound(stack);
        }
        return sound.getSound(stack);
    }

    public void logScreens() {
        this.logScreenClasses = !this.logScreenClasses;
        Flair.sendMessage("Logging screen classes: %s", EnumChatFormatting.GREEN, Flair.INSTANCE.logScreenClasses);

        if (!this.logScreenClasses) {
            FlairLog.SCREENS.close();
        } else {
            try {
                FlairLog.SCREENS = new PrintStream(
                        new FileOutputStream(new File(FlairConfig.CONFIG_DIRECTORY, FlairConfig.SCREEN_LOG_NAME)),
                        true);
            } catch (FileNotFoundException e) {
                LOGGER.error("Failed to create sound log", e);
            }
        }
    }

    public void logSounds() {
        this.logSounds = !this.logSounds;
        Flair.sendMessage("Logging sounds: %s", Flair.INSTANCE.logSounds);

        if (!this.logSounds) {
            FlairLog.SOUNDS.close();
        } else {
            try {
                FlairLog.SOUNDS = new PrintStream(
                        new FileOutputStream(new File(FlairConfig.CONFIG_DIRECTORY, FlairConfig.SOUND_LOG_NAME)), true);
            } catch (FileNotFoundException e) {
                LOGGER.error("Failed to create screen log", e);
            }
        }
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
        playSound(sound.sound, sound.volume, sound.pitch);
    }

    public void playSound(SoundData sound, int delay) {
        if (sound == null) return;
        playSound(sound.sound, sound.volume, sound.pitch, delay);
    }

    public void playSound(SoundData sound, Float volume, Float pitch) {
        if (sound == null) return;
        this.playSound(sound.sound, volume == null ? sound.volume : volume, pitch == null ? sound.pitch : pitch);
    }

    public void playSound(SoundData sound, Float volume, Float pitch, int delay) {
        if (sound == null) return;
        this.playSound(sound.sound, volume == null ? sound.volume : volume, pitch == null ? sound.pitch : pitch, delay);
    }

    private void stopSound(MetaSound sound) {
        Minecraft mc = Minecraft.getMinecraft();
        String name = sound.getPositionedSoundLocation().toString();
        String uuid = this.playingSounds.get(name);
        if (uuid == null) return;
        mc.getSoundHandler().sndManager.sndSystem.stop(uuid);
    }

    public void playSound(String sound, float volume, float pitch) {
        if (sound == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        if (!FlairConfig.INSTANCE.ALLOW_SPAM && mc.thePlayer.ticksExisted == this.lastSoundTick) {
            LOGGER.debug("Last sound tick was {}, skipping", this.lastSoundTick);
            return;
        }

        MetaSound soundInstance = new MetaSound(
                sound, volume * FlairConfig.INSTANCE.VOLUME / 100f, pitch,
                mc.thePlayer
        );

        if (!FlairConfig.INSTANCE.ALLOW_SPAM) {
            this.stopSound(soundInstance);
            LOGGER.debug("Sound {} is already playing... stopping", sound);
        }

        mc.getSoundHandler().playSound(soundInstance);
        if (!FlairConfig.INSTANCE.ALLOW_SPAM) {
            this.playingSounds.put(
                    soundInstance.getPositionedSoundLocation().toString(),
                    (String) mc.getSoundHandler().sndManager.invPlayingSounds.get(soundInstance)
            );
        }

        this.lastSoundTick = mc.thePlayer.ticksExisted;
    }

    public void playSound(String sound, float volume, float pitch, int delay) {
        if (sound == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        MetaSound soundInstance = new MetaSound(sound, volume * FlairConfig.INSTANCE.VOLUME / 100f, pitch,
                mc.thePlayer);
        mc.getSoundHandler().playDelayedSound(soundInstance, delay);
        this.lastSoundTick = mc.thePlayer.ticksExisted;
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
    public void onWorldUnload(WorldEvent.Unload event) {
        if (!Flair.isClientThread()) return;
        if (this.logSounds) this.logSounds();
        if (this.logScreenClasses) this.logScreens();
    }

    @SubscribeEvent
    public void onSoundEvent(PlaySoundSourceEvent event) {
        if (!this.logSounds) return;
        if (event.sound instanceof MetaSound) return;
        FlairLog.SOUNDS.printf("Sound '%s' played%n", event.sound.getPositionedSoundLocation());
    }

    @SubscribeEvent
    public void onCraft(PlayerEvent.ItemCraftedEvent event) {
        if (!Flair.isClientThread()) return;
        if (FlairConfig.INSTANCE.DEFAULT_CRAFTING_SOUND == null) return;

        playSound(FlairConfig.INSTANCE.DEFAULT_CRAFTING_SOUND.getSound(event.crafting));
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!Flair.isClientThread()) return;

        Block block = event.world.getBlock(event.x, event.y, event.z);
        if (block == null || block == Blocks.air) return;
        int meta = event.world.getBlockMetadata(event.x, event.y, event.z);
        TileEntity te = event.world.getTileEntity(event.x, event.y, event.z);
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;

        playSound(new BlockInstance(block, event.world, te, meta, event.x, event.y, event.z));
    }

    // ONLY WORKS FOR DROPPING FROM THE UI ON THE CLIENT
    // TO DROP WITH KEYBIND @DropStackMixin
    @SubscribeEvent
    public void onItemTossedEvent(ItemTossEvent event) {
        if (FlairConfig.INSTANCE.DEFAULT_DROP_SOUND == null) return;
        if (!Flair.isClientThread()) return;

        playSound(FlairConfig.INSTANCE.DEFAULT_DROP_SOUND.getSound(event.entityItem.getEntityItem()));
    }

    //TODO: MODULARIZE? WE CAN DETECT KEY INPUTS AT THE ROOT BUT HOW TO KNOW IF IT'S BEING CAPTURED IN AN INPUTFIELD?
    @SubscribeEvent
    public void onSignChange(SignEvent event) {
        if (FlairConfig.INSTANCE.DEFAULT_TYPING_SOUND == null) return;
        if (event.character == 0) return;

        int lineIndex = event.line;
        int lineLength = event.lines[lineIndex].length();

        float p = (lineIndex / 4f + lineLength / 15f * 0.25f) / 2f;
        SoundData sound = FlairConfig.INSTANCE.DEFAULT_TYPING_SOUND.getSound(null);
        float newPitch = Math.lerp(sound.pitch, 2f, p);
        if (newPitch >= 2) newPitch = sound.pitch / 2f;
        playSound(sound, null, newPitch);
    }

    @SubscribeEvent
    public void onAnvilTyping(AnvilTypingEvent event) {
        if (FlairConfig.INSTANCE.DEFAULT_TYPING_SOUND == null) return;
        if (event.typedChar == 0) return;

        int lineLength = event.textField.getText().length();
        float maxLength = event.textField.getMaxStringLength();

        SoundData sound = FlairConfig.INSTANCE.DEFAULT_TYPING_SOUND.getSound(null);
        float newPitch = Math.lerp(sound.pitch, 2f, (float) lineLength / maxLength);
        if (newPitch >= 2) newPitch = sound.pitch / 2f;
        playSound(sound, null, newPitch);
    }

    //TODO: MODULARIZE? WE CAN DETECT KEY INPUTS AT THE ROOT BUT HOW TO KNOW IF IT'S BEING CAPTURED IN AN INPUTFIELD?
    @SubscribeEvent
    public void onChatTyping(ChatTypingEvent event) {
        if (FlairConfig.INSTANCE.DEFAULT_TYPING_SOUND == null) return;
        if (event.typedChar == 0) return;

        int lineLength = event.inputField.getText().length();
        int maxLength = event.inputField.getMaxStringLength();

        SoundData sound = FlairConfig.INSTANCE.DEFAULT_TYPING_SOUND.getSound(null);
        float newPitch = Math.lerp(sound.pitch, 2f, (float) lineLength / maxLength);
        if (newPitch >= 2) newPitch = sound.pitch / 2f;
        playSound(sound, null, newPitch);
    }

    // TODO: MODULARIZE? HOW TO ENCODE TO A NICE USER DISPLAYABLE STRING? JUST DONT? AND MAKE A COMMAND TO LOG SCREEN CLASS NAMES?
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (this.logScreenClasses && event.gui != null) {
            List<String> hierarchy = Utils.getClassHierarchy(event.gui.getClass());
            FlairLog.SCREENS.printf("Gui '%s' opened%n", String.join(" -> ", hierarchy));
        }

        if (FlairConfig.INSTANCE.DEFAULT_INV_SOUND == null) return;
        GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
        GuiScreen newScreen = event.gui;
        boolean anyInventory = currentScreen instanceof GuiInventory || newScreen instanceof GuiInventory;
        boolean close = newScreen == null;
        if (!anyInventory) return;

        SoundData sound = FlairConfig.INSTANCE.DEFAULT_INV_SOUND.getSound(null);
        if (close) {
            playSound(sound, null, sound.pitch * 0.9f, 2);
        } else {
            playSound(sound, null, sound.pitch);
        }
    }

    @SubscribeEvent
    public void onHotbarChange(HotbarChangedEvent event) {
        this.playSound(event.player.inventory.getCurrentItem());
    }

}
