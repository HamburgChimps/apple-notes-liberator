package de.hamburgchimps.apple.notes.liberator.command;

import de.hamburgchimps.apple.notes.liberator.Constants;
import de.hamburgchimps.apple.notes.liberator.ExceptionHandler;
import de.hamburgchimps.apple.notes.liberator.UserMessages;
import de.hamburgchimps.apple.notes.liberator.data.NoteData;
import de.hamburgchimps.apple.notes.liberator.entity.Note;
import io.agroal.api.AgroalDataSource;
import io.agroal.api.AgroalDataSource.FlushMode;
import io.quarkus.logging.Log;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import javax.enterprise.context.control.ActivateRequestContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@QuarkusMain
@Command
@SuppressWarnings("unused")
public class LiberateCommand implements Runnable, QuarkusApplication {
    @Option(names = {"-f", "--file"}, description = "Path to Apple Notes sqlite file")
    private String filePath;
    private final CommandLine.IFactory factory;
    private final AgroalDataSource dataSource;

    public LiberateCommand(CommandLine.IFactory factory, AgroalDataSource dataSource) {
        this.factory = factory;
        this.dataSource = dataSource;
    }

    @Override
    @ActivateRequestContext
    public void run() {
        dataSource.flush(FlushMode.IDLE);
        copyNotesDb();

        var parsedNotes = getAllNotes()
                .stream()
                .map(NoteData::new)
                .toList();

        // TODO add some information to readme
        // TODO release v0.1.0?
        // TODO provide some sort of html or csv mapping?
        Log.infov("Parsed {0} notes.", parsedNotes.size());
    }

    @Override
    // TODO make quarkus logging less verbose
    public int run(String... args) {
        return new CommandLine(this, factory)
                .setExecutionExceptionHandler(new ExceptionHandler())
                .execute(args);
    }

    private void copyNotesDb() {
        if (this.filePath == null) {
            this.filePath = Constants.NOTE_STORE_PATH;
        }
        var noteStoreDb = new File(filePath);

        if (!noteStoreDb.exists()) {
            // TODO modify message here
            throw new RuntimeException(UserMessages.CANT_AUTOMATICALLY_FIND_NOTES_DATABASE);
        }

        try {
            Files.copy(Path.of(Constants.NOTE_STORE_PATH),
                    Path.of("notes.sqlite"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(UserMessages.CANT_COPY_NOTES_DATABASE);
        }
    }

    private List<Note> getAllNotes() {
        return Note.listAll();
    }
}
