package me.gravityio.flair;

import me.gravityio.flair.condition.SoundCondition;
import me.gravityio.flair.condition.sound.ISoundGenerator;
import me.gravityio.flair.data.SoundData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class SoundRegistry<T>  {
    private ISoundGenerator<T> defaultSound;
    public ArrayList<SoundCondition<T>> conditions;
    public HashMap<String, ISoundGenerator<T>> mappings;

    public SoundRegistry(int expectedConditions, int expectedMappings) {
        this(expectedConditions, expectedMappings, null);
    }

    public SoundRegistry(int expectedConditions, int expectedMappings, @Nullable ISoundGenerator<T> defaultSound) {
        this.conditions = new ArrayList<>(expectedConditions);
        this.mappings = new HashMap<>(expectedMappings);
        this.defaultSound = defaultSound;
    }

    /**
     * Sets the default sound for the registry.
     * @param sound The sound
     */
    public void defaultSound(ISoundGenerator<T> sound) {
        this.defaultSound = sound;
    }


    /**
     * Adds a mapping to the registry.
     * @param name The name of the sound
     * @param sound The sound
     */
    public void mapping(String name, ISoundGenerator<T> sound) {
        this.mappings.put(name, sound);
    }

    /**
     * Adds a condition to the registry.
     * @param condition The condition
     */
    public void condition(SoundCondition<T> condition) {
        this.conditions.add(condition);
    }

    /**
     * Resets the registry.
     */
    public void reset() {
        this.mappings.clear();
        this.conditions.clear();
        this.defaultSound = null;
    }

    /**
     * Gets the default sound for the registry.
     * @param obj The object
     * @return The sound
     */
    private SoundData getDefaultSound(T obj) {
        if (this.defaultSound == null) return null;
        return this.defaultSound.getSound(obj);
    }

    /**
     * Gets the sound for the object.
     * @param obj The object
     * @return The sound
     */
    public SoundData getSound(T obj) {
        if (obj == null) return this.getDefaultSound(obj);

        String name = this.getName(obj);
        String metaName = this.getMetaname(obj);
        ISoundGenerator<T> sound = this.mappings.get(metaName);
        if (sound == null) {
            sound = this.mappings.get(name);
        }

        if (sound == null) {
            for (SoundCondition<T> condition : this.conditions) {
                if (!condition.shouldPlay(obj)) continue;
                return condition.getSound(obj);
            }
            return this.getDefaultSound(obj);
        }
        return sound.getSound(obj);
    }

    public abstract String getName(T obj);
    public abstract @Nullable String getMetaname(T obj);
}
