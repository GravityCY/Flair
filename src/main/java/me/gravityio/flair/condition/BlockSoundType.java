package me.gravityio.flair.condition;

public enum BlockSoundType {
    BREAK("break"),
    STEP("step");

    private final String user;

    BlockSoundType(String user) {
        this.user = user;
    }

    public static BlockSoundType fromString(String str) {
        for (BlockSoundType type : values()) {
            if (type.user.equals(str)) return type;
        }
        return null;
    }
}