package me.gravityio.flair.condition;

public enum SoundGeneratorType {
    PLAY("play"), PLAYBLOCKSOUND("playblocksound");

    public final String str;
    SoundGeneratorType(String str) {
        this.str = str;
    }

    public static SoundGeneratorType fromString(String str) {
        for (SoundGeneratorType type : values()) {
            if (type.str.equals(str)) return type;
        }
        return null;
    }
}
