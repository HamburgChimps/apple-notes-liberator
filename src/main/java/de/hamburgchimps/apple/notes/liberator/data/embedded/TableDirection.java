package de.hamburgchimps.apple.notes.liberator.data.embedded;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum TableDirection {
    LEFT_TO_RIGHT("CRTableColumnDirectionLeftToRight"),
    RIGHT_TO_LEFT("CRTableColumnDirectionRightToLeft"),
    UNKNOWN("Unknown");

    static final Map<String, TableDirection> DIRECTION_IDENTIFIER_TO_DIRECTION = new HashMap<>();

    static {
        Arrays.stream(values())
                .forEach((t) -> DIRECTION_IDENTIFIER_TO_DIRECTION.put(t.identifier, t));
    }


    private final String identifier;

    TableDirection(String identifier) {
        this.identifier = identifier;
    }
}
