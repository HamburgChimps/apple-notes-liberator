package de.hamburgchimps.apple.notes.liberator.data;

import com.ciofecaforensics.Notestore.MergableDataProto;
import de.hamburgchimps.apple.notes.liberator.ProtoUtils;
import de.hamburgchimps.apple.notes.liberator.entity.EmbeddedObject;
import io.quarkus.logging.Log;

public class Table implements EmbeddedObjectData {
    public Table(EmbeddedObject embeddedObject) {
        var tableData = ProtoUtils.parseProtoUsingParserFromBytes(MergableDataProto.parser(), embeddedObject.zMergeableData);
        Log.debug("parsed data");
    }
}
