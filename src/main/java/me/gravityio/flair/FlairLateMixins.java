package me.gravityio.flair;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;

@SideOnly(Side.CLIENT)
@LateMixin
public class FlairLateMixins implements ILateMixinLoader {
    @Override
    public String getMixinConfig() {
        return "mixins.flair.compat.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();
        if (loadedMods.contains("NotEnoughItems")) {
            mixins.add("NEIRecipeClickMixin");
            mixins.add("NEIBookmarkMixin");
            mixins.add("NEIUsageClickMixin");
            mixins.add("NEITypingMixin");
            mixins.add("NEIRecipeTypingMixin");
        }
        return mixins;
    }
}