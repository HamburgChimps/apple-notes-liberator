package de.hamburgchimps.apple.notes.liberator;

public class UserMessages {
    public static final String CANNOT_CREATE_OUTPUT_DIRECTORY = "Cannot create output directory (%s). Cannot continue without an output directory.";
    public static final String CANNOT_COPY_EMBEDDED_FILE = "Cannot copy embedded file from original location (%s) to output directory (%s)";
    public static final String NOTES_DATABASE_DOES_NOT_EXIST_AT_SPECIFIED_PATH = "No file exists at given path (%s), please check the file path you specified or leave this option out to attempt automatic resolution of the notes database.";
    public static final String CANNOT_AUTOMATICALLY_FIND_NOTES_DATABASE = "Cannot automatically find notes database, you will have to manually provide the file path via the -f (--file) option.";
    public static final String CANNOT_COPY_NOTES_DATABASE = "Cannot copy notes database, do you have read and execute permissions for %s?";
    public static final String EMBEDDED_OBJECT_PARSE_ERROR_NO_TYPE_IDENTIFIER = "Cannot parse embedded object with identifier \"%s\": no type identifier present";
    public static final String TABLE_PARSE_ERROR_CANT_FIND_ROOT = "Failed to parse table: unable to find root table";
    public static final String TABLE_PARSE_ERROR_CANT_PARSE_PROTO = "Failed to parse table";
}
