package de.hamburgchimps;

import picocli.CommandLine.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Command
public class LiberateCommand implements Runnable {
    @Override
    public void run() {
        try {
            Files.copy(Paths.get("./NoteStore.sqlite"), Paths.get("./NoteStoreCopy.sqlite"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
