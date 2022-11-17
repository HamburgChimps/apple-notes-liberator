package de.hamburgchimps;

import io.quarkus.logging.Log;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Command
@SuppressWarnings("unused")
public class LiberateCommand implements Runnable {
    @Override
    public void run() {
        var noteStoreDb = new File(Constants.NOTE_STORE_PATH);


        if (!noteStoreDb.exists()) {
            throw new RuntimeException(UserMessages.CANT_AUTOMATICALLY_FIND_NOTES_DATABASE);
        }

        try {
            Files.copy(Path.of(Constants.NOTE_STORE_PATH), Path.of("notes.sqlite"));
        } catch (IOException e) {
            Log.error(e);
            throw new RuntimeException(UserMessages.CANT_COPY_NOTES_DATABASE);
            // TODO: @next implement exception handler and print string there
        }

        Log.info("If found, copy to app dir");
        Log.info("Try to read from it");
        Log.info("If reading from it works, start parsing Notes");
    }
}
