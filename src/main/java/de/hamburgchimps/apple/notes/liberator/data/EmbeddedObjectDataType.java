package de.hamburgchimps.apple.notes.liberator.data;

import de.hamburgchimps.apple.notes.liberator.data.parser.EmbeddedObjectParser;
import de.hamburgchimps.apple.notes.liberator.data.parser.TableParser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum EmbeddedObjectDataType {
    TABLE("com.apple.notes.table", new TableParser());

    private static final Map<String, EmbeddedObjectDataType> IDENTIFIER_TO_TYPE = new HashMap<>();

    static {
        Arrays.stream(values())
                .forEach((t) -> IDENTIFIER_TO_TYPE.put(t.identifier, t));
    }

    private final String identifier;

    private final EmbeddedObjectParser parser;

    EmbeddedObjectDataType(String identifier, EmbeddedObjectParser parser) {
        this.identifier = identifier;
        this.parser = parser;
    }

    public static EmbeddedObjectDataType byIdentifier(String identifier) {
        return IDENTIFIER_TO_TYPE.get(identifier);
    }
}
