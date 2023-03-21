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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.hamburgchimps.apple.notes.liberator.Constants.TABLE_CELLS_KEY_NAME;
import static de.hamburgchimps.apple.notes.liberator.Constants.TABLE_COLUMNS_KEY_NAME;
import static de.hamburgchimps.apple.notes.liberator.Constants.TABLE_DIRECTION_KEY_NAME;
import static de.hamburgchimps.apple.notes.liberator.Constants.TABLE_DIRECTION_UNKNOWN;
import static de.hamburgchimps.apple.notes.liberator.Constants.TABLE_ROOT_IDENTIFIER;
import static de.hamburgchimps.apple.notes.liberator.Constants.TABLE_ROWS_KEY_NAME;
import static de.hamburgchimps.apple.notes.liberator.UserMessages.TABLE_PARSE_ERROR_CANT_FIND_ROOT;
import static de.hamburgchimps.apple.notes.liberator.UserMessages.TABLE_PARSE_ERROR_CANT_PARSE_PROTO;

public class Table implements EmbeddedObjectData {

    private String direction;
    private ProtocolStringList keys;
    private ProtocolStringList types;
    private List<ByteString> uuids;
    private final List<MergeableDataObjectEntry> tables = new ArrayList<>();
    private MergeableDataObjectEntry root;
    private final Map<Integer, Integer> rowIndices = new HashMap<>();
    private final Map<Integer, Integer> columnIndices = new HashMap<>();

    // TODO add members to hold row and column structure
    private final Map<String, Consumer<MergeableDataObjectEntry>> parsers = Map.of(
            TABLE_ROWS_KEY_NAME, this::parseRows,
            TABLE_COLUMNS_KEY_NAME, this::parseColumns,
            TABLE_CELLS_KEY_NAME, this::parseCells
    );
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
        this.root
                .getCustomMap()
                .getMapEntryList()
                .forEach(this::parseMapEntry);
    }

    private void parseMapEntry(MapEntry entry) {
        var key = this.keys.get(entry.getKey());
        var parser = this.parsers.get(this.keys.get(entry.getKey()));

        if (parser == null) {
            Log.tracev("no parser registered for key \"{0}\"", key);
            return;
        }

        parser.accept(this.tables.get(entry.getValue().getObjectIndex()));
    }

    private void parseRows(MergeableDataObjectEntry entry) {
        Log.debug("parsing rows...");
        initIndices(entry, rowIndices);
        mapIndices(entry, rowIndices);
    }

    private void parseColumns(MergeableDataObjectEntry entry) {
        Log.debug("parsing columns...");
        initIndices(entry, columnIndices);
        mapIndices(entry, columnIndices);
    }

    private void parseCells(MergeableDataObjectEntry entry) {
        Log.debug("parsing cells...");

        entry
                .getDictionary()
                .getElementList()
                .forEach((column) -> {
                    var columnUuid = getUuidFromObjectEntry(this.tables.get(column.getKey().getObjectIndex()));
                    var rows = this.tables.get(column.getValue().getObjectIndex());

                    rows
                            .getDictionary()
                            .getElementList()
                            .forEach((row) -> {
                                var rowUuid = getUuidFromObjectEntry(this.tables.get(row.getKey().getObjectIndex()));
                                var cell = this.tables.get(row.getValue().getObjectIndex());

                                // TODO parse cell text
                                if (cell.getNote().getNoteText().contains("2023")) {
                                    Log.debug(cell.getNote().getNoteText());
                                }
                                // TODO parse table representation
                            });
                });
    }

    private void initIndices(MergeableDataObjectEntry entry, Map<Integer, Integer> indices) {
        var attachments = entry
                .getOrderedSet()
                .getOrdering()
                .getArray()
                .getAttachmentList();

        indices.clear();
        indices.putAll(IntStream
                .range(0, attachments.size()).boxed()
                .collect(Collectors.toMap((i) -> this.uuids.indexOf(attachments.get(i).getUuid()), Function.identity())));
    }

    private void mapIndices(MergeableDataObjectEntry entry, Map<Integer, Integer> indices) {
        var elements = entry
                .getOrderedSet()
                .getOrdering()
                .getContents()
                .getElementList();


        elements.forEach((e) -> {
            var key = getUuidFromObjectEntry(this.tables.get(e.getKey().getObjectIndex()));
            var value = getUuidFromObjectEntry(this.tables.get(e.getValue().getObjectIndex()));

            indices.put((int) value, indices.get((int) key));
        });

    }

    private long getUuidFromObjectEntry(MergeableDataObjectEntry entry) {
        return entry
                .getCustomMap()
                .getMapEntry(0)
                .getValue()
                .getUnsignedIntegerValue();
    }
}
