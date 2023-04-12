package de.hamburgchimps.apple.notes.liberator.data.format;

import de.hamburgchimps.apple.notes.liberator.data.Markdownable;

public record Link(String text, String url) implements Markdownable {
    @Override
    public String toMarkdown() {
        return String.format("[%s](%s)", text, url);
    }
}
