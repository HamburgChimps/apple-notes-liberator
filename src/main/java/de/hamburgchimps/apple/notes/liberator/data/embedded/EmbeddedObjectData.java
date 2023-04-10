package de.hamburgchimps.apple.notes.liberator.data.embedded;

import de.hamburgchimps.apple.notes.liberator.data.Markdownable;

public interface EmbeddedObjectData extends Markdownable {
    EmbeddedObjectDataType getType();
}
