package de.hamburgchimps.apple.notes.liberator.command;

import com.ciofecaforensics.Notestore.NoteStoreProto;
import com.ciofecaforensics.Notestore.Document;
import com.ciofecaforensics.Notestore.Note;

import de.hamburgchimps.apple.notes.liberator.Constants;
import de.hamburgchimps.apple.notes.liberator.ExceptionHandler;
import de.hamburgchimps.apple.notes.liberator.UserMessages;
import de.hamburgchimps.apple.notes.liberator.service.NoteService;
import io.agroal.api.AgroalDataSource;
import io.agroal.api.AgroalDataSource.FlushMode;
import io.quarkus.logging.Log;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@QuarkusMain
@Command
@SuppressWarnings("unused")
public class LiberateCommand implements Runnable, QuarkusApplication {
    @Inject
    CommandLine.IFactory factory;

    @Inject
    AgroalDataSource dataSource;

    @Inject
    NoteService noteService;

    @Override
    @ActivateRequestContext
    public void run() {
        dataSource.flush(FlushMode.IDLE);
        copyNotesDb();

        var attributeRuns = noteService
                .getAllNotes()
                .parallelStream()
                .filter((n) -> n.zNote == Constants.ZNOTE_ID_WITH_EMBEDDED_TABLE)
                .map(noteService::parseZData)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(NoteStoreProto::getDocument)
                .map(Document::getNote)
                .map(Note::getAttributeRunList)
                .peek((attributeRunList) -> Log.debug(attributeRunList))
                .toList();
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
            Files.copy(Path.of(Constants.NOTE_STORE_PATH),
                    Path.of("notes.sqlite"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(UserMessages.CANT_COPY_NOTES_DATABASE);
        }
    }
}
