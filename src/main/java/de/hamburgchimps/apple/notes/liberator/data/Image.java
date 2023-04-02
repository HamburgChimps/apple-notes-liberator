package de.hamburgchimps.apple.notes.liberator.data;

import de.hamburgchimps.apple.notes.liberator.entity.NotesCloudObject;

public class Image implements EmbeddedObjectData {
    private final EmbeddedObjectDataType type = EmbeddedObjectDataType.IMAGE;

    public Image(NotesCloudObject notesCloudObject) {

    }

    @Override
    public EmbeddedObjectDataType getType() {
        return type;
    }
}
