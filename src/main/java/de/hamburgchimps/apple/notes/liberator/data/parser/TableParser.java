package de.hamburgchimps.apple.notes.liberator.data.parser;

import de.hamburgchimps.apple.notes.liberator.data.Table;

public class TableParser implements EmbeddedObjectParser {
    @Override
    public Table parse() {
        return new Table();
    }
}
