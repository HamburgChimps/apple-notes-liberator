package de.hamburgchimps.apple.notes.liberator;

public class UserMessages {
    public static final String CANT_AUTOMATICALLY_FIND_NOTES_DATABASE = "Can't automatically find notes database, you will have to manually provide the file path via the -f (--file) option.";
    public static final String CANT_COPY_NOTES_DATABASE = String.format("Can't copy notes database, do you have read and execute permissions for %s?", Constants.NOTE_STORE_PATH);
}
