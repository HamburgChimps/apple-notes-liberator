package de.hamburgchimps;

import de.hamburgchimps.entity.Note;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@QuarkusMain
@Command
@SuppressWarnings("unused")
public class LiberateCommand implements Runnable, QuarkusApplication {
    @Inject
    CommandLine.IFactory factory;

    @Override
    @ActivateRequestContext
    public void run() {
        copyNotesDb();

        List<Note> notes = Note.listAll();
    }

    @Override
    public int run(String... args) {
        return new CommandLine(this, factory)
                .setExecutionExceptionHandler(new ExceptionHandler())
                .execute(args);
    }

    private void copyNotesDb() {
        var noteStoreDb = new File(Constants.NOTE_STORE_PATH);

        if (!noteStoreDb.exists()) {
            throw new RuntimeException(UserMessages.CANT_AUTOMATICALLY_FIND_NOTES_DATABASE);
        }

        try {
            Files.copy(Path.of(Constants.NOTE_STORE_PATH), Path.of("notes.sqlite"));
        } catch (FileAlreadyExistsException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(UserMessages.CANT_COPY_NOTES_DATABASE);
        }
    }
}
