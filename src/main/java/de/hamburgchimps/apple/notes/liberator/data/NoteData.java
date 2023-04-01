package de.hamburgchimps.apple.notes.liberator.data;

import com.ciofecaforensics.Notestore;
import com.ciofecaforensics.Notestore.NoteStoreProto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.hamburgchimps.apple.notes.liberator.ProtoUtils;
import de.hamburgchimps.apple.notes.liberator.UserMessages;
import de.hamburgchimps.apple.notes.liberator.entity.NotesCloudObject;
import de.hamburgchimps.apple.notes.liberator.entity.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NoteData {
    private final Note note;
    private String title;
    private String folder;
    private String text;
    private List<EmbeddedObjectData> embeddedObjects;
    private NoteStoreProto proto;
    private final List<RuntimeException> errors = new ArrayList<>();

    public NoteData(Note n) {
        this.note = n;

        this.parseZData();

        if (this.proto == null) {
            return;
        }

        NotesCloudObject noteObject = NotesCloudObject.findById(this.note.zNote);

        this.title = noteObject.zTitle1;
        this.folder = noteObject.zFolder.zTitle2;
        this.text = this.getProtoNote().getNoteText();
        this.parseEmbeddedObjects();
    }

    public String getTitle() {
        return title;
    }
    public String getFolder() {
        return folder;
    }
    public String getText() {
        return text;
    }

    public List<EmbeddedObjectData> getEmbeddedObjects() {
        return embeddedObjects;
    }
    @JsonIgnore
    public List<RuntimeException> getErrors() {
        return errors;
    }

    private void parseZData() {
        var parseResult = ProtoUtils.parseProtoUsingParserFromBytes(NoteStoreProto.parser(), this.note.zData);

        if (parseResult.isError()) {
            this.errors.add(parseResult.error());
            return;
        }

        this.proto = parseResult.get();
    }

    private void parseEmbeddedObjects() {
        this.embeddedObjects = this.getProtoNote()
                .getAttributeRunList()
                .stream()
                .filter(Notestore.AttributeRun::hasAttachmentInfo)
                .map(Notestore.AttributeRun::getAttachmentInfo)
                .map(this::parseEmbeddedObject)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<EmbeddedObjectData> parseEmbeddedObject(Notestore.AttachmentInfo attachmentInfo) {
        NotesCloudObject notesCloudObject = NotesCloudObject
                .find("zIdentifier", attachmentInfo.getAttachmentIdentifier())
                .firstResult();

        var typeIdentifier = notesCloudObject.zTypeUti;

        if (typeIdentifier == null || typeIdentifier.isEmpty()) {
            this.errors.add(new RuntimeException(String.format(UserMessages.EMBEDDED_OBJECT_PARSE_ERROR_NO_TYPE_IDENTIFIER, attachmentInfo.getAttachmentIdentifier())));
            return Optional.empty();
        }

        EmbeddedObjectDataType type = EmbeddedObjectDataType
                .byIdentifier(typeIdentifier);

        if (type == null) {
            this.errors.add(new RuntimeException(String.format(UserMessages.EMBEDDED_OBJECT_PARSE_ERROR_TYPE_NOT_YET_SUPPORTED, notesCloudObject.zTypeUti)));
            return Optional.empty();
        }

        return Optional.of(type.embeddedObjectDataCreator.apply(notesCloudObject));
    }

    private Notestore.Note getProtoNote() {
        return this.proto.getDocument().getNote();
    }
}
