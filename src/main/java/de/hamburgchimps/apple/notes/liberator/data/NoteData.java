package de.hamburgchimps.apple.notes.liberator.data;

import com.ciofecaforensics.Notestore;
import com.ciofecaforensics.Notestore.NoteStoreProto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.hamburgchimps.apple.notes.liberator.Constants;
import de.hamburgchimps.apple.notes.liberator.ProtoUtils;
import de.hamburgchimps.apple.notes.liberator.UserMessages;
import de.hamburgchimps.apple.notes.liberator.data.embedded.EmbeddedObjectData;
import de.hamburgchimps.apple.notes.liberator.data.embedded.File;
import de.hamburgchimps.apple.notes.liberator.data.embedded.Table;
import de.hamburgchimps.apple.notes.liberator.data.format.BoldText;
import de.hamburgchimps.apple.notes.liberator.data.format.Link;
import de.hamburgchimps.apple.notes.liberator.data.format.Paragraph;
import de.hamburgchimps.apple.notes.liberator.entity.Note;
import de.hamburgchimps.apple.notes.liberator.entity.NotesCloudObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NoteData implements Markdownable {
    private final Note note;
    private String title;
    private String folder;
    private String text;
    private final List<Markdownable> markdownItems = new ArrayList<>();
    private final List<EmbeddedObjectData> embeddedObjects = new ArrayList<>();
    private final List<Link> links = new ArrayList<>();
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
        this.folder = (noteObject.zFolder != null) ? noteObject.zFolder.zTitle2 : "unknown";
        this.text = this.getProtoNote().getNoteText();
        this.parseFormattingInformation();
    }

    @Override
    public String toMarkdown() {
        var markdownBuilder = new StringBuilder()
                .append("#")
                .append(" ")
                .append(this.title)
                .append("\n\n");

        this.markdownItems
                .stream()
                .map(Markdownable::toMarkdown)
                .forEach(markdownBuilder::append);

        return markdownBuilder.toString();
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

    public List<Link> getLinks() {
        return links;
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

    private void parseFormattingInformation() {
        this.markdownItems.clear();
        this.embeddedObjects.clear();
        this.links.clear();

        var positionTracker = 0;

        for (var attributeRun : this.getProtoNote().getAttributeRunList()) {
            Markdownable formattingItem = null;
            var endOfFormattingItem = positionTracker + attributeRun.getLength();
            var formattingItemText = this.getText().substring(positionTracker, endOfFormattingItem);
            if (attributeRun.hasAttachmentInfo()) {
                var embeddedObject = this.parseEmbeddedObject(attributeRun.getAttachmentInfo());
                embeddedObject.ifPresent(this.embeddedObjects::add);
                formattingItem = embeddedObject.orElse(null);
            }
            if (attributeRun.hasLink()) {
                var link = new Link(formattingItemText, attributeRun.getLink());
                this.links.add(link);
                formattingItem = link;
            }
            if (attributeRun.hasFontWeight()) {
                formattingItem = new BoldText(formattingItemText);
            }
            if (formattingItem == null) {
                formattingItem = new Paragraph(formattingItemText);
            }
            this.markdownItems.add(formattingItem);
            positionTracker += attributeRun.getLength();
        }
    }

    private Optional<EmbeddedObjectData> parseEmbeddedObject(Notestore.AttachmentInfo attachmentInfo) {
        var identifier = attachmentInfo.getAttachmentIdentifier();
        NotesCloudObject notesCloudObject = NotesCloudObject
                .find("zIdentifier", identifier)
                .firstResult();

        if (notesCloudObject == null) {
            // We are most likely dealing with a deleted note that has some dead references
            // hanging around in the database still. There is not much we can do with these.
            this.errors.add(new RuntimeException(String.format(UserMessages.EMBEDDED_OBJECT_PARSE_ERROR_IDENTIFIER_DOES_NOT_EXIST, identifier)));
            return Optional.empty();
        }

        var typeIdentifier = notesCloudObject.zTypeUti;

        if (typeIdentifier == null || typeIdentifier.isEmpty()) {
            this.errors.add(new RuntimeException(String.format(UserMessages.EMBEDDED_OBJECT_PARSE_ERROR_NO_TYPE_IDENTIFIER, identifier)));
            return Optional.empty();
        }

        // are we dealing with a table?
        if (typeIdentifier.equals(Constants.TABLE_IDENTIFIER)) {
            return Optional.of(new Table(notesCloudObject));
        }

        // otherwise assume the embedded object is a file
        return Optional.of(new File(notesCloudObject));
    }

    private Notestore.Note getProtoNote() {
        return this.proto.getDocument().getNote();
    }
}
