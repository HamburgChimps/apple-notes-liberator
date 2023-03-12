package de.hamburgchimps.apple.notes.liberator.data;

import com.ciofecaforensics.Notestore.MergableDataProto;
import de.hamburgchimps.apple.notes.liberator.ProtoUtils;
import de.hamburgchimps.apple.notes.liberator.entity.EmbeddedObject;
import io.quarkus.logging.Log;

public class Table implements EmbeddedObjectData {
    public Table(EmbeddedObject embeddedObject) {
        var result = ProtoUtils.parseProtoUsingParserFromBytes(MergableDataProto.parser(), embeddedObject.zMergeableData);

        if (result.isError()) {
            Log.error("failed to parse table, see stacktrace starting on next line for more information");
            result.error().printStackTrace();
            return;
        }

        var proto = result.get();

        var data = proto
                .getMergableDataObject()
                .getMergeableDataObjectData();

        var keys = data.getMergeableDataObjectKeyItemList();
        var types = data.getMergeableDataObjectTypeItemList();
        var uuids = data.getMergeableDataObjectUuidItemList();
        var tables = proto
                .getMergableDataObject()
                .getMergeableDataObjectData()
                .getMergeableDataObjectEntryList();

        Log.debug("parsing...");
    }
}
