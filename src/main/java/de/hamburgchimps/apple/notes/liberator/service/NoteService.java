package de.hamburgchimps.apple.notes.liberator.service;

import de.hamburgchimps.apple.notes.liberator.entity.Note;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class NoteService {
    public List<Note> getAllNotes() {
        return Note.listAll();
    }
}
