package de.hamburgchimps.apple.notes.liberator;

public class Constants {
    public static final String NOTE_STORE_PATH = String.format("%s/Library/Group Containers/group.com.apple.notes/notestore.sqlite", System.getProperty("user.home"));
    public static final String TABLE_DIRECTION_KEY_NAME = "crTableColumnDirection";
    public static final String TABLE_ROWS_KEY_NAME = "crRows";
    public static final String TABLE_COLUMNS_KEY_NAME = "crColumns";
    public static final String TABLE_CELLS_KEY_NAME = "cellColumns";
    public static final String TABLE_ROOT_IDENTIFIER = "com.apple.notes.ICTable";
    public static final String COPIED_NOTE_STORE_PATH = "notes.sqlite";
    public static final String NOTES_JSON_PATH = "notes.json";
}
