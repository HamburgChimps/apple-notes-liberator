package de.hamburgchimps.apple.notes.liberator.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
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
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.enterprise.context.control.ActivateRequestContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@QuarkusMain
@Command(version = "0.2.0", description = "Free your data from Apple Notes.", mixinStandardHelpOptions = true)
@SuppressWarnings("unused")
public class LiberateCommand implements Runnable, QuarkusApplication {
    @Option(names = {"-f", "--file"}, description = "Path to Apple Notes sqlite file")
    private File noteStoreDb;
    private final CommandLine.IFactory factory;
    private final AgroalDataSource dataSource;
    private final ObjectMapper objectMapper;

    public LiberateCommand(CommandLine.IFactory factory, AgroalDataSource dataSource) {
        this.factory = factory;
        this.dataSource = dataSource;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new ProtobufModule());
    }

    @Override
    @ActivateRequestContext
    public void run() {
        dataSource.flush(FlushMode.IDLE);

        this.copyNotesDb();

        var parsedNotes = getAllNotes()
                .stream()
                .map(NoteData::new)
                .toList();

        Log.infov("Parsed {0} notes.", parsedNotes.size());

        try {
            Files.write(Path.of(Constants.NOTES_JSON_PATH), objectMapper.writeValueAsString(parsedNotes).getBytes());
        } catch (IOException e) {
            Log.error(e);
        }
    }

    @Override
    public int run(String... args) {
        return new CommandLine(this, factory)
                .setExecutionExceptionHandler(new ExceptionHandler())
                .execute(args);
    }

    private void copyNotesDb() {
        if (this.noteStoreDb != null && !this.noteStoreDb.exists()) {
            throw new RuntimeException(String.format(UserMessages.NOTES_DATABASE_DOES_NOT_EXIST_AT_SPECIFIED_PATH, this.noteStoreDb.getPath()));
        }

        this.noteStoreDb = new File(Constants.NOTE_STORE_PATH);

        if (!this.noteStoreDb.exists()) {
            throw new RuntimeException(UserMessages.CANT_AUTOMATICALLY_FIND_NOTES_DATABASE);
        }

        try {
            Files.copy(this.noteStoreDb.toPath(),
                    Path.of(Constants.COPIED_NOTE_STORE_PATH),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(String.format(UserMessages.CANT_COPY_NOTES_DATABASE, this.noteStoreDb.getPath()));
        }
    }

    private List<Note> getAllNotes() {
        return Note.listAll();
    }
}
