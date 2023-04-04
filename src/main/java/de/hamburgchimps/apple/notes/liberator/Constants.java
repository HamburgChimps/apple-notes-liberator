package de.hamburgchimps.apple.notes.liberator;

public class Constants {
    public static final String NOTE_STORE_CONTAINER_PATH = String.format("%s/library/group containers/group.com.apple.notes", System.getProperty("user.home"));
    public static final String NOTE_STORE_PATH = String.format("%s/notestore.sqlite", NOTE_STORE_CONTAINER_PATH);
    public static final String NOTE_STORE_MEDIA_PATH = String.format("%s/media", NOTE_STORE_CONTAINER_PATH);
    public static final String TABLE_DIRECTION_KEY_NAME = "crTableColumnDirection";
    public static final String TABLE_ROWS_KEY_NAME = "crRows";
    public static final String TABLE_COLUMNS_KEY_NAME = "crColumns";
    public static final String TABLE_CELLS_KEY_NAME = "cellColumns";
    public static final String TABLE_IDENTIFIER = "com.apple.notes.table";
    public static final String TABLE_ROOT_IDENTIFIER = "com.apple.notes.ICTable";
    public static final String OUTPUT_DIRECTORY = "liberated-notes";
    public static final String COPIED_NOTE_STORE_PATH = String.format("%s/notes.sqlite", OUTPUT_DIRECTORY);
    public static final String NOTES_JSON_PATH = String.format("%s/notes.json", OUTPUT_DIRECTORY);
}
