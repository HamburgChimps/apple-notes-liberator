package de.hamburgchimps.apple.notes.liberator.data;

import de.hamburgchimps.apple.notes.liberator.Constants;
import de.hamburgchimps.apple.notes.liberator.entity.NotesCloudObject;

import java.nio.file.Path;

public class Image implements EmbeddedObjectData {
    private final EmbeddedObjectDataType type = EmbeddedObjectDataType.IMAGE;
    private Path originalFilePath;

    public Image(NotesCloudObject notesCloudObject) {
        if (notesCloudObject.zMedia == null || notesCloudObject.zMedia.zIdentifier == null || notesCloudObject.zMedia.zFileName == null) {
            // We are probably dealing with a recently deleted notes object and
            // as such cannot extract any meaningful data from it.
            return;
        }
        this.originalFilePath = Path.of(Constants.NOTE_STORE_MEDIA_PATH, notesCloudObject.zMedia.zIdentifier, notesCloudObject.zMedia.zFileName);
    }

    @Override
    public EmbeddedObjectDataType getType() {
        return type;
    }

    public Path getOriginalFilePath() {
        return originalFilePath;
    }
}
