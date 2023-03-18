package de.hamburgchimps.apple.notes.liberator.data;

import com.ciofecaforensics.Notestore.ObjectID;
import com.ciofecaforensics.Notestore.MapEntry;
import com.ciofecaforensics.Notestore.MergableDataProto;
import com.ciofecaforensics.Notestore.MergeableDataObjectEntry;
import com.ciofecaforensics.Notestore.MergeableDataObjectMap;
import de.hamburgchimps.apple.notes.liberator.ProtoUtils;
import de.hamburgchimps.apple.notes.liberator.entity.EmbeddedObject;
import io.quarkus.logging.Log;

import java.util.ArrayList;
import java.util.List;

import static de.hamburgchimps.apple.notes.liberator.Constants.TABLE_DIRECTION_KEY_NAME;
import static de.hamburgchimps.apple.notes.liberator.Constants.TABLE_DIRECTION_UNKNOWN;

public class Table implements EmbeddedObjectData {

    private String direction;
    private final List<MergeableDataObjectEntry> tables = new ArrayList<>();
    private final List<RuntimeException> errors = new ArrayList<>();

    public Table(EmbeddedObject embeddedObject) {
        var result = ProtoUtils.parseProtoUsingParserFromBytes(MergableDataProto.parser(), embeddedObject.zMergeableData);

        if (result.isError()) {
            Log.error("failed to parse table, see stacktrace starting on next line for more information");
            result.error().printStackTrace();
            this.errors.add(result.error());
            return;
        }

        var proto = result.get();

        var data = proto
                .getMergableDataObject()
                .getMergeableDataObjectData();

        var keys = data.getMergeableDataObjectKeyItemList();
        var types = data.getMergeableDataObjectTypeItemList();
        var uuids = data.getMergeableDataObjectUuidItemList();
        this.tables.addAll(data.getMergeableDataObjectEntryList());

        this.direction = tables
                .stream()
                .filter(MergeableDataObjectEntry::hasCustomMap)
                .map(MergeableDataObjectEntry::getCustomMap)
                .map(this::getFirstMapEntry)
                .filter((entry) -> entry.getKey() == keys.indexOf(TABLE_DIRECTION_KEY_NAME) + 1)
                .map(MapEntry::getValue)
                .map(ObjectID::getStringValue)
                .findFirst()
                .orElse(TABLE_DIRECTION_UNKNOWN);
    }

    public String getDirection() {
        return direction;
    }

    public List<MergeableDataObjectEntry> getTables() {
        return tables;
    }

    public List<RuntimeException> getErrors() {
        return errors;
    }

    private MapEntry getFirstMapEntry(MergeableDataObjectMap customMap) {
        return customMap.getMapEntry(0);
    }
}
