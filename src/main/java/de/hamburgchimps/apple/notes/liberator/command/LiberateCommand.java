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
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

import javax.enterprise.context.control.ActivateRequestContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@QuarkusMain
@Command(version = "2.0.0", description = "Free your data from Apple Notes.", mixinStandardHelpOptions = true)
@SuppressWarnings("unused")
public class LiberateCommand implements Runnable, QuarkusApplication {
    @Option(names = {"-f", "--file"}, description = "Path to Apple Notes sqlite file")
    private File noteStoreDb;
    @Option(names = {"-j", "--json"}, description = "Generate JSON")
    private boolean generateJson;
    @Option(names = {"-m", "--markdown"}, description = "Generate markdown (in early development, please report bugs and request features here -> https://github.com/HamburgChimps/apple-notes-liberator/issues)")
    private boolean generateMarkdown;
    @Spec
    private CommandSpec spec;
    private final CommandLine.IFactory factory;
    private final AgroalDataSource dataSource;
    private final ObjectMapper objectMapper;
    private List<NoteData> parsedNotes;

    public LiberateCommand(CommandLine.IFactory factory, AgroalDataSource dataSource) {
        this.factory = factory;
        this.dataSource = dataSource;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new ProtobufModule());
    }

    @Override
    @ActivateRequestContext
    public void run() {
        if (!this.generateJson && !this.generateMarkdown) {
            spec.commandLine().usage(System.out);
            return;
        }

        dataSource.flush(FlushMode.IDLE);

        this.createOutputDir();

        this.copyNotesDb();

        this.parsedNotes = getAllNotes()
                .stream()
                .map(NoteData::new)
                .toList();

        Log.infov("Parsed {0} notes.", parsedNotes.size());

        if (generateJson) {
            this.generateJson();
        }

        if (generateMarkdown) {
            this.generateMarkdown();
        }

    }

    @Override
    public int run(String... args) {
        return new CommandLine(this, factory)
                .setExecutionExceptionHandler(new ExceptionHandler())
                .execute(args);
    }

    private void createOutputDir() {
        try {
            Files.createDirectories(Paths.get(Constants.OUTPUT_DIRECTORY));
        } catch (IOException e) {
            throw new RuntimeException(String.format(UserMessages.CANNOT_CREATE_OUTPUT_DIRECTORY, Constants.OUTPUT_DIRECTORY), e);
        }
    }

    private void generateJson() {
        try {
            Files.write(Path.of(Constants.NOTES_JSON_PATH), this.objectMapper.writeValueAsString(parsedNotes).getBytes());
        } catch (IOException e) {
            Log.error(e);
        }
    }

    private void generateMarkdown() {
        try {
            Files.createDirectories(Paths.get(Constants.OUTPUT_DIRECTORY, "markdown"));
        } catch (IOException e) {
            throw new RuntimeException(String.format(UserMessages.CANNOT_CREATE_OUTPUT_DIRECTORY, Constants.OUTPUT_DIRECTORY), e);
        }
        int counter = 0;
        for (var note : this.parsedNotes) {
            try {
                String noteTitle = note.getTitle();
                String fileName;
                if (noteTitle == null) {
                    fileName = String.format("unnamed-note-%d.md", ++counter);
                } else {
                    fileName = String.format("%s.md", noteTitle.toLowerCase().replace(" ", "-").replace("/", "-"));
                }
                Files.write(Path.of(Constants.OUTPUT_DIRECTORY, "markdown", fileName), note.toMarkdown().getBytes());
            } catch (IOException e) {
                Log.error(e);
            }
        }
    }

    private void copyNotesDb() {
        if (this.noteStoreDb != null && !this.noteStoreDb.exists()) {
            throw new RuntimeException(String.format(UserMessages.NOTES_DATABASE_DOES_NOT_EXIST_AT_SPECIFIED_PATH, this.noteStoreDb.getPath()));
        }

        this.noteStoreDb = new File(Constants.NOTE_STORE_PATH);

        if (!this.noteStoreDb.exists()) {
            throw new RuntimeException(UserMessages.CANNOT_AUTOMATICALLY_FIND_NOTES_DATABASE);
        }

        try {
            Files.copy(this.noteStoreDb.toPath(),
                    Path.of(Constants.COPIED_NOTE_STORE_PATH),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(String.format(UserMessages.CANNOT_COPY_NOTES_DATABASE, this.noteStoreDb.getPath()), e);
        }
    }

    private List<Note> getAllNotes() {
        return Note.listAll();
    }
}
