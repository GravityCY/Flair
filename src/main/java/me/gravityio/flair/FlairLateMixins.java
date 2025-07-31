package me.gravityio.flair;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.*;

@SideOnly(Side.CLIENT)
@LateMixin
public class FlairLateMixins implements ILateMixinLoader {
    @Override
    public String getMixinConfig() {
        return "mixins.flair.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        if (FMLLaunchHandler.side() == Side.CLIENT) {
            return Arrays.asList("NEIRecipeClickMixin", "NEIUsageClickMixin");
        }
        return Collections.emptyList();
    }
}