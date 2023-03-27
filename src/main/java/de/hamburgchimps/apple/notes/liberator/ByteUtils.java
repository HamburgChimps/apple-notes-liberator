package de.hamburgchimps.apple.notes.liberator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import static java.util.zip.GZIPInputStream.GZIP_MAGIC;

public class ByteUtils {
    public static boolean isCompressed(byte[] data) {
        return data[0] == (byte) GZIP_MAGIC && data[1] == (byte) (GZIP_MAGIC >> 8);
    }

    public static byte[] decompress(byte[] data) throws IOException {
        try (var gZipInput = new GZIPInputStream(new ByteArrayInputStream(data));
             var outputBytes = new ByteArrayOutputStream()) {
            gZipInput.transferTo(outputBytes);
            return outputBytes.toByteArray();
        }
    }
}
