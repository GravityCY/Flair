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
import me.gravityio.flair.condition.ItemCondition;
import me.gravityio.flair.condition.SoundData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public static SoundData getSound(ItemStack stack) {
        if (stack == null) return null;

        ISoundGenerator sound;
        String name = GameData.getItemRegistry().getNameForObject(stack.getItem());
        if (stack.getHasSubtypes()) {
            sound = FlairConfig.INSTANCE.ITEM_SOUNDS.get(name + "@" + stack.getItemDamage());
            if (sound == null) sound = FlairConfig.INSTANCE.ITEM_SOUNDS.get(name);
        } else {
            sound = FlairConfig.INSTANCE.ITEM_SOUNDS.get(name);
        }

        if (sound == null) {
            for (ItemCondition condition : FlairConfig.INSTANCE.CONDITIONS) {
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
        event.player.playSound(SoundResources.POP, 1, 1);
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui == null) return;


        Minecraft mc = Minecraft.getMinecraft();
        if (event.gui instanceof GuiFurnace) {
            mc.thePlayer.playSound("fire.fire", 1, 1);
        } else if (event.gui instanceof GuiRepair) {
            mc.thePlayer.playSound("random.anvil_land", 1, 1);
        } else if (event.gui instanceof GuiDispenser) {
            mc.thePlayer.playSound("random.click", 1, 1);
        } else if (event.gui instanceof GuiHopper) {
            mc.thePlayer.playSound(SoundResources.METALLY, 1, 1);
        } else if (event.gui instanceof GuiCrafting) {
            mc.thePlayer.playSound(SoundResources.WOODY, 1, 1);
        }
    }

}
