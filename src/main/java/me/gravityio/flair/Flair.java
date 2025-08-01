package me.gravityio.flair;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.gravityio.flair.condition.ItemCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
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
    public static boolean OUR_SOUND = false;
    public static Flair INSTANCE;

    private long lastSound;

    public static void sendMessage(String message, Object... args) {
        message = String.format(message, args);
        String[] arr = message.split("\n");
        if (arr.length == 0) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
        } else {
            for (String str : arr) {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
            }
        }
    }

    public static String getSound(ItemStack stack) {
        if (stack == null) return null;

        String sound = FlairConfig.CONFIG.get(stack);
        if (sound == null) {
            for (ItemCondition condition : FlairConfig.CONFIG.CONDITIONS) {
                if (!condition.shouldPlay(stack)) continue;
                return condition.getSound();
            }
            return SoundResources.POP;
        }
        return sound;
    }

    public void playSound(ItemStack stack) {
        if (stack == null) return;
        this.playSound(getSound(stack), FlairConfig.CONFIG.VOLUME / 100f, 1);
    }

    public void playSound(String sound, float volume, float pitch) {
        if (sound == null) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        if (System.currentTimeMillis() - this.lastSound < 100) return;

        // FOR THE MIXIN MOD
        OUR_SOUND = true;
        mc.thePlayer.playSound(sound, volume, pitch);

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
