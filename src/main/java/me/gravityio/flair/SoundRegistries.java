package me.gravityio.flair;

import me.gravityio.flair.data.SoundData;

import java.util.HashMap;
import java.util.Map;

// TODO: UHHHHHH, EACH ITEM IS ASSOCIATED WITH MULTIPLE SOUND TYPES,
//  WHY SEGREGATE THE MAPS? WHY NOT A SINGULAR HASHMAP THAT POINTS TO MULTIPLE SOUNDS?
public class SoundRegistries {
    public HashMap<String, SoundRegistry<?>> REGISTRIES = new HashMap<>(8);

    public static SoundRegistries INSTANCE = new SoundRegistries();

    public <Y extends SoundRegistry<?>> Y register(String name, Y registry) {
        this.REGISTRIES.put(name, registry);
        return registry;
    }

    public <T> SoundData getSound(String name, T obj, Class<? extends SoundRegistry<T>> registry) {
        SoundRegistry<?> rawRegistry = this.REGISTRIES.get(name);
        if (rawRegistry == null) {
            throw new IllegalArgumentException("No registry found for name: " + name);
        }
        return registry.cast(rawRegistry).getSound(obj);
    }

    public void reset() {
        for (Map.Entry<String, SoundRegistry<?>> stringSoundRegistryEntry : this.REGISTRIES.entrySet()) {
            stringSoundRegistryEntry.getValue().reset();
        }
    }
}
