package de.hamburgchimps.apple.notes.liberator.data.format;

public record Link(String text, String url) implements Markdownable {
    @Override
    public String toMarkdown() {
        return null;
    }
}
