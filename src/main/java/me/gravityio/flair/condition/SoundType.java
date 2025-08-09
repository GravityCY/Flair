package me.gravityio.flair.condition;

public enum SoundType {
    ITEM("item"),
    BLOCK("block"),
    CRAFT("craft"),
    DROP("drop"),
    SWING("swing");
    private final String str;

    SoundType(String str) {
        this.str = str;
    }

    public static SoundType fromString(String type) {
        for (SoundType ifType : values()) {
            if (ifType.str.equals(type)) return ifType;
        }
        return null;
    }
}
