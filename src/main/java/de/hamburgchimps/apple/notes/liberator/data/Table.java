package de.hamburgchimps.apple.notes.liberator.data;

import com.ciofecaforensics.Notestore.MergableDataProto;
import de.hamburgchimps.apple.notes.liberator.Constants;
import de.hamburgchimps.apple.notes.liberator.ProtoUtils;
import de.hamburgchimps.apple.notes.liberator.entity.EmbeddedObject;
import io.quarkus.logging.Log;

public class Table implements EmbeddedObjectData {

    private String direction;

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

        data.getMergeableDataObjectEntryList().forEach((entry) -> {
            // each entry is a table object
            // if there is a custom map
            // and first key is "crTableColumnDirection" + 1 ?
            // then first string value is table direction

            // TODO: clean this up!
            // TODO: why + 1??
            if (entry.hasCustomMap() && entry.getCustomMap().getMapEntry(0).getKey() == keys.indexOf(Constants.TABLE_DIRECTION_KEY_NAME) + 1) {
                this.direction = entry.getCustomMap().getMapEntry(0).getValue().getStringValue();
            }
        });

        Log.debug(this.direction);
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
