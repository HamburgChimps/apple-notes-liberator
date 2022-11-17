package de.hamburgchimps;

import io.quarkus.logging.Log;
import picocli.CommandLine.Command;

import java.io.File;

@Command
@SuppressWarnings("unused")
public class LiberateCommand implements Runnable {
    @Override
    public void run() {
        Log.info("Look for NoteStore.sqlite file in user dir");
        var noteStoreDb = new File(Constants.NOTE_STORE_PATH);
        if (!noteStoreDb.exists()) {
            throw new RuntimeException("Can't find notes file");
        }
        Log.info("If found, copy to app dir");
        Log.info("Try to read from it");
        Log.info("If reading from it works, start parsing Notes");
    }
}
