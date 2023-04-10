package de.hamburgchimps.apple.notes.liberator.data.format;

import de.hamburgchimps.apple.notes.liberator.data.Markdownable;

public record BoldText(int start, int end) implements Markdownable {
    @Override
    public String toMarkdown() {
        return null;
    }
}
