package de.hamburgchimps.apple.notes.liberator.data.format;

import de.hamburgchimps.apple.notes.liberator.data.Markdownable;

public record Paragraph(String text) implements Markdownable {
    @Override
    public String toMarkdown() {
        return String.format("%s%n", text);
    }
}
