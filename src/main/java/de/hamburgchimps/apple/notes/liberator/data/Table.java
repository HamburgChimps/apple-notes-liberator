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
import java.util.Collections;
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
import static de.hamburgchimps.apple.notes.liberator.Constants.TABLE_ROOT_IDENTIFIER;
import static de.hamburgchimps.apple.notes.liberator.Constants.TABLE_ROWS_KEY_NAME;
import static de.hamburgchimps.apple.notes.liberator.UserMessages.TABLE_PARSE_ERROR_CANT_FIND_ROOT;
import static de.hamburgchimps.apple.notes.liberator.UserMessages.TABLE_PARSE_ERROR_CANT_PARSE_PROTO;
import static de.hamburgchimps.apple.notes.liberator.data.TableDirection.DIRECTION_IDENTIFIER_TO_DIRECTION;
import static de.hamburgchimps.apple.notes.liberator.data.TableDirection.UNKNOWN;

// The only reason I was able to parse an Apple Notes table is because someone else did it first.
// Check out their work:
// https://github.com/threeplanetssoftware/apple_cloud_notes_parser/blob/master/lib/AppleNotesEmbeddedTable.rb
// This implementation is basically a 1-to-1 translation of their parsing code into java.
public class Table implements EmbeddedObjectData {

    private TableDirection direction;
    private ProtocolStringList keys;
    private ProtocolStringList types;
    private List<ByteString> uuids;
    private final List<MergeableDataObjectEntry> tables = new ArrayList<>();
    private MergeableDataObjectEntry root;
    private final Map<Integer, Integer> rowIndices = new HashMap<>();
    private final Map<Integer, Integer> columnIndices = new HashMap<>();
    private int numRows;
    private int numColumns;
    private List<List<String>> data;
    private final EmbeddedObjectDataType type = EmbeddedObjectDataType.TABLE;
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
                .map(DIRECTION_IDENTIFIER_TO_DIRECTION::get)
                .findFirst()
                .orElse(UNKNOWN);

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

        this.reverseRowsIfNeeded();
    }

    public List<List<String>> getData() {
        return data;
    }

    public EmbeddedObjectDataType getType() {
        return type;
    }

    private void parse() {
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
        initIndices(entry, rowIndices);
        this.numRows = this.rowIndices.size();
        mapIndices(entry, rowIndices);
    }

    private void parseColumns(MergeableDataObjectEntry entry) {
        initIndices(entry, columnIndices);
        this.numColumns = this.columnIndices.size();
        mapIndices(entry, columnIndices);
    }

    private void parseCells(MergeableDataObjectEntry entry) {
        initParsed();

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

                                var rowIndex = this.rowIndices.get((int) rowUuid);
                                var columnIndex = this.columnIndices.get((int) columnUuid);

                                this.data
                                        .get(rowIndex)
                                        .set(columnIndex, cell.getNote().getNoteText());
                            });
                });
    }

    // TODO improve and better understand direction handling
    private void reverseRowsIfNeeded() {
        if (this.direction == TableDirection.RIGHT_TO_LEFT) {
            Log.debug("reversing table");
            this.data.forEach(Collections::reverse);
        }
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

    private void initParsed() {
        this.data = IntStream.range(0, this.numRows)
                .mapToObj((i) -> new ArrayList<>(Collections.nCopies(this.numColumns, "")))
                .collect(Collectors.toList());
    }

    private long getUuidFromObjectEntry(MergeableDataObjectEntry entry) {
        return entry
                .getCustomMap()
                .getMapEntry(0)
                .getValue()
                .getUnsignedIntegerValue();
    }
}
