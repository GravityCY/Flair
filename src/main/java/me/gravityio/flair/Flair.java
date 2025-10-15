package me.gravityio.flair;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.gravityio.flair.config.FlairConfig;
import me.gravityio.flair.config.WatchThread;
import me.gravityio.flair.data.BlockInstance;
import me.gravityio.flair.data.MetaSound;
import me.gravityio.flair.data.SoundData;
import me.gravityio.flair.event.*;
import me.gravityio.flair.event.nei.*;
import me.gravityio.flair.util.MathHelper;
import me.gravityio.flair.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Math;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
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

    public static BlockSoundRegistry BLOCK_SOUNDS = SoundRegistries.INSTANCE.register("block_sounds",
            new BlockSoundRegistry(8, 16));
    public static ItemSoundRegistry ITEM_SOUNDS = SoundRegistries.INSTANCE.register("item_sounds",
            new ItemSoundRegistry(8, 32));
    public static ItemSoundRegistry CRAFT_SOUNDS = SoundRegistries.INSTANCE.register("craft_sounds",
            new ItemSoundRegistry(4, 4));
    public static ItemSoundRegistry DROP_SOUNDS = SoundRegistries.INSTANCE.register("drop_sounds",
            new ItemSoundRegistry(4, 4));
    public static ItemSoundRegistry SWING_SOUNDS = SoundRegistries.INSTANCE.register("swing_sounds",
            new ItemSoundRegistry(4, 4));

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
            if (pick.getItem() != null) items.add(pick);
        } catch (Exception ignored) {
        }

        if (!items.isEmpty()) return items;

        items.add(0, new ItemStack(mouseoverBlock, 1, world.getBlockMetadata(x, y, z)));

        return items;
    }

    public static boolean isClientThread() {
        return Minecraft.getMinecraft().func_152345_ab();
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

    public void playSound(SoundData sound) {
        if (sound == null) return;
        playSound(sound.sound, sound.volume, sound.pitch);
    }

    public void playSound(SoundData sound, Float volume, Float pitch) {
        if (sound == null) return;
        this.playSound(sound.sound, volume == null ? sound.volume : volume, pitch == null ? sound.pitch : pitch);
    }

    public void playSoundForced(SoundData sound) {
        if (sound == null) return;
        this.playSoundForced(sound, null, null);
    }

    public void playSoundForced(SoundData sound, Float volume, Float pitch) {
        if (sound == null) return;
        this.playSoundForced(sound.sound, volume == null ? sound.volume : volume, pitch == null ? sound.pitch : pitch);
    }

    public void playSoundForced(String sound, float volume, float pitch) {
        if (sound == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        MetaSound soundInstance = new MetaSound(
                sound, volume * FlairConfig.INSTANCE.VOLUME / 100f, pitch,
                mc.thePlayer
        );
        mc.getSoundHandler().playSound(soundInstance);
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

    public void playSoundDelayed(SoundData sound, int delay) {
        if (sound == null) return;
        playSoundDelayed(sound.sound, sound.volume, sound.pitch, delay);
    }

    public void playSoundDelayed(SoundData sound, Float volume, Float pitch, int delay) {
        if (sound == null) return;
        this.playSoundDelayed(sound.sound, volume == null ? sound.volume : volume, pitch == null ? sound.pitch : pitch,
                delay);
    }

    public void playSoundDelayed(String sound, float volume, float pitch, int delay) {
        if (sound == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        MetaSound soundInstance = new MetaSound(sound, volume * FlairConfig.INSTANCE.VOLUME / 100f, pitch,
                mc.thePlayer);
        mc.getSoundHandler().playDelayedSound(soundInstance, delay);
        this.lastSoundTick = mc.thePlayer.ticksExisted;
    }

    public void stopSound(MetaSound sound) {
        Minecraft mc = Minecraft.getMinecraft();
        String name = sound.getPositionedSoundLocation().toString();
        String uuid = this.playingSounds.get(name);
        if (uuid == null) return;
        mc.getSoundHandler().sndManager.sndSystem.stop(uuid);
    }

    public void playSoundAt(SoundData sound, Vec3 pos, SoundCategory category) {
        if (sound == null) return;
        this.playSoundAt(sound.sound, sound.volume, sound.pitch, pos, category);
    }

    public void playSoundAt(@Nullable String sound, float volume, float pitch, Vec3 pos, SoundCategory category) {
        if (sound == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        MetaSound soundInstance = new MetaSound(
                sound, volume, pitch,
                pos, category
        );

        mc.getSoundHandler().playSound(soundInstance);
    }

    public void playTyping(int currentLength, int maxLength, boolean end) {
        if (FlairConfig.INSTANCE.TYPING_SOUND == null) return;

        SoundData sound = FlairConfig.INSTANCE.TYPING_SOUND.getSound(null);
        float newPitch;
        if (end) {
            newPitch = sound.pitch / 2f;
        } else {
            newPitch = Math.lerp(sound.pitch, 2f, (float) currentLength / maxLength);
        }
        this.playSoundForced(sound, null, newPitch);
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

    // MODE 0 == PICKUP, PLACE | MOUSEBUTTON IS REAL
    // MODE 1 == SHIFT CLICK | MOUSEBUTTON IS REAL
    // MODE 2 == NUMBERS | MOUSEBUTTON IS SLOT INDEX
    // MODE 3 == MIDDLE MOUSE | MOUSEBUTTON IS MIDDLE MOUSE
    // MODE 4 == DROP | MOUSEBUTTON: 0 IS DROP 1, 1 IS DROP ALL
    // MODE 5 == SPREAD | MOUSEBUTTON IS PACKED INT: SPREAD STAGE, AND MOUSE BUTTON
    // MODE 6 == PICKUP ALL | MOUSE BUTTON IS REAL BUT ONLY EVER LEFT CLICK
    @SubscribeEvent
    public void onSlotClickEvent(SlotClickEvent event) {
        var player = Minecraft.getMinecraft().thePlayer;
        var inventorySlots = event.container.inventorySlots;

        if (event.slot < 0 || event.slot >= inventorySlots.size()) return;
        ItemStack stack = null;
        switch (event.mode) {
            case 0: {
                stack = inventorySlots.get(event.slot).getStack();
                if (stack == null) stack = player.inventory.getItemStack();
                break;
            }
            case 1, 3: {
                stack = inventorySlots.get(event.slot).getStack();
                break;
            }
            case 2: {
                stack = inventorySlots.get(event.slot).getStack();
                if (stack == null) stack = player.inventory.getStackInSlot(event.button);
                break;
            }
            case 5, 6: {
                stack = player.inventory.getItemStack();
                break;
            }
        }

        if (stack == null) return;
        playSound(ITEM_SOUNDS.getSound(stack));
    }

    @SubscribeEvent
    public void onStackDroppedEvent(DropStackEvent event) {
        if (!Flair.isClientThread()) return;

        playSound(DROP_SOUNDS.getSound(event.item));
    }

    @SubscribeEvent
    public void onSwingItemEvent(SwingItemEvent event) {
        if (!Flair.isClientThread()) return;
        if (event.type != SwingItemEvent.SwingType.START) return;

        this.playSoundForced(SWING_SOUNDS.getSound(event.stack));
    }

    @SubscribeEvent
    public void onCraft(PlayerEvent.ItemCraftedEvent event) {
        if (!Flair.isClientThread()) return;

        playSound(CRAFT_SOUNDS.getSound(event.crafting));
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!Flair.isClientThread()) return;

        Block block = event.world.getBlock(event.x, event.y, event.z);
        if (block == null || block == Blocks.air) return;
        int meta = event.world.getBlockMetadata(event.x, event.y, event.z);
        TileEntity te = event.world.getTileEntity(event.x, event.y, event.z);
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;

        playSound(BLOCK_SOUNDS.getSound(new BlockInstance(block, event.world, te, meta, event.x, event.y, event.z)));
    }

    @SubscribeEvent
    public void onNEIRecipeClick(NEIOpenRecipeEvent event) {
        if (!Flair.isClientThread()) return;
        playSound(ITEM_SOUNDS.getSound(event.stack));
    }

    @SubscribeEvent
    public void onNEIRecipeTyping(NEIRecipeTypingEvent event) {
        int l1 = event.newString.length();
        int l2 = 256;
        this.playTyping(l1, l2, l1 == l2);
    }

    @SubscribeEvent
    public void onNEISearchTyping(NEISearchTypingEvent event) {
        int l1 = event.newString.length();
        int l2 = 256;
        this.playTyping(l1, l2, l1 == l2);
    }

    @SubscribeEvent
    public void onNEIAddBookmark(NEINewBookmarkEvent event) {
        if (FlairConfig.INSTANCE.BOOKMARK_SOUND == null) return;

        SoundData data = FlairConfig.INSTANCE.BOOKMARK_SOUND.getSound(event.bookmarkItem.itemStack);
        if (data == null) return;

        int current = event.bookmarkGrid.size() + 1;
        int wrapped = current % event.bookmarkGrid.getPerPage();
        float p = (float) wrapped / event.bookmarkGrid.getPerPage();

        float start = data.pitch;
        float end = Math.min(data.pitch * 1.5f, 1.5f);
        float pitch = MathHelper.lerp(start, end, p);

        playSound(data, null, pitch);
    }

    @SubscribeEvent
    public void onNEIRemoveBookmark(NEIRemoveBookmarkEvent event) {
        if (FlairConfig.INSTANCE.BOOKMARK_SOUND == null) return;

        SoundData data = FlairConfig.INSTANCE.BOOKMARK_SOUND.getSound(null);
        if (data == null) return;

        playSound(data, null, Math.max(data.pitch * 0.5f, 0.5f));
    }

    @SubscribeEvent
    public void onNEIScrollBookmark(NEIScrollBookmarkEvent event) {
        if (FlairConfig.INSTANCE.TYPING_SOUND == null) return;

        SoundData data = FlairConfig.INSTANCE.TYPING_SOUND.getSound(null);
        if (data == null) return;

        long maxStackSize = (long) event.bookmarkItem.itemStack.getMaxStackSize() * Math.abs(event.shift);
        if (maxStackSize == 1 || maxStackSize == 0) maxStackSize = 64;

        long newSize = event.bookmarkItem.amount + event.shift;
        long wrapped = newSize % maxStackSize;
        float p = (float) wrapped / maxStackSize;

        float pitch = MathHelper.lerp(data.pitch, java.lang.Math.min(2, data.pitch * 2), p);
        playSoundForced(data, null, pitch);
    }

    //TODO: MODULARIZE? WE CAN DETECT KEY INPUTS AT THE ROOT BUT HOW TO KNOW IF IT'S BEING CAPTURED IN AN INPUTFIELD?
    @SubscribeEvent
    public void onSignChange(SignEvent event) {
        if (event.character == 0) return;

        int lineIndex = event.line;
        int lineLength = event.lines[lineIndex].length();
        int total = lineIndex * 15 + lineLength;

        this.playTyping(total, 60, total % 15 == 0);
    }

    @SubscribeEvent
    public void onAnvilTyping(AnvilTypingEvent event) {
        if (!ChatAllowedCharacters.isAllowedCharacter(event.typedChar) && event.keyCode != Keyboard.KEY_BACK) return;

        int l1 = event.inputField.getText().length();
        int l2 = event.inputField.getMaxStringLength();

        this.playTyping(l1, l2, l1 == l2);
    }

    //TODO: MODULARIZE? WE CAN DETECT KEY INPUTS AT THE ROOT BUT HOW TO KNOW IF IT'S BEING CAPTURED IN AN INPUTFIELD?
    @SubscribeEvent
    public void onChatTyping(ChatTypingEvent event) {
        if (event.keyCode == Keyboard.KEY_ESCAPE || event.keyCode == Keyboard.KEY_RETURN) return;

        int l1 = event.inputField.getText().length();
        int l2 = event.inputField.getMaxStringLength();

        this.playTyping(l1, l2, l1 == l2);
    }

    // TODO: MODULARIZE? HOW TO ENCODE TO A NICE USER DISPLAYABLE STRING? JUST DONT? AND MAKE A COMMAND TO LOG SCREEN CLASS NAMES?
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (this.logScreenClasses && event.gui != null) {
            List<String> hierarchy = Utils.getClassHierarchy(event.gui.getClass());
            FlairLog.SCREENS.printf("Gui '%s' opened%n", String.join(" -> ", hierarchy));
        }

        if (FlairConfig.INSTANCE.INVENTORY_SOUND == null) return;
        GuiScreen prevScreen = Minecraft.getMinecraft().currentScreen;
        GuiScreen newScreen = event.gui;
        boolean close = newScreen == null;
        boolean anyInventory = newScreen instanceof GuiInventory || prevScreen instanceof GuiInventory;
        if (!anyInventory) return;

        SoundData sound = FlairConfig.INSTANCE.INVENTORY_SOUND.getSound(null);
        if (close && prevScreen instanceof GuiInventory) {
            playSoundDelayed(sound, null, sound.pitch * 0.9f, 2);
        } else {
            playSound(sound, null, sound.pitch);
        }
    }

    @SubscribeEvent
    public void onHotbarChange(HotbarChangedEvent event) {
        float p = event.newSlot / 9f;

        SoundData sound;
        if (FlairConfig.INSTANCE.HOTBAR_SOUND == null) {
            sound = ITEM_SOUNDS.getSound(event.player.inventory.getCurrentItem());
        } else {
            sound = FlairConfig.INSTANCE.HOTBAR_SOUND.getSound(event.player.inventory.getCurrentItem());
        }
        if (sound == null) return;
        this.playSoundForced(sound.copy(null, null, Math.lerp(sound.pitch, 2f, p)));
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
}
