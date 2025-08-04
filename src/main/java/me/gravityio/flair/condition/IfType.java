package me.gravityio.flair.condition;

public enum IfType {
    ITEM("item"), BLOCK("block");

    private final String str;

    IfType(String str) {
        this.str = str;
    }

    public static IfType fromString(String type) {
        for (IfType ifType : values()) {
            if (ifType.str.equals(type)) return ifType;
        }
        return null;
    }
}
