package me.gravityio.flair;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.*;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
@TransformerExclusions("me.gravityio.flair.FlairCore")
@MCVersion("1.7.10")
@Name("Flair")
public class FlairEarlyMixins implements IFMLLoadingPlugin, IEarlyMixinLoader {
    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public String getMixinConfig() {
        return "mixins.flair.vanilla.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        if (FMLLaunchHandler.side() == Side.CLIENT) {
            return Arrays.asList(
                    "DropStackMixin",
                    "SlotClickMixin",
                    "SlotClickBackupMixin",
                    "SoundCategoryMixin",
                    "ChangeSlotMixin",
                    "SignEventMixin",
                    "ChatTypingMixin",
                    "AnvilTypingMixin",
                    "SwingItemMixin$SwingStartMixin",
                    "SwingItemMixin$SwingAnimMixin"
            );
        }
        return Collections.emptyList();
    }
}