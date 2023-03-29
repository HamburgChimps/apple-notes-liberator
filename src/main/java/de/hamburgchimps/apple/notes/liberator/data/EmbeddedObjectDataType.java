package de.hamburgchimps.apple.notes.liberator.data;

import de.hamburgchimps.apple.notes.liberator.entity.NotesCloudObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum EmbeddedObjectDataType {
    TABLE("com.apple.notes.table", Table::new);

    private static final Map<String, EmbeddedObjectDataType> IDENTIFIER_TO_TYPE = new HashMap<>();

    static {
        Arrays.stream(values())
                .forEach((t) -> IDENTIFIER_TO_TYPE.put(t.identifier, t));
    }

    private final String identifier;

    public final Function<NotesCloudObject, ? extends EmbeddedObjectData> embeddedObjectDataCreator;

    EmbeddedObjectDataType(String identifier, Function<NotesCloudObject, ? extends EmbeddedObjectData> embeddedObjectDataCreator) {
        this.identifier = identifier;
        this.embeddedObjectDataCreator = embeddedObjectDataCreator;
    }

    public static EmbeddedObjectDataType byIdentifier(String identifier) {
        return IDENTIFIER_TO_TYPE.get(identifier);
    }
}
