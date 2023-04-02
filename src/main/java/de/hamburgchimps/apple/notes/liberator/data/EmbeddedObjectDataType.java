package de.hamburgchimps.apple.notes.liberator.data;

import de.hamburgchimps.apple.notes.liberator.entity.NotesCloudObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public enum EmbeddedObjectDataType {
    TABLE(Table::new, "com.apple.notes.table"),
    IMAGE(Image::new, "public.png", "public.jpeg");

    private static final Map<String, EmbeddedObjectDataType> IDENTIFIER_TO_TYPE = new HashMap<>();

    static {
        Arrays.stream(values())
                .forEach((t) -> t.identifiers.forEach((i) -> IDENTIFIER_TO_TYPE.put(i, t)));
    }

    private final List<String> identifiers;

    public final Function<NotesCloudObject, ? extends EmbeddedObjectData> embeddedObjectDataCreator;

    EmbeddedObjectDataType(Function<NotesCloudObject, ? extends EmbeddedObjectData> embeddedObjectDataCreator, String... identifiers) {
        this.identifiers = Arrays.asList(identifiers);
        this.embeddedObjectDataCreator = embeddedObjectDataCreator;
    }

    public static EmbeddedObjectDataType byIdentifier(String identifier) {
        return IDENTIFIER_TO_TYPE.get(identifier);
    }
}
