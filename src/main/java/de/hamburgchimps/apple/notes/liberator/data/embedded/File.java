package de.hamburgchimps.apple.notes.liberator.data.embedded;

import de.hamburgchimps.apple.notes.liberator.Constants;
import de.hamburgchimps.apple.notes.liberator.UserMessages;
import de.hamburgchimps.apple.notes.liberator.entity.NotesCloudObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class File implements EmbeddedObjectData {
    private final EmbeddedObjectDataType type = EmbeddedObjectDataType.FILE;
    private String fileName;
    private Path noteStoreFilePath;
    private Path outputFilePath;
    private final List<RuntimeException> errors = new ArrayList<>();

    public File(NotesCloudObject notesCloudObject) {
        if (notesCloudObject.zMedia == null || notesCloudObject.zMedia.zIdentifier == null || notesCloudObject.zMedia.zFileName == null) {
            // We are probably dealing with a recently deleted notes object and
            // as such cannot extract any meaningful data from it.
            return;
        }
        this.fileName = notesCloudObject.zMedia.zFileName;
        this.noteStoreFilePath = Path.of(Constants.NOTE_STORE_MEDIA_PATH, notesCloudObject.zMedia.zIdentifier, this.fileName);
        this.outputFilePath = Path.of(Constants.OUTPUT_DIRECTORY, this.fileName);

        this.copyToOutputDir();
    }

    @Override
    public String toMarkdown() {
        if (this.fileName == null) {
            return null;
        }
        // TODO improve this
        if (this.fileName.contains(".png") || this.fileName.contains(".jpeg")) {
            return String.format("![%s](../%s)", this.fileName, this.fileName);
        }
        return String.format("[%s](../%s)", this.fileName, this.fileName);
    }

    @Override
    public EmbeddedObjectDataType getType() {
        return type;
    }

    public String getData() {
        return fileName;
    }

    private void copyToOutputDir() {
        try {
            Files.copy(this.noteStoreFilePath, this.outputFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            this.errors.add(new RuntimeException(String.format(UserMessages.CANNOT_COPY_EMBEDDED_FILE, this.noteStoreFilePath, this.outputFilePath), e));
        }
    }
}
