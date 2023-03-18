package de.hamburgchimps.apple.notes.liberator;

public class Constants {
    public static final String NOTE_STORE_PATH = String.format("%s/Library/Group Containers/group.com.apple.notes/notestore.sqlite", System.getProperty("user.home"));

    public static final String TABLE_DIRECTION_KEY_NAME = "crTableColumnDirection";

    public static final String TABLE_DIRECTION_UNKNOWN = "unknown";
}
