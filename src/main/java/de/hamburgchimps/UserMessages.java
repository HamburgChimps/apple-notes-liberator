package de.hamburgchimps;

import picocli.CommandLine.Help.Ansi;

public class UserMessages {

    public static final String CANT_AUTOMATICALLY_FIND_NOTES_DATABASE = createErrorString("Can't automatically find notes database, you will have to manually provide the file path via the -f (--file) option.");

    public static final String CANT_COPY_NOTES_DATABASE = createErrorString(String.format("Can't copy notes database, do you have read and execute permissions for %s?", Constants.NOTE_STORE_PATH));

    private static String createErrorString(String str) {
        return Ansi.AUTO.string(String.format("@|bold,red %s |@", str));
    }
}
