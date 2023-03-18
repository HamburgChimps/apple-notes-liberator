package de.hamburgchimps.apple.notes.liberator.data;

import com.ciofecaforensics.Notestore.MapEntry;
import com.ciofecaforensics.Notestore.MergableDataProto;
import com.ciofecaforensics.Notestore.MergeableDataObjectEntry;
import com.ciofecaforensics.Notestore.ObjectID;
import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import de.hamburgchimps.apple.notes.liberator.ProtoUtils;
import de.hamburgchimps.apple.notes.liberator.entity.EmbeddedObject;
import io.quarkus.logging.Log;

import java.util.ArrayList;
import java.util.List;

import static de.hamburgchimps.apple.notes.liberator.Constants.TABLE_DIRECTION_KEY_NAME;
import static de.hamburgchimps.apple.notes.liberator.Constants.TABLE_DIRECTION_UNKNOWN;
import static de.hamburgchimps.apple.notes.liberator.Constants.TABLE_ROOT_IDENTIFIER;
import static de.hamburgchimps.apple.notes.liberator.UserMessages.TABLE_PARSE_ERROR_CANT_FIND_ROOT;
import static de.hamburgchimps.apple.notes.liberator.UserMessages.TABLE_PARSE_ERROR_CANT_PARSE_PROTO;

public class Table implements EmbeddedObjectData {

    private String direction;
    private ProtocolStringList keys;
    private ProtocolStringList types;
    private List<ByteString> uuids;
    private final List<MergeableDataObjectEntry> tables = new ArrayList<>();
    private MergeableDataObjectEntry root;
    private final List<RuntimeException> errors = new ArrayList<>();

    public Table(EmbeddedObject embeddedObject) {
        var result = ProtoUtils.parseProtoUsingParserFromBytes(MergableDataProto.parser(), embeddedObject.zMergeableData);

        if (result.isError()) {
            Log.error(TABLE_PARSE_ERROR_CANT_PARSE_PROTO);
            result.error().printStackTrace();
            this.errors.add(result.error());
            return;
        }

        var proto = result.get();

        var data = proto
                .getMergableDataObject()
                .getMergeableDataObjectData();

        this.keys = data.getMergeableDataObjectKeyItemList();
        this.types = data.getMergeableDataObjectTypeItemList();
        this.uuids = data.getMergeableDataObjectUuidItemList();
        this.tables.addAll(data.getMergeableDataObjectEntryList());

        this.direction = this.tables
                .stream()
                .filter(MergeableDataObjectEntry::hasCustomMap)
                .map(MergeableDataObjectEntry::getCustomMap)
                .map(ProtoUtils::getFirstMapEntry)
                .filter((entry) -> entry.getKey() == keys.indexOf(TABLE_DIRECTION_KEY_NAME) + 1)
                .map(MapEntry::getValue)
                .map(ObjectID::getStringValue)
                .findFirst()
                .orElse(TABLE_DIRECTION_UNKNOWN);

        var potentialRoot = this.tables
                .stream()
                .filter(MergeableDataObjectEntry::hasCustomMap)
                .filter((t) -> types.get(t.getCustomMap().getType()).equals(TABLE_ROOT_IDENTIFIER))
                .findFirst();

        if (potentialRoot.isEmpty()) {
            this.errors.add(new RuntimeException(TABLE_PARSE_ERROR_CANT_FIND_ROOT));
            return;
        }

        this.root = potentialRoot.get();

        this.parse();
    }

    private void parse() {
        Log.debug("parsing table...");
        // TODO: parse root
    }
}
