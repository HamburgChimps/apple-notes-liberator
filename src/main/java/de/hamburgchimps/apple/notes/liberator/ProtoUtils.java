package de.hamburgchimps.apple.notes.liberator;

import com.ciofecaforensics.Notestore.MapEntry;
import com.ciofecaforensics.Notestore.MergeableDataObjectEntry;
import com.ciofecaforensics.Notestore.MergeableDataObjectMap;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;

import java.io.IOException;

import static de.hamburgchimps.apple.notes.liberator.ByteUtils.decompress;
import static de.hamburgchimps.apple.notes.liberator.ByteUtils.isCompressed;

public class ProtoUtils {
    public static <T extends GeneratedMessageV3> Result<T, RuntimeException> parseProtoUsingParserFromBytes(Parser<T> parser, byte[] bytes) {
        if (bytes == null) {
            return Result.Error(new RuntimeException("no data to parse"));
        }

        try {
            byte[] decompressed = (isCompressed(bytes))
                    ? decompress(bytes)
                    : bytes;

            return Result.Ok(parser.parseFrom(decompressed));
        } catch (IOException e) {
            return Result.Error(new RuntimeException(e));
        }
    }

    public static MapEntry getFirstMapEntry(MergeableDataObjectMap customMap) {
        return customMap.getMapEntry(0);
    }

    public static long getUuidFromObjectEntry(MergeableDataObjectEntry entry) {
        return entry
                .getCustomMap()
                .getMapEntry(0)
                .getValue()
                .getUnsignedIntegerValue();
    }
}
