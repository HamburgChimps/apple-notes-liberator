package de.hamburgchimps.apple.notes.liberator.data;

import com.ciofecaforensics.Notestore;
import com.ciofecaforensics.Notestore.NoteStoreProto;
import de.hamburgchimps.apple.notes.liberator.ProtoUtils;
import de.hamburgchimps.apple.notes.liberator.entity.EmbeddedObject;
import de.hamburgchimps.apple.notes.liberator.entity.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NoteData {
    private final Note note;

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

        this.parseText();
        this.parseEmbeddedObjects();
    }

    public String getText() {
        return text;
    }

    public List<EmbeddedObjectData> getEmbeddedObjects() {
        return embeddedObjects;
    }

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

    private void parseText() {
        this.text = this.getProtoNote().getNoteText();
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
        EmbeddedObject embeddedObject = EmbeddedObject
                .find("zIdentifier", attachmentInfo.getAttachmentIdentifier())
                .firstResult();

        var typeIdentifier = embeddedObject.zTypeUti;

        if (typeIdentifier.isEmpty()) {
            this.errors.add(new RuntimeException(String.format("Cannot parse embedded object with identifier \"%s\": no type identifier present", attachmentInfo.getAttachmentIdentifier())));
            return Optional.empty();
        }

        EmbeddedObjectDataType type = EmbeddedObjectDataType
                .byIdentifier(typeIdentifier);

        if (type == null) {
            this.errors.add(new RuntimeException(String.format("Parsing for embedded objects of type \"%s\" is not yet supported", embeddedObject.zTypeUti)));
            return Optional.empty();
        }

        return Optional.of(type.embeddedObjectDataCreator.apply(embeddedObject));
    }

    private Notestore.Note getProtoNote() {
        return this.proto.getDocument().getNote();
    }
}
