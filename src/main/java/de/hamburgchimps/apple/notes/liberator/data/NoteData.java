package de.hamburgchimps.apple.notes.liberator.data;

import com.ciofecaforensics.Notestore.NoteStoreProto;
import de.hamburgchimps.apple.notes.liberator.entity.Note;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.hamburgchimps.apple.notes.liberator.ByteUtils.decompress;
import static de.hamburgchimps.apple.notes.liberator.ByteUtils.isCompressed;

public class NoteData {
    private final Note note;

    private String text;

    private List<EmbeddedObjectData> embeddedObjects;

    private NoteStoreProto proto;

    private final List<Exception> errors = new ArrayList<>();


    public NoteData(Note n) {
        this.note = n;

        this.parseZData();

        if (this.proto == null) {
            return;
        }

        this.parseText();
        this.parseEmbeddedObjects();
    }

    public String getText() {
        return text;
    }

    public List<EmbeddedObjectData> getEmbeddedObjects() {
        return embeddedObjects;
    }

    public List<Exception> getErrors() {
        return errors;
    }

    private void parseZData() {
        if (this.note.zData == null) {
            return;
        }

        try {
            byte[] decompressed = (isCompressed(this.note.zData))
                    ? decompress(this.note.zData)
                    : this.note.zData;

            this.proto = NoteStoreProto.parseFrom(decompressed);
        } catch (IOException e) {
            this.errors.add(e);
        }
    }

    private void parseText() {
        this.text = this.proto.getDocument().getNote().getNoteText();
    }

    private void parseEmbeddedObjects() {
        // TODO: @next parse table
        this.embeddedObjects = List.of();
    }
}
