package me.gravityio.flair.data;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.ItemStack;


public class MetaLocation {
    public String namespace;
    public String path;
    public int meta = -1;

    public MetaLocation(String namespace, String path, int meta) {
        this.namespace = namespace;
        this.path = path;
        this.meta = meta;
    }

    public static MetaLocation fromStack(ItemStack stack) {
        if (stack.getHasSubtypes()) {
            return parse(GameData.getItemRegistry().getNameForObject(stack.getItem()) + "@" + stack.getItemDamage());
        }
        return parse(GameData.getItemRegistry().getNameForObject(stack.getItem()));
    }

    public static MetaLocation parse(String input) {
        String namespace = "minecraft";
        String path = input;
        int meta = -1;
        int i = input.indexOf(':');
        if (i != -1) {
            path = input.substring(i + 1);
            if (i > 1) namespace = input.substring(0, i);
        }
        int at = path.lastIndexOf("@");
        if (at != -1) {
            meta = Integer.parseInt(path.substring(at + 1));
            path = path.substring(0, at);
        }

        return new MetaLocation(namespace, path, meta);
    }

    public String toRegistry() {
        return this.namespace + ":" + this.path;
    }

    public boolean hasMeta() {
        return this.meta != -1;
    }

    public String toString() {
        if (this.meta == -1) return this.namespace + ":" + this.path;
        return this.namespace + ":" + this.path + "@" + this.meta;
    }
}
